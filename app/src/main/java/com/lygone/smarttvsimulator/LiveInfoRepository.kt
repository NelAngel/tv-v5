package com.lygone.smarttvsimulator

import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.math.roundToInt

data class WeatherInfo(
    val tempCelsius: Int,
    val conditionText: String,
    val isDay: Boolean,
    val code: Int
)

enum class EventCategory(val label: String) {
    ALL("Todo"),
    FOOTBALL("Fútbol"),
    MOVIES("Películas"),
    SERIES("Series")
}

data class UpcomingEvent(
    val category: EventCategory,
    val badge: String,
    val title: String,
    val subtitle: String,
    val schedule: String,
    val platform: String,
    val accentHex: Long,
    val imageUrl: String? = null
)

object LiveInfoRepository {

    // Coordenadas de Arequipa, Perú (actualiza automáticamente cada 30 min)
    private const val WEATHER_URL =
        "https://api.open-meteo.com/v1/forecast?latitude=-16.4090&longitude=-71.5375&current=temperature_2m,is_day,weather_code&timezone=America%2FLima"

    // Feed RSS oficial de noticias de Perú (RPP)
    private const val NEWS_RSS_URL = "https://rpp.pe/feed"

    // Feeds RSS en vivo de Deportes / Fútbol y Cine / Entretenimiento
    private const val SPORTS_RSS_URL = "https://rpp.pe/feed/deportes"
    private const val ENTERTAINMENT_RSS_URL = "https://rpp.pe/feed/entretenimiento"

    fun fetchWeather(): WeatherInfo? {
        return try {
            val jsonStr = httpGet(WEATHER_URL) ?: return null
            val current = JSONObject(jsonStr).getJSONObject("current")
            val temp = current.getDouble("temperature_2m").roundToInt()
            val isDay = current.optInt("is_day", 1) == 1
            val code = current.optInt("weather_code", 0)
            WeatherInfo(
                tempCelsius = temp,
                conditionText = weatherDescription(code),
                isDay = isDay,
                code = code
            )
        } catch (_: Exception) {
            null
        }
    }

    fun fetchPeruNews(limit: Int = 10): List<String> {
        return parseRssTitles(NEWS_RSS_URL, limit)
    }

    /**
     * Combina titulares reales en vivo de Fútbol y Cine/Series (RSS) con la programación destacada
     * para que el apartado de Cartelera y Eventos siempre tenga novedades frescas.
     */
    fun fetchBillboardEvents(): List<UpcomingEvent> {
        val events = ArrayList<UpcomingEvent>()

        // 1. Fútbol en vivo desde feed deportivo; se extrae también la imagen editorial del RSS.
        val sportsItems = parseRssItems(SPORTS_RSS_URL, 6)
        if (sportsItems.isNotEmpty()) {
            sportsItems.take(4).forEachIndexed { idx, item ->
                val headline = item.first
                events.add(
                    UpcomingEvent(
                        category = EventCategory.FOOTBALL,
                        badge = "FÚTBOL • EN AGENDA",
                        title = headline,
                        subtitle = "Cobertura deportiva y próximos encuentros",
                        schedule = if (idx == 0) "Destacado Hoy" else "Próxima Jornada",
                        platform = "L1 Max / ESPN / Disney+",
                        accentHex = 0xFF10B981,
                        imageUrl = item.second
                    )
                )
            }
        } else {
            events.addAll(defaultFootballEvents())
        }

        // 2. Películas y Series desde feed de entretenimiento
        val entertainmentItems = parseRssItems(ENTERTAINMENT_RSS_URL, 6)
        if (entertainmentItems.isNotEmpty()) {
            entertainmentItems.take(4).forEachIndexed { idx, item ->
                val headline = item.first
                val isSeries = headline.contains("serie", ignoreCase = true) ||
                    headline.contains("temporada", ignoreCase = true) ||
                    headline.contains("capítulo", ignoreCase = true) ||
                    idx % 2 == 1
                events.add(
                    UpcomingEvent(
                        category = if (isSeries) EventCategory.SERIES else EventCategory.MOVIES,
                        badge = if (isSeries) "SERIE • ESTRENO" else "PELÍCULA • CARTELERA",
                        title = headline,
                        subtitle = if (isSeries) "Nuevos episodios y lanzamientos en streaming" else "Estrenos y tendencias de cine",
                        schedule = "Esta semana",
                        platform = "YouTube / Xuper / Prime / Netflix",
                        accentHex = if (isSeries) 0xFFEC4899 else 0xFFF59E0B,
                        imageUrl = item.second
                    )
                )
            }
        } else {
            events.addAll(defaultMediaEvents())
        }

        return events
    }

    fun getDefaultBillboardEvents(): List<UpcomingEvent> =
        defaultFootballEvents() + defaultMediaEvents()

    private fun defaultFootballEvents(): List<UpcomingEvent> = listOf(
        UpcomingEvent(
            category = EventCategory.FOOTBALL,
            badge = "FÚTBOL • LIGA 1 & SELECCIÓN",
            title = "Jornada de Fútbol Peruano e Internacional",
            subtitle = "Partidos en vivo, previa y tabla de posiciones",
            schedule = "Hoy • 20:00 hrs",
            platform = "L1 Max / Movistar Deportes",
            accentHex = 0xFF10B981
        ),
        UpcomingEvent(
            category = EventCategory.FOOTBALL,
            badge = "FÚTBOL • TORNEOS CONMEBOL & UEFA",
            title = "Cruces de Fase Final y Partidos Estelares",
            subtitle = "Transmisión en alta definición",
            schedule = "Mañana • 15:00 hrs",
            platform = "ESPN / Disney+ / YouTube",
            accentHex = 0xFF3B82F6
        )
    )

    private fun defaultMediaEvents(): List<UpcomingEvent> = listOf(
        UpcomingEvent(
            category = EventCategory.MOVIES,
            badge = "PELÍCULA • ESTRENO DESTACADO",
            title = "Cartelera de Estrenos y Acción 4K",
            subtitle = "Títulos recién llegados a plataformas digitales",
            schedule = "Disponible ahora",
            platform = "Xuper / Prime Video / Max",
            accentHex = 0xFFF59E0B
        ),
        UpcomingEvent(
            category = EventCategory.SERIES,
            badge = "SERIE • NUEVA TEMPORADA",
            title = "Maratón de Series y Estrenos Semanales",
            subtitle = "Nuevos episodios disponibles para ver hoy",
            schedule = "Este fin de semana",
            platform = "Netflix / Disney+ / YouTube",
            accentHex = 0xFFEC4899
        )
    )

    /** Descarga una portada editorial del feed con límites para evitar imágenes enormes. */
    fun fetchImage(imageUrl: String): android.graphics.Bitmap? {
        return try {
            if (!imageUrl.startsWith("https://") && !imageUrl.startsWith("http://")) return null
            val connection = URL(imageUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 6500
            connection.readTimeout = 6500
            connection.instanceFollowRedirects = true
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 LYGANE-SmartTV")
            connection.connect()
            val bitmap = connection.inputStream.use { android.graphics.BitmapFactory.decodeStream(it) }
            connection.disconnect()
            bitmap
        } catch (_: Exception) { null }
    }

    /** Devuelve título e imagen asociada a cada elemento RSS (media:content, enclosure o img HTML). */
    private fun parseRssItems(urlStr: String, limit: Int): List<Pair<String, String?>> {
        return try {
            val xml = httpGet(urlStr) ?: return emptyList()
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))
            val items = ArrayList<Pair<String, String?>>()
            var insideItem = false
            var title: String? = null
            var image: String? = null
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT && items.size < limit) {
                if (eventType == XmlPullParser.START_TAG) {
                    val tag = parser.name.lowercase(Locale.ROOT)
                    if (tag == "item" || tag == "entry") {
                        insideItem = true; title = null; image = null
                    } else if (insideItem && tag == "title") {
                        title = parser.nextText().trim()
                    } else if (insideItem && (tag == "content" || tag == "thumbnail" || tag == "enclosure")) {
                        image = parser.getAttributeValue(null, "url")
                            ?: parser.getAttributeValue("http://search.yahoo.com/mrss/", "url")
                            ?: image
                    } else if (insideItem && (tag == "description" || tag == "summary")) {
                        val html = parser.nextText()
                        if (image == null) {
                            val match = Regex("<img[^>]+src=[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE).find(html)
                            image = match?.groupValues?.getOrNull(1)
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && (parser.name == "item" || parser.name == "entry")) {
                    title?.takeIf { it.isNotBlank() }?.let { items.add(it to image) }
                    insideItem = false
                }
                eventType = parser.next()
            }
            items
        } catch (_: Exception) { emptyList() }
    }

    private fun parseRssTitles(urlStr: String, limit: Int): List<String> {
        return try {
            val xml = httpGet(urlStr) ?: return emptyList()
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            val headlines = ArrayList<String>()
            var insideItem = false
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT && headlines.size < limit) {
                if (eventType == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        "item" -> insideItem = true
                        "title" -> if (insideItem) {
                            val title = parser.nextText().trim()
                            if (title.isNotBlank()) headlines.add(title)
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && parser.name == "item") {
                    insideItem = false
                }
                eventType = parser.next()
            }
            headlines
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun httpGet(urlStr: String): String? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", "LYGANE-SmartTV/2.1")
            }
            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun weatherDescription(code: Int): String = when (code) {
        0 -> "Despejado"
        1, 2 -> "Parc. nublado"
        3 -> "Nublado"
        45, 48 -> "Neblina"
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Lluvia"
        71, 73, 75 -> "Nieve"
        95, 96, 99 -> "Tormenta"
        else -> "Arequipa"
    }
}
