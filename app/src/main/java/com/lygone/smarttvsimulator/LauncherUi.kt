@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.lygone.smarttvsimulator

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Newspaper
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ───────────────────────────── Paleta Hisense VIDAA + Sunset ─────────────────────────────

private val Ink = Color(0xFF070B14)
private val SurfaceGlass = Color(0xBD0B101D)
private val SurfaceCard = Color(0xE6141C2E)
private val TextHi = Color(0xFFFFFFFF)
private val TextLo = Color(0xFFD0D8EA)
private val AngelCyan = Color(0xFF4FA8FF)
private val VidaaTeal = Color(0xFF00D4C5)
private val Gold = Color(0xFFFFD166)

private enum class NavDestination(val label: String, val icon: ImageVector) {
    Home("Inicio", Icons.Rounded.Home),
    Billboard("Cartelera", Icons.Rounded.LiveTv),
    Apps("Apps", Icons.Rounded.Apps),
    Favorites("Favoritos", Icons.Rounded.Star),
    Search("Buscar", Icons.Rounded.Search),
    Settings("Ajustes", Icons.Rounded.Settings)
}

// ───────────────────────────── Pantalla principal ─────────────────────────────

@Composable
fun LauncherApp(homeSignal: Int, packageSignal: Int, resumeSignal: Int) {
    val context = LocalContext.current
    val isTv = remember { isTelevision(context) }
    val dpad = remember { isTv || !context.packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN) }

    var apps by remember { mutableStateOf<List<TvApp>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }
    var reload by remember { mutableIntStateOf(0) }
    var favorites by remember { mutableStateOf(Prefs.favorites(context)) }
    var recents by remember { mutableStateOf(Prefs.recents(context)) }
    var activeSection by remember { mutableStateOf(NavDestination.Home) }
    var search by remember { mutableStateOf("") }
    var showSettingsStation by remember { mutableStateOf(false) }
    val spotlight = remember { mutableStateOf<TvApp?>(null) }

    // Estados en vivo: Clima, Noticias de Perú y Cartelera (Películas, Series y Fútbol)
    var weather by remember { mutableStateOf<WeatherInfo?>(null) }
    var peruNews by remember { mutableStateOf<List<String>>(emptyList()) }
    var billboardEvents by remember { mutableStateOf(LiveInfoRepository.getDefaultBillboardEvents()) }

    // Estado del minirreproductor multimedia activo
    val nowPlaying by MediaMonitorService.nowPlaying.collectAsState()
    val hasMediaPermission = remember(resumeSignal, showSettingsStation) {
        MediaMonitorService.isPermissionGranted(context)
    }

    val now = rememberNow()
    val scope = rememberCoroutineScope()
    val firstFocus = remember { FocusRequester() }
    val searchFocus = remember { FocusRequester() }

    LaunchedEffect(packageSignal, reload) {
        apps = withContext(Dispatchers.Default) { AppRepository.load(context, isTv) }
        loaded = true
    }

    // Sondeo continuo cada 2.5 segundos de sesiones multimedia activas (YouTube, YT Music, Xuper, etc.)
    LaunchedEffect(Unit) {
        while (true) {
            MediaMonitorService.refreshSessions(context)
            delay(2500L)
        }
    }

    // Carga y actualización periódica del clima (cada 30 min), noticias y cartelera (cada 15 min)
    LaunchedEffect(reload) {
        while (true) {
            val w = withContext(Dispatchers.IO) { LiveInfoRepository.fetchWeather() }
            if (w != null) weather = w
            delay(30 * 60 * 1000L)
        }
    }
    LaunchedEffect(reload) {
        while (true) {
            val news = withContext(Dispatchers.IO) { LiveInfoRepository.fetchPeruNews(limit = 10) }
            if (news.isNotEmpty()) peruNews = news
            val events = withContext(Dispatchers.IO) { LiveInfoRepository.fetchBillboardEvents() }
            if (events.isNotEmpty()) billboardEvents = events
            delay(15 * 60 * 1000L)
        }
    }

    val byPackage = remember(apps) { apps.associateBy { it.packageName } }
    val recentApps = remember(apps, recents) { recents.mapNotNull { byPackage[it] } }
    val lastUsedApp = recentApps.firstOrNull() ?: apps.firstOrNull()
    val favoriteApps = remember(apps, favorites) { apps.filter { it.packageName in favorites } }

    val homeCarouselApps = remember(apps, recentApps, favoriteApps) {
        val ordered = LinkedHashMap<String, TvApp>()
        recentApps.forEach { ordered[it.packageName] = it }
        favoriteApps.forEach { ordered[it.packageName] = it }
        apps.forEach { ordered[it.packageName] = it }
        ordered.values.toList()
    }

    val query = search.trim()
    val searchResults = remember(apps, query) {
        if (query.isEmpty()) apps else apps.filter { it.name.contains(query, ignoreCase = true) }
    }

    val openApp: (TvApp) -> Unit = { app ->
        if (AppRepository.launch(context, app)) {
            recents = Prefs.pushRecent(context, app.packageName)
        }
    }
    val toggleFavorite: (TvApp) -> Unit = { app ->
        favorites = if (app.packageName in favorites) favorites - app.packageName else favorites + app.packageName
        Prefs.saveFavorites(context, favorites)
    }
    val onFocused: (TvApp) -> Unit = { spotlight.value = it }

    fun goHome() {
        activeSection = NavDestination.Home
        search = ""
        showSettingsStation = false
        scope.launch {
            if (dpad) {
                delay(120)
                runCatching { firstFocus.requestFocus() }
            }
        }
    }

    LaunchedEffect(homeSignal) { if (homeSignal > 0) goHome() }
    LaunchedEffect(loaded) {
        if (loaded && dpad) {
            delay(220)
            runCatching { firstFocus.requestFocus() }
        }
    }
    LaunchedEffect(activeSection) {
        if (activeSection == NavDestination.Search) {
            delay(120)
            runCatching { searchFocus.requestFocus() }
        }
    }

    BackHandler(enabled = showSettingsStation || activeSection != NavDestination.Home) {
        if (showSettingsStation) {
            showSettingsStation = false
        } else {
            goHome()
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AngelCyan, background = Ink, surface = SurfaceCard,
            onBackground = TextHi, onSurface = TextHi
        )
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().background(Ink)) {
            val wide = maxWidth >= 720.dp
            val pad = if (wide) 52.dp else 20.dp
            val cardW = if (wide) 196.dp else 132.dp
            val cardH = if (wide) 114.dp else 84.dp

            ScenicWallpaperLayer(spotlight)

            Column(
                Modifier.fillMaxSize()
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                // 1. Barra superior: LYGANE TV + Accesos rápidos (Cartelera + Estación de Ajustes) + Perfil ANGEL + Clima + Reloj
                TopStatusHeader(
                    now = now,
                    weather = weather,
                    wide = wide,
                    pad = pad,
                    billboardActive = activeSection == NavDestination.Billboard,
                    settingsActive = showSettingsStation,
                    onOpenBillboard = {
                        activeSection = if (activeSection == NavDestination.Billboard) {
                            NavDestination.Home
                        } else {
                            NavDestination.Billboard
                        }
                    },
                    onOpenSettings = { showSettingsStation = !showSettingsStation }
                )

                // 2. Contenido central
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when {
                        !loaded -> Text(
                            "Cargando aplicaciones…",
                            color = TextHi,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        activeSection == NavDestination.Home -> {
                            VidaaHomeStage(
                                wide = wide,
                                pad = pad,
                                cardW = cardW,
                                cardH = cardH,
                                lastUsedApp = lastUsedApp,
                                hasRecents = recentApps.isNotEmpty(),
                                focusedApp = spotlight.value ?: lastUsedApp,
                                carouselApps = homeCarouselApps,
                                favorites = favorites,
                                peruNews = peruNews,
                                nowPlaying = nowPlaying,
                                firstFocus = firstFocus,
                                onFocused = onFocused,
                                onOpen = openApp,
                                onToggleFavorite = toggleFavorite,
                                onOpenMediaSource = { MediaMonitorService.openSourceApp(context) },
                                onOpenBillboard = { activeSection = NavDestination.Billboard }
                            )
                        }

                        activeSection == NavDestination.Billboard -> {
                            BillboardSectionOverlay(
                                events = billboardEvents,
                                wide = wide,
                                pad = pad,
                                onRefresh = { reload++ },
                                onClose = { goHome() }
                            )
                        }

                        activeSection == NavDestination.Apps -> {
                            AppGridOverlay(
                                title = "Todas las aplicaciones (${apps.size})",
                                apps = apps,
                                emptyText = "No se encontraron aplicaciones compatibles.",
                                cardW = cardW,
                                cardH = cardH,
                                wide = wide,
                                pad = pad,
                                favorites = favorites,
                                focusFirst = firstFocus,
                                onFocused = onFocused,
                                onOpen = openApp,
                                onToggleFavorite = toggleFavorite
                            )
                        }

                        activeSection == NavDestination.Favorites -> {
                            AppGridOverlay(
                                title = "Favoritos",
                                apps = favoriteApps,
                                emptyText = "Aún no tienes favoritos.\nMantén pulsado OK sobre una app para fijarla aquí.",
                                cardW = cardW,
                                cardH = cardH,
                                wide = wide,
                                pad = pad,
                                favorites = favorites,
                                focusFirst = firstFocus,
                                onFocused = onFocused,
                                onOpen = openApp,
                                onToggleFavorite = toggleFavorite
                            )
                        }

                        activeSection == NavDestination.Search -> {
                            Column(Modifier.fillMaxSize()) {
                                Box(Modifier.padding(horizontal = pad, vertical = 8.dp)) {
                                    SearchField(
                                        value = search,
                                        onChange = { search = it },
                                        onClose = { goHome() },
                                        focusRequester = searchFocus,
                                        modifier = Modifier.fillMaxWidth(if (wide) 0.55f else 1f)
                                    )
                                }
                                AppGridOverlay(
                                    title = if (query.isEmpty()) "Explorar aplicaciones" else "Resultados para “$query”",
                                    apps = searchResults,
                                    emptyText = "No se encontró ninguna aplicación.",
                                    cardW = cardW,
                                    cardH = cardH,
                                    wide = wide,
                                    pad = pad,
                                    favorites = favorites,
                                    focusFirst = null,
                                    onFocused = onFocused,
                                    onOpen = openApp,
                                    onToggleFavorite = toggleFavorite
                                )
                            }
                        }

                        else -> Unit
                    }
                }

                // 3. Dock inferior tipo píldora (VIDAA + Cartelera + Ajustes)
                BottomVidaaDock(
                    selected = if (showSettingsStation) NavDestination.Settings else activeSection,
                    wide = wide,
                    pad = pad,
                    onSelect = { dest ->
                        if (dest == NavDestination.Settings) {
                            showSettingsStation = true
                        } else {
                            showSettingsStation = false
                            activeSection = dest
                            if (dest != NavDestination.Search) search = ""
                        }
                    }
                )
            }
        }

        // Estación Desplegable de Ajustes Rápidos (Wi-Fi, Bluetooth, Sonido/Volumen, Pantalla DeX, Permiso Multimedia)
        if (showSettingsStation) {
            QuickSettingsStationDialog(
                appCount = apps.size,
                hasMediaPermission = hasMediaPermission,
                onWifi = { SystemSettingsHelper.toggleWifi(context) },
                onBluetooth = { SystemSettingsHelper.toggleBluetooth(context) },
                onVolume = { SystemSettingsHelper.openVolumePanel(context) },
                onSoundSettings = { SystemSettingsHelper.openSoundSettings(context) },
                onDisplay = { SystemSettingsHelper.openDisplaySettings(context) },
                onSystemSettings = { SystemSettingsHelper.openAllSettings(context) },
                onGrantMediaPermission = { SystemSettingsHelper.openNotificationListenerSettings(context) },
                onRefreshData = { showSettingsStation = false; reload++ },
                onClose = { showSettingsStation = false }
            )
        }
    }
}

// ───────────────────────────── Fondo Escénico Limpio ─────────────────────────────

@Composable
private fun ScenicWallpaperLayer(spotlight: State<TvApp?>) {
    val accent = spotlight.value?.accent ?: AngelCyan
    val animatedAccent by animateColorAsState(accent, tween(650), label = "accent-glow")

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.launcher_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.0f to Color(0x40060A14),
                    0.42f to Color(0x12060A14),
                    0.70f to Color(0x80060A14),
                    1.0f to Color(0xE0050811)
                )
            )
        )

        Box(
            Modifier.fillMaxSize().drawBehind {
                drawRect(
                    Brush.radialGradient(
                        0f to animatedAccent.copy(alpha = 0.22f),
                        1f to Color.Transparent,
                        center = Offset(size.width * 0.22f, size.height * 0.78f),
                        radius = size.width * 0.55f
                    )
                )
            }
        )
    }
}

// ───────────────────────────── Header Superior: Marca + Botones Rápidos + Perfil + Clima + Reloj ─────────────────────────────

@Composable
private fun TopStatusHeader(
    now: State<Long>,
    weather: WeatherInfo?,
    wide: Boolean,
    pad: Dp,
    billboardActive: Boolean,
    settingsActive: Boolean,
    onOpenBillboard: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val ms = now.value

    Row(
        Modifier.fillMaxWidth().padding(horizontal = pad, vertical = if (wide) 14.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Marca discreta LYGANE TV
        Row(
            Modifier.clip(RoundedCornerShape(50))
                .background(SurfaceGlass)
                .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(50))
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.size(9.dp).clip(CircleShape).background(VidaaTeal))
            Text(
                "LYGANE TV",
                color = TextHi,
                fontSize = if (wide) 13.sp else 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        // Botón rápido: Cartelera (Películas, Series y Fútbol)
        TopActionPill(
            icon = Icons.Rounded.LiveTv,
            label = if (wide) "Cartelera y Fútbol" else "Cartelera",
            active = billboardActive,
            accent = Gold,
            wide = wide,
            onClick = onOpenBillboard
        )

        // Botón rápido: Estación de Ajustes (Wi-Fi, Bluetooth, Sonido)
        TopActionPill(
            icon = Icons.Rounded.Settings,
            label = if (wide) "Estación de Ajustes" else "Ajustes",
            active = settingsActive,
            accent = AngelCyan,
            wide = wide,
            onClick = onOpenSettings
        )

        Spacer(Modifier.weight(1f))

        // Bloque derecho: Perfil ANGEL + CLIMA + RELOJ GRANDE
        Row(
            Modifier.clip(RoundedCornerShape(22.dp))
                .background(SurfaceGlass)
                .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
                .padding(horizontal = if (wide) 18.dp else 12.dp, vertical = if (wide) 9.dp else 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (wide) 14.dp else 9.dp)
        ) {
            Box(
                Modifier.size(if (wide) 38.dp else 30.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(AngelCyan, VidaaTeal))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Ink,
                    modifier = Modifier.size(if (wide) 22.dp else 18.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        "ANGEL",
                        color = TextHi,
                        fontSize = if (wide) 14.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Icon(
                        Icons.Rounded.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = VidaaTeal,
                        modifier = Modifier.size(if (wide) 16.dp else 13.dp)
                    )
                }
                Text(
                    formatDateShort(ms),
                    color = TextLo,
                    fontSize = if (wide) 11.sp else 10.sp
                )
            }

            if (weather != null) {
                Box(
                    Modifier.width(1.dp).height(if (wide) 34.dp else 26.dp)
                        .background(Color.White.copy(alpha = 0.20f))
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = weatherIcon(weather),
                        contentDescription = weather.conditionText,
                        tint = Gold,
                        modifier = Modifier.size(if (wide) 26.dp else 20.dp)
                    )
                    Column {
                        Text(
                            text = "${weather.tempCelsius}°C",
                            color = TextHi,
                            fontSize = if (wide) 20.sp else 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (wide) {
                            Text(
                                text = weather.conditionText,
                                color = TextLo,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Box(
                Modifier.width(1.dp).height(if (wide) 34.dp else 26.dp)
                    .background(Color.White.copy(alpha = 0.22f))
            )

            Text(
                text = formatTime(context, ms),
                color = TextHi,
                fontSize = if (wide) 40.sp else 26.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0x80000000),
                        offset = Offset(0f, 3f),
                        blurRadius = 8f
                    )
                )
            )
        }
    }
}

@Composable
private fun TopActionPill(
    icon: ImageVector,
    label: String,
    active: Boolean,
    accent: Color,
    wide: Boolean,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val shape = RoundedCornerShape(50)
    val bg = when {
        focused -> Color.White
        active -> accent.copy(alpha = 0.28f)
        else -> SurfaceGlass
    }
    val fg = if (focused) Ink else TextHi

    Row(
        Modifier.clip(shape)
            .background(bg)
            .border(
                width = if (focused || active) 1.5.dp else 1.dp,
                color = if (focused) Color.White else if (active) accent else Color.White.copy(alpha = 0.16f),
                shape = shape
            )
            .onKeyEvent { e ->
                if (e.type == KeyEventType.KeyUp && (e.key == Key.Enter || e.key == Key.DirectionCenter)) {
                    onClick()
                    true
                } else {
                    false
                }
            }
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = if (wide) 14.dp else 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (focused) Ink else accent,
            modifier = Modifier.size(if (wide) 17.dp else 15.dp)
        )
        Text(
            text = label,
            color = fg,
            fontSize = if (wide) 12.sp else 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

private fun weatherIcon(info: WeatherInfo): ImageVector = when (info.code) {
    0 -> if (info.isDay) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay
    1, 2, 3, 45, 48 -> Icons.Rounded.Cloud
    51, 53, 55, 61, 63, 65, 80, 81, 82 -> Icons.Rounded.WaterDrop
    95, 96, 99 -> Icons.Rounded.Thunderstorm
    else -> Icons.Rounded.Cloud
}

// ───────────────────────────── Escenario Principal + Minirreproductor + Noticias ─────────────────────────────

@Composable
private fun VidaaHomeStage(
    wide: Boolean,
    pad: Dp,
    cardW: Dp,
    cardH: Dp,
    lastUsedApp: TvApp?,
    hasRecents: Boolean,
    focusedApp: TvApp?,
    carouselApps: List<TvApp>,
    favorites: Set<String>,
    peruNews: List<String>,
    nowPlaying: NowPlayingState,
    firstFocus: FocusRequester,
    onFocused: (TvApp) -> Unit,
    onOpen: (TvApp) -> Unit,
    onToggleFavorite: (TvApp) -> Unit,
    onOpenMediaSource: () -> Unit,
    onOpenBillboard: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Fila de Saludo "ANGEL Estamos listos para usar" + Minirreproductor Multimedia + Última App Usada
        Row(
            Modifier.fillMaxWidth().padding(horizontal = pad),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextHi, fontWeight = FontWeight.ExtraBold)) {
                            append("¡Hola, ")
                        }
                        withStyle(SpanStyle(color = AngelCyan, fontWeight = FontWeight.ExtraBold)) {
                            append("ANGEL")
                        }
                        withStyle(SpanStyle(color = TextHi, fontWeight = FontWeight.ExtraBold)) {
                            append("!")
                        }
                    },
                    fontSize = if (wide) 48.sp else 28.sp,
                    lineHeight = if (wide) 52.sp else 32.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0x99000000),
                            offset = Offset(0f, 4f),
                            blurRadius = 12f
                        )
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Estamos listos para usar",
                    color = TextHi.copy(alpha = 0.94f),
                    fontSize = if (wide) 22.sp else 14.sp,
                    fontWeight = FontWeight.Medium,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0x88000000),
                            offset = Offset(0f, 2f),
                            blurRadius = 8f
                        )
                    )
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier.width(if (wide) 76.dp else 52.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Brush.horizontalGradient(listOf(AngelCyan, VidaaTeal)))
                )
            }

            // Minirreproductor Multimedia Dinámico (aparece solo cuando suena música o video)
            AnimatedVisibility(
                visible = nowPlaying.isActive,
                enter = fadeIn(tween(260)) + scaleIn(initialScale = 0.92f),
                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.92f)
            ) {
                MiniMediaPlayerCard(
                    state = nowPlaying,
                    wide = wide,
                    onPrevious = { MediaMonitorService.skipPrevious() },
                    onTogglePlay = { MediaMonitorService.togglePlayPause() },
                    onNext = { MediaMonitorService.skipNext() },
                    onOpenApp = onOpenMediaSource
                )
            }

            if (lastUsedApp != null) {
                LastUsedAppBadge(
                    app = lastUsedApp,
                    isRecent = hasRecents,
                    wide = wide,
                    onOpen = { onOpen(lastUsedApp) }
                )
            }
        }

        Spacer(Modifier.height(if (wide) 14.dp else 10.dp))

        // Cintillo minimalista de Últimas Noticias de Perú + acceso directo a Cartelera
        if (peruNews.isNotEmpty()) {
            PeruNewsTicker(
                headlines = peruNews,
                wide = wide,
                pad = pad,
                onOpenBillboard = onOpenBillboard
            )
            Spacer(Modifier.height(if (wide) 12.dp else 8.dp))
        }

        // Nombre de la app enfocada
        if (focusedApp != null) {
            Row(
                Modifier.padding(horizontal = pad),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = focusedApp.name,
                    color = TextHi,
                    fontSize = if (wide) 15.sp else 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "•  OK para abrir  •  Mantén OK para favorito",
                    color = TextLo.copy(alpha = 0.85f),
                    fontSize = if (wide) 12.sp else 11.sp
                )
            }
        }

        // Carrusel horizontal de aplicaciones
        LazyRow(
            contentPadding = PaddingValues(horizontal = pad, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(if (wide) 18.dp else 12.dp)
        ) {
            itemsIndexed(carouselApps, key = { _, a -> a.packageName }) { index, app ->
                val isLastUsed = index == 0 && hasRecents
                VidaaAppCard(
                    app = app,
                    width = cardW,
                    height = cardH,
                    wide = wide,
                    isFavorite = app.packageName in favorites,
                    isLastUsed = isLastUsed,
                    focusRequester = if (index == 0) firstFocus else null,
                    onFocused = onFocused,
                    onOpen = onOpen,
                    onToggleFavorite = onToggleFavorite
                )
            }
        }
    }
}

// ───────────────────────────── Minirreproductor Multimedia (Sonido / Video activo) ─────────────────────────────

@Composable
private fun MiniMediaPlayerCard(
    state: NowPlayingState,
    wide: Boolean,
    onPrevious: () -> Unit,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onOpenApp: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val cardSource = remember { MutableInteractionSource() }
    val cardFocused by cardSource.collectIsFocusedAsState()

    Row(
        Modifier.widthIn(min = if (wide) 320.dp else 230.dp, max = if (wide) 400.dp else 280.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xE6101B2E), Color(0xE60B1322))
                )
            )
            .border(
                width = if (cardFocused) 2.dp else 1.2.dp,
                color = if (cardFocused) Color.White else VidaaTeal.copy(alpha = 0.65f),
                shape = shape
            )
            .padding(horizontal = if (wide) 14.dp else 10.dp, vertical = if (wide) 10.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Carátula del video/canción o ecualizador
        Box(
            Modifier.size(if (wide) 48.dp else 38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AngelCyan.copy(alpha = 0.22f))
                .clickable(interactionSource = cardSource, indication = null, onClick = onOpenApp),
            contentAlignment = Alignment.Center
        ) {
            if (state.albumArt != null) {
                Image(
                    bitmap = state.albumArt.asImageBitmap(),
                    contentDescription = state.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = VidaaTeal,
                    modifier = Modifier.size(if (wide) 26.dp else 20.dp)
                )
            }
        }

        // Título, artista y etiqueta SONIDO EN VIVO
        Column(
            Modifier.weight(1f)
                .clickable(onClick = onOpenApp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(VidaaTeal))
                Text(
                    text = if (state.appName.isNotBlank()) state.appName.uppercase() else "SONIDO ACTIVO",
                    color = VidaaTeal,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    maxLines = 1
                )
            }
            Text(
                text = state.title,
                color = TextHi,
                fontSize = if (wide) 13.sp else 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = state.artist,
                color = TextLo.copy(alpha = 0.85f),
                fontSize = if (wide) 11.sp else 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Controles multimedia navegables con clic o Enter
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MediaControlButton(
                icon = Icons.Rounded.SkipPrevious,
                desc = "Anterior",
                primary = false,
                onClick = onPrevious
            )
            MediaControlButton(
                icon = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                desc = if (state.isPlaying) "Pausar" else "Reproducir",
                primary = true,
                onClick = onTogglePlay
            )
            MediaControlButton(
                icon = Icons.Rounded.SkipNext,
                desc = "Siguiente",
                primary = false,
                onClick = onNext
            )
        }
    }
}

@Composable
private fun MediaControlButton(
    icon: ImageVector,
    desc: String,
    primary: Boolean,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val bg = when {
        focused -> Color.White
        primary -> AngelCyan
        else -> Color.White.copy(alpha = 0.12f)
    }
    val tint = if (focused || primary) Ink else TextHi

    Box(
        Modifier.size(if (primary) 34.dp else 28.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = desc, tint = tint, modifier = Modifier.size(if (primary) 20.dp else 16.dp))
    }
}

/** Barra ultrafina translúcida que rota los titulares de Perú cada 7 segundos + acceso rápido a Cartelera. */
@Composable
private fun PeruNewsTicker(
    headlines: List<String>,
    wide: Boolean,
    pad: Dp,
    onOpenBillboard: () -> Unit
) {
    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(headlines) {
        index = 0
        while (headlines.size > 1) {
            delay(7000L)
            index = (index + 1) % headlines.size
        }
    }

    val currentHeadline = headlines.getOrElse(index) { "" }
    val shape = RoundedCornerShape(12.dp)

    Row(
        Modifier.fillMaxWidth()
            .padding(horizontal = pad)
            .clip(shape)
            .background(Color(0x99090E1A))
            .border(1.dp, Color.White.copy(alpha = 0.12f), shape)
            .padding(horizontal = if (wide) 14.dp else 10.dp, vertical = if (wide) 7.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            Modifier.clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFD92B3A))
                .padding(horizontal = 7.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Rounded.Newspaper,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(if (wide) 13.dp else 11.dp)
            )
            Text(
                text = "PERÚ",
                color = Color.White,
                fontSize = if (wide) 10.sp else 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        AnimatedContent(
            targetState = currentHeadline,
            transitionSpec = {
                (slideInVertically { it } + fadeIn(tween(350))) togetherWith
                    (slideOutVertically { -it } + fadeOut(tween(300)))
            },
            label = "news-ticker",
            modifier = Modifier.weight(1f)
        ) { headline ->
            Text(
                text = headline,
                color = TextHi.copy(alpha = 0.95f),
                fontSize = if (wide) 13.sp else 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Acceso rápido a próximos partidos, películas y series
        Row(
            Modifier.clip(RoundedCornerShape(8.dp))
                .background(Gold.copy(alpha = 0.22f))
                .clickable(onClick = onOpenBillboard)
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Rounded.SportsSoccer, null, tint = Gold, modifier = Modifier.size(12.dp))
            Text(
                text = "PRÓXIMOS EVENTOS",
                color = Gold,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp
            )
        }
    }
}

// ───────────────────────────── Apartado de Cartelera: Películas, Series y Fútbol ─────────────────────────────

@Composable
private fun BillboardSectionOverlay(
    events: List<UpcomingEvent>,
    wide: Boolean,
    pad: Dp,
    onRefresh: () -> Unit,
    onClose: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(EventCategory.ALL) }
    val filtered = remember(events, selectedFilter) {
        if (selectedFilter == EventCategory.ALL) events else events.filter { it.category == selectedFilter }
    }

    Column(
        Modifier.fillMaxSize()
            .padding(horizontal = pad, vertical = 8.dp)
    ) {
        // Encabezado + Filtros (Todo / Fútbol / Películas / Series)
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Cartelera, Series y Fútbol",
                    color = TextHi,
                    fontSize = if (wide) 24.sp else 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Próximos partidos, estrenos de películas y lanzamientos de series",
                    color = TextLo,
                    fontSize = if (wide) 13.sp else 11.sp
                )
            }

            EventCategory.values().forEach { cat ->
                val isSelected = selectedFilter == cat
                val source = remember { MutableInteractionSource() }
                val focused by source.collectIsFocusedAsState()
                val bg = when {
                    focused -> Color.White
                    isSelected -> AngelCyan
                    else -> SurfaceGlass
                }
                val fg = if (focused || isSelected) Ink else TextHi

                Box(
                    Modifier.clip(RoundedCornerShape(50))
                        .background(bg)
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(50))
                        .clickable(interactionSource = source, indication = null) { selectedFilter = cat }
                        .padding(horizontal = if (wide) 16.dp else 10.dp, vertical = 7.dp)
                ) {
                    Text(cat.label, color = fg, fontSize = if (wide) 13.sp else 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                Modifier.size(36.dp)
                    .clip(CircleShape)
                    .background(SurfaceGlass)
                    .clickable(onClick = onRefresh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar cartelera", tint = VidaaTeal)
            }

            Box(
                Modifier.size(36.dp)
                    .clip(CircleShape)
                    .background(SurfaceGlass)
                    .clickable(onClick = onClose),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Volver a Inicio", tint = TextHi)
            }
        }

        Spacer(Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = if (wide) 290.dp else 220.dp),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filtered) { event ->
                BillboardEventCard(event = event, wide = wide)
            }
        }
    }
}

@Composable
private fun BillboardEventCard(event: UpcomingEvent, wide: Boolean) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val accent = Color(event.accentHex)
    val shape = RoundedCornerShape(22.dp)
    val icon = when (event.category) {
        EventCategory.FOOTBALL -> Icons.Rounded.SportsSoccer
        EventCategory.MOVIES -> Icons.Rounded.Movie
        EventCategory.SERIES -> Icons.Rounded.Tv
        EventCategory.ALL -> Icons.Rounded.LiveTv
    }
    var poster by remember(event.imageUrl) { mutableStateOf<android.graphics.Bitmap?>(null) }
    val posterUrl = event.imageUrl ?: when (event.category) {
        EventCategory.FOOTBALL -> "https://images.unsplash.com/photo-1522778119026-d647f0596c20?w=900&q=82"
        EventCategory.MOVIES -> "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=900&q=82"
        EventCategory.SERIES -> "https://images.unsplash.com/photo-1522869635100-9f4c5e86aa37?w=900&q=82"
        EventCategory.ALL -> "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=900&q=82"
    }
    LaunchedEffect(posterUrl) {
        poster = withContext(Dispatchers.IO) { LiveInfoRepository.fetchImage(posterUrl) }
    }
    val scale by animateFloatAsState(if (focused) 1.025f else 1f, spring(dampingRatio = 0.82f), label = "billboard-card")

    Column(
        Modifier.fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(SurfaceCard)
            .border(1.dp, accent.copy(alpha = if (focused) 0.85f else 0.30f), shape)
            .then(if (focused) Modifier.glow(accent, 0.34f) else Modifier)
            .clickable(interactionSource = source, indication = null, onClick = {})
    ) {
        Box(Modifier.fillMaxWidth().height(if (wide) 156.dp else 126.dp)) {
            if (poster != null) {
                Image(
                    bitmap = poster!!.asImageBitmap(),
                    contentDescription = event.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(accent.copy(alpha = 0.52f), Color(0xFF101827)))))
                Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.75f), modifier = Modifier.align(Alignment.Center).size(54.dp))
            }
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.02f), Color.Black.copy(alpha = 0.88f)))))
            Text(
                text = event.badge,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(14.dp)
                    .clip(RoundedCornerShape(50)).background(accent.copy(alpha = 0.92f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(event.title, color = TextHi, fontSize = if (wide) 16.sp else 14.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(event.subtitle, color = TextLo.copy(alpha = 0.88f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(Modifier.fillMaxWidth().padding(top = 3.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(event.schedule, color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(event.platform, color = VidaaTeal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun LastUsedAppBadge(
    app: TvApp,
    isRecent: Boolean,
    wide: Boolean,
    onOpen: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(if (focused) 1.05f else 1f, label = "resume-scale")
    val shape = RoundedCornerShape(18.dp)

    Row(
        Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(SurfaceGlass)
            .border(
                width = if (focused) 2.5.dp else 1.dp,
                color = if (focused) Color.White else app.accent.copy(alpha = 0.65f),
                shape = shape
            )
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onOpen)
            .padding(horizontal = if (wide) 16.dp else 12.dp, vertical = if (wide) 11.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(if (wide) 44.dp else 34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(app.accent.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Image(app.icon, contentDescription = app.name, modifier = Modifier.size(if (wide) 32.dp else 24.dp))
        }

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Rounded.History, contentDescription = null, tint = VidaaTeal, modifier = Modifier.size(13.dp))
                Text(
                    text = if (isRecent) "ÚLTIMA APP USADA" else "LISTA PARA ABRIR",
                    color = VidaaTeal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = app.name,
                color = TextHi,
                fontSize = if (wide) 15.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = if (wide) 180.dp else 110.dp)
            )
        }

        if (wide) {
            Box(
                Modifier.size(32.dp).clip(CircleShape).background(if (focused) Color.White else AngelCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = "Abrir", tint = Ink, modifier = Modifier.size(19.dp))
            }
        }
    }
}

// ───────────────────────────── Tarjetas Panorámicas Estilo VIDAA ─────────────────────────────

@Composable
private fun VidaaAppCard(
    app: TvApp,
    width: Dp,
    height: Dp,
    wide: Boolean,
    isFavorite: Boolean,
    isLastUsed: Boolean,
    focusRequester: FocusRequester?,
    onFocused: (TvApp) -> Unit,
    onOpen: (TvApp) -> Unit,
    onToggleFavorite: (TvApp) -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        if (focused) 1.09f else 1f,
        spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "card-scale"
    )
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(16.dp)
    val currentOnFocused by rememberUpdatedState(onFocused)

    LaunchedEffect(focused) { if (focused) currentOnFocused(app) }

    Column(
        Modifier.width(width).onKeyEvent { e ->
            if (e.type == KeyEventType.KeyUp && e.key == Key.Menu) {
                onToggleFavorite(app)
                true
            } else {
                false
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(width, height)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .then(if (focused) Modifier.glow(app.accent, 0.65f) else Modifier)
                .clip(shape)
                .background(SurfaceCard)
                .background(
                    Brush.linearGradient(
                        listOf(
                            app.accent.copy(alpha = if (focused) 0.55f else 0.30f),
                            Color(0xCC0D1322)
                        )
                    )
                )
                .border(
                    width = if (focused) 1.5.dp else 1.dp,
                    color = app.accent.copy(alpha = if (focused) 0.92f else 0.18f),
                    shape = shape
                )
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                .combinedClickable(
                    interactionSource = source,
                    indication = null,
                    role = Role.Button,
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggleFavorite(app)
                    },
                    onClick = { onOpen(app) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                app.icon,
                contentDescription = app.name,
                modifier = Modifier.size(height * 0.56f)
            )

            if (isLastUsed) {
                Box(
                    Modifier.align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(VidaaTeal)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("RECIENTE", color = Ink, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            if (isFavorite) {
                Box(
                    Modifier.align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xB3000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Star, null, tint = Gold, modifier = Modifier.size(14.dp))
                }
            }

            if (focused) {
                Box(
                    Modifier.align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xCC060913))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.name,
                        color = TextHi,
                        fontSize = if (wide) 12.sp else 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ───────────────────────────── Cuadrícula para Apps / Favoritos / Búsqueda ─────────────────────────────

@Composable
private fun AppGridOverlay(
    title: String,
    apps: List<TvApp>,
    emptyText: String,
    cardW: Dp,
    cardH: Dp,
    wide: Boolean,
    pad: Dp,
    favorites: Set<String>,
    focusFirst: FocusRequester?,
    onFocused: (TvApp) -> Unit,
    onOpen: (TvApp) -> Unit,
    onToggleFavorite: (TvApp) -> Unit
) {
    if (apps.isEmpty()) {
        Column(
            Modifier.fillMaxSize().padding(pad),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Rounded.Apps, null, tint = TextLo.copy(alpha = 0.6f), modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(emptyText, color = TextHi, fontSize = 15.sp, textAlign = TextAlign.Center)
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = cardW + 16.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = pad, end = pad, top = 8.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                title,
                color = TextHi,
                fontSize = if (wide) 22.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        gridItemsIndexed(apps, key = { _, a -> a.packageName }) { index, app ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                VidaaAppCard(
                    app = app,
                    width = cardW,
                    height = cardH,
                    wide = wide,
                    isFavorite = app.packageName in favorites,
                    isLastUsed = false,
                    focusRequester = if (index == 0) focusFirst else null,
                    onFocused = onFocused,
                    onOpen = onOpen,
                    onToggleFavorite = onToggleFavorite
                )
                Text(
                    text = app.name,
                    color = TextHi,
                    fontSize = if (wide) 13.sp else 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp).width(cardW),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ───────────────────────────── Dock Inferior Tipo Píldora ─────────────────────────────

@Composable
private fun BottomVidaaDock(
    selected: NavDestination,
    wide: Boolean,
    pad: Dp,
    onSelect: (NavDestination) -> Unit
) {
    val shape = RoundedCornerShape(50)
    Row(
        Modifier.fillMaxWidth()
            .padding(horizontal = pad, vertical = if (wide) 12.dp else 8.dp)
            .clip(shape)
            .background(SurfaceGlass)
            .border(1.dp, Color.White.copy(alpha = 0.16f), shape)
            .padding(horizontal = if (wide) 16.dp else 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavDestination.values().forEach { item ->
            val isSelected = item == selected
            val source = remember { MutableInteractionSource() }
            val focused by source.collectIsFocusedAsState()

            val bg by animateColorAsState(
                when {
                    focused -> Color.White
                    isSelected -> Color.White.copy(alpha = 0.18f)
                    else -> Color.Transparent
                },
                tween(150),
                label = "dock-bg"
            )
            val fg = when {
                focused -> Ink
                isSelected -> TextHi
                else -> TextLo
            }

            Row(
                Modifier.clip(RoundedCornerShape(50))
                    .background(bg)
                    .clickable(interactionSource = source, indication = null, role = Role.Tab) { onSelect(item) }
                    .padding(horizontal = if (wide) 20.dp else 10.dp, vertical = if (wide) 9.dp else 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(item.icon, contentDescription = item.label, tint = fg, modifier = Modifier.size(if (wide) 20.dp else 18.dp))
                if (wide || isSelected) {
                    Text(
                        text = item.label,
                        color = fg,
                        fontSize = if (wide) 14.sp else 11.sp,
                        fontWeight = if (isSelected || focused) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ───────────────────────────── Buscador, Estación de Ajustes y Utilidades ─────────────────────────────

@Composable
private fun SearchField(
    value: String,
    onChange: (String) -> Unit,
    onClose: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier.height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceCard)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Search, null, tint = AngelCyan, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) Text("Buscar aplicación…", color = TextLo, fontSize = 15.sp)
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(color = TextHi, fontSize = 15.sp),
                cursorBrush = SolidColor(AngelCyan),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
        }
        Box(
            Modifier.size(34.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Close, "Cerrar", tint = TextHi, modifier = Modifier.size(18.dp))
        }
    }
}

private fun Modifier.glow(color: Color, strength: Float): Modifier = this.drawBehind {
    val radius = this.size.maxDimension * 0.58f + 16.dp.toPx()
    drawCircle(
        brush = Brush.radialGradient(
            0.55f to color.copy(alpha = strength),
            1f to Color.Transparent,
            center = center,
            radius = radius
        ),
        radius = radius
    )
}

@Composable
private fun QuickSettingsStationDialog(
    appCount: Int,
    hasMediaPermission: Boolean,
    onWifi: () -> Unit,
    onBluetooth: () -> Unit,
    onVolume: () -> Unit,
    onSoundSettings: () -> Unit,
    onDisplay: () -> Unit,
    onSystemSettings: () -> Unit,
    onGrantMediaPermission: () -> Unit,
    onRefreshData: () -> Unit,
    onClose: () -> Unit
) {
    val firstTile = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(120)
        runCatching { firstTile.requestFocus() }
    }
    val shape = RoundedCornerShape(26.dp)

    Box(
        Modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = 0.42f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose),
        contentAlignment = Alignment.CenterEnd
    ) {
        AnimatedVisibility(
            visible = true,
            modifier = Modifier.fillMaxHeight().widthIn(max = 520.dp).fillMaxWidth(0.92f)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {}),
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(220)),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(180))
        ) {
        Column(
            Modifier.fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF17243B), Color(0xF70A1120))))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Estación de Ajustes · ANGEL",
                        color = TextHi,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "Modo App Samsung DeX • $appCount apps listas",
                        color = VidaaTeal,
                        fontSize = 12.sp
                    )
                }
                Box(
                    Modifier.size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Cerrar", tint = TextHi, modifier = Modifier.size(18.dp))
                }
            }

            // Fila 1 de accesos rápidos: Wi-Fi y Bluetooth
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickSettingTile(
                    icon = Icons.Rounded.Wifi,
                    title = "Wi-Fi e Internet",
                    subtitle = "Redes y conexión",
                    accent = VidaaTeal,
                    modifier = Modifier.weight(1f).focusRequester(firstTile),
                    onClick = onWifi
                )
                QuickSettingTile(
                    icon = Icons.Rounded.Bluetooth,
                    title = "Bluetooth",
                    subtitle = "Parlantes, mando y audio",
                    accent = AngelCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onBluetooth
                )
            }

            // Fila 2 de accesos rápidos: Volumen / Sonido y Pantalla DeX
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickSettingTile(
                    icon = Icons.AutoMirrored.Rounded.VolumeUp,
                    title = "Volumen y Audio",
                    subtitle = "Salida de sonido / HDMI",
                    accent = Gold,
                    modifier = Modifier.weight(1f),
                    onClick = onVolume
                )
                QuickSettingTile(
                    icon = Icons.Rounded.DisplaySettings,
                    title = "Pantalla y DeX",
                    subtitle = "Brillo y resolución",
                    accent = Color(0xFFA78BFA),
                    modifier = Modifier.weight(1f),
                    onClick = onDisplay
                )
            }

            // Fila 3: Ajustes de Sonido completos y Ajustes del Sistema
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickSettingTile(
                    icon = Icons.Rounded.GraphicEq,
                    title = "Ajustes de Sonido",
                    subtitle = "Ecualizador y dispositivos",
                    accent = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f),
                    onClick = onSoundSettings
                )
                QuickSettingTile(
                    icon = Icons.Rounded.Settings,
                    title = "Todos los Ajustes",
                    subtitle = "Configuración Android",
                    accent = Color(0xFF94A3B8),
                    modifier = Modifier.weight(1f),
                    onClick = onSystemSettings
                )
            }

            // Aviso para activar minirreproductor completo (carátula y controles) si falta permiso
            if (!hasMediaPermission) {
                DialogPill(
                    text = "Activar detección de música/video (Acceso a notificaciones)",
                    primary = true,
                    icon = Icons.Rounded.NotificationsActive,
                    onClick = onGrantMediaPermission
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    DialogPill(
                        text = "Actualizar todo",
                        primary = false,
                        icon = Icons.Rounded.Refresh,
                        onClick = onRefreshData
                    )
                }
                Box(Modifier.weight(1f)) {
                    DialogPill(
                        text = "Cerrar",
                        primary = false,
                        onClick = onClose
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun QuickSettingTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val shape = RoundedCornerShape(16.dp)
    val bg = if (focused) Color.White else Color(0xFF162036)
    val titleColor = if (focused) Ink else TextHi
    val subColor = if (focused) Ink.copy(alpha = 0.75f) else TextLo

    Row(
        modifier.clip(shape)
            .background(bg)
            .border(
                width = if (focused) 2.5.dp else 1.dp,
                color = if (focused) Color.White else accent.copy(alpha = 0.45f),
                shape = shape
            )
            .clickable(interactionSource = source, indication = null, role = Role.Button, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (focused) accent.copy(alpha = 0.25f) else accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = if (focused) Ink else accent, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(subtitle, color = subColor, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun DialogPill(
    text: String,
    primary: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val bg = when {
        focused -> Color.White
        primary -> AngelCyan
        else -> Color.White.copy(alpha = 0.12f)
    }
    val fg = if (focused || primary) Ink else TextHi
    Row(
        modifier.fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(interactionSource = source, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, color = fg, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun rememberNow(): State<Long> {
    val now = remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now.value = System.currentTimeMillis()
            delay(10_000)
        }
    }
    return now
}

private fun isTelevision(context: Context): Boolean {
    val mode = (context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager)?.currentModeType
    return mode == Configuration.UI_MODE_TYPE_TELEVISION ||
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
}

private fun formatTime(context: Context, millis: Long): String =
    android.text.format.DateFormat.getTimeFormat(context).format(Date(millis))

private fun formatDateShort(millis: Long): String {
    val spanish = Locale("es", "PE")
    return SimpleDateFormat("EEE, d MMM", spanish).format(Date(millis))
        .replaceFirstChar { it.titlecase(spanish) }
}
