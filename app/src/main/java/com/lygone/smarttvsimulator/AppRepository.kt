package com.lygone.smarttvsimulator

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.bluetooth.BluetoothAdapter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.Locale
import android.graphics.Color as AndroidColor

/** Una aplicación lanzable, con su icono ya convertido y un color de acento extraído del icono. */
@Immutable
data class TvApp(
    val name: String,
    val packageName: String,
    val component: ComponentName,
    val icon: ImageBitmap,
    val accent: Color
)

object AppRepository {
    private const val ICON_PX = 192
    private val NeutralAccent = Color(0xFF4F7BD9)

    /**
     * Devuelve las apps lanzables. En TV se prefiere la actividad Leanback de cada paquete;
     * en móvil, la actividad LAUNCHER normal. Debe llamarse fuera del hilo principal.
     */
    fun load(context: Context, preferTv: Boolean): List<TvApp> {
        val pm = context.packageManager
        val categories = if (preferTv) {
            listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)
        } else {
            listOf(Intent.CATEGORY_LAUNCHER, Intent.CATEGORY_LEANBACK_LAUNCHER)
        }
        val seen = HashSet<String>()
        val result = ArrayList<TvApp>()
        for (category in categories) {
            val query = Intent(Intent.ACTION_MAIN).addCategory(category)
            val infos = try {
                pm.queryIntentActivities(query, 0)
            } catch (_: Exception) {
                emptyList()
            }
            for (info in infos) {
                val activity = info.activityInfo ?: continue
                val pkg = activity.packageName
                if (pkg == context.packageName || !seen.add(pkg)) continue
                try {
                    val bitmap = drawableToBitmap(info.loadIcon(pm), ICON_PX)
                    result.add(
                        TvApp(
                            name = info.loadLabel(pm).toString(),
                            packageName = pkg,
                            component = ComponentName(pkg, activity.name),
                            icon = bitmap.asImageBitmap(),
                            accent = dominantColor(bitmap)
                        )
                    )
                } catch (_: Exception) {
                    // Se omite la app cuyo icono o nombre no se pueda leer.
                }
            }
        }
        return result.sortedBy { it.name.lowercase(Locale.getDefault()) }
    }

    /**
     * Abre la app en su propia tarea. Al pulsar Atrás en la app, Android vuelve a esta pantalla;
     * al pulsar Inicio, vuelve aquí si LYGANE es el launcher predeterminado.
     */
    fun launch(context: Context, app: TvApp): Boolean {
        val direct = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(app.component)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        if (tryStart(context, direct)) return true

        val pm = context.packageManager
        val fallback = pm.getLeanbackLaunchIntentForPackage(app.packageName)
            ?: pm.getLaunchIntentForPackage(app.packageName)
        return fallback != null && tryStart(context, fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun tryStart(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent)
        true
    } catch (_: Exception) {
        false
    }

    private fun drawableToBitmap(drawable: Drawable, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        return bitmap
    }

    /** Color medio ponderado por saturación; si el icono es gris/blanco usa un azul neutro. */
    private fun dominantColor(source: Bitmap): Color {
        val side = 16
        val small = Bitmap.createScaledBitmap(source, side, side, true)
        val pixels = IntArray(side * side)
        small.getPixels(pixels, 0, side, 0, 0, side, side)
        val hsv = FloatArray(3)
        var r = 0f
        var g = 0f
        var b = 0f
        var total = 0f
        for (p in pixels) {
            if (AndroidColor.alpha(p) < 128) continue
            AndroidColor.colorToHSV(p, hsv)
            val weight = hsv[1] * hsv[2] + 0.02f
            r += AndroidColor.red(p) * weight
            g += AndroidColor.green(p) * weight
            b += AndroidColor.blue(p) * weight
            total += weight
        }
        if (total == 0f) return NeutralAccent
        AndroidColor.RGBToHSV((r / total).toInt(), (g / total).toInt(), (b / total).toInt(), hsv)
        if (hsv[1] < 0.12f) return NeutralAccent
        hsv[1] = hsv[1].coerceAtLeast(0.5f)
        hsv[2] = hsv[2].coerceIn(0.55f, 0.95f)
        return Color(AndroidColor.HSVToColor(hsv))
    }
}

/** Favoritos y recientes. Mantiene el mismo archivo y clave de favoritos de la versión anterior. */
object Prefs {
    private fun prefs(context: Context) = context.getSharedPreferences("smart_tv", Context.MODE_PRIVATE)

    fun favorites(context: Context): Set<String> =
        prefs(context).getStringSet("favorites", emptySet())?.toSet() ?: emptySet()

    fun saveFavorites(context: Context, favorites: Set<String>) {
        prefs(context).edit().putStringSet("favorites", favorites).apply()
    }

    fun recents(context: Context): List<String> =
        (prefs(context).getString("recents", "") ?: "").split(",").filter { it.isNotBlank() }

    fun pushRecent(context: Context, packageName: String): List<String> {
        val updated = (listOf(packageName) + recents(context).filter { it != packageName }).take(12)
        prefs(context).edit().putString("recents", updated.joinToString(",")).apply()
        return updated
    }
}


/** Accesos directos a los paneles nativos de ajustes del sistema (Wi-Fi, Bluetooth, Sonido, Pantalla). */
object SystemSettingsHelper {
    /** Intenta cambiar el estado real. Si Android/OEM lo bloquea, abre el panel oficial. */
    @Suppress("DEPRECATION")
    fun toggleWifi(context: Context) {
        val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val changed = runCatching {
            manager != null && manager.setWifiEnabled(!manager.isWifiEnabled)
        }.getOrDefault(false)
        if (!changed) openWifiPanel(context)
    }

    @Suppress("DEPRECATION")
    fun toggleBluetooth(context: Context) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        val changed = runCatching {
            adapter != null && if (adapter.isEnabled) adapter.disable() else adapter.enable()
        }.getOrDefault(false)
        if (!changed) openBluetoothSettings(context)
    }

    fun openWifiPanel(context: Context) {
        val intent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Intent(android.provider.Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
        } else {
            Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
        }
        startSafe(context, intent)
    }

    fun openBluetoothSettings(context: Context) {
        startSafe(context, Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS))
    }

    fun openVolumePanel(context: Context) {
        val intent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Intent(android.provider.Settings.Panel.ACTION_VOLUME)
        } else {
            Intent(android.provider.Settings.ACTION_SOUND_SETTINGS)
        }
        startSafe(context, intent)
    }

    fun openSoundSettings(context: Context) {
        startSafe(context, Intent(android.provider.Settings.ACTION_SOUND_SETTINGS))
    }

    fun openDisplaySettings(context: Context) {
        startSafe(context, Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS))
    }

    fun openAllSettings(context: Context) {
        startSafe(context, Intent(android.provider.Settings.ACTION_SETTINGS))
    }

    fun openNotificationListenerSettings(context: Context) {
        startSafe(context, Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }

    private fun startSafe(context: Context, intent: Intent) {
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {
            runCatching {
                context.startActivity(
                    Intent(android.provider.Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }
}
