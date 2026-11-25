package com.lygone.smarttvsimulator

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NowPlayingState(
    val isActive: Boolean = false,
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val appName: String = "",
    val albumArt: Bitmap? = null,
    val appPackage: String = ""
)

class MediaMonitorService : NotificationListenerService() {
    companion object {
        private val _nowPlaying = MutableStateFlow(NowPlayingState())
        val nowPlaying: StateFlow<NowPlayingState> = _nowPlaying
        private var activeController: MediaController? = null

        fun isPermissionGranted(context: Context): Boolean {
            val enabled = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ) ?: return false
            return enabled.contains(context.packageName)
        }

        fun togglePlayPause() {
            val state = activeController?.playbackState?.state
            if (state == PlaybackState.STATE_PLAYING) {
                activeController?.transportControls?.pause()
            } else {
                activeController?.transportControls?.play()
            }
        }

        fun skipNext() {
            activeController?.transportControls?.skipToNext()
        }

        fun skipPrevious() {
            activeController?.transportControls?.skipToPrevious()
        }

        fun openSourceApp(context: Context) {
            val pkg = _nowPlaying.value.appPackage
            if (pkg.isBlank()) return
            val pm = context.packageManager
            val intent = pm.getLeanbackLaunchIntentForPackage(pkg) ?: pm.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                runCatching {
                    context.startActivity(intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }

        fun refreshSessions(context: Context) {
            try {
                val manager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
                    ?: return
                val component = ComponentName(context, MediaMonitorService::class.java)
                val sessions = manager.getActiveSessions(component)
                val controller = sessions.firstOrNull {
                    it.playbackState?.state == PlaybackState.STATE_PLAYING
                } ?: sessions.firstOrNull {
                    it.playbackState?.state == PlaybackState.STATE_PAUSED ||
                        it.playbackState?.state == PlaybackState.STATE_BUFFERING
                }

                activeController = controller
                if (controller != null) {
                    val meta = controller.metadata
                    val pm = context.packageManager
                    val label = runCatching {
                        pm.getApplicationLabel(pm.getApplicationInfo(controller.packageName, 0)).toString()
                    }.getOrDefault("Multimedia")

                    val rawTitle = meta?.getString(MediaMetadata.METADATA_KEY_TITLE)
                        ?: meta?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                        ?: "Reproducción activa"
                    val rawArtist = meta?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                        ?: meta?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                        ?: meta?.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
                        ?: label

                    _nowPlaying.value = NowPlayingState(
                        isActive = true,
                        isPlaying = controller.playbackState?.state == PlaybackState.STATE_PLAYING,
                        title = rawTitle,
                        artist = rawArtist,
                        appName = label,
                        albumArt = meta?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                            ?: meta?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                            ?: meta?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON),
                        appPackage = controller.packageName
                    )
                } else {
                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                    if (audioManager?.isMusicActive == true) {
                        _nowPlaying.value = NowPlayingState(
                            isActive = true,
                            isPlaying = true,
                            title = "Audio / Video en reproducción",
                            artist = "Sonido activo del sistema",
                            appName = "Multimedia"
                        )
                    } else {
                        _nowPlaying.value = NowPlayingState(isActive = false)
                    }
                }
            } catch (_: SecurityException) {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                if (audioManager?.isMusicActive == true) {
                    _nowPlaying.value = NowPlayingState(
                        isActive = true,
                        isPlaying = true,
                        title = "Audio / Video en reproducción",
                        artist = "Activa permiso de notificaciones para ver carátula",
                        appName = "Sonido"
                    )
                } else {
                    _nowPlaying.value = NowPlayingState(isActive = false)
                }
            } catch (_: Exception) {
                _nowPlaying.value = NowPlayingState(isActive = false)
            }
        }
    }

    override fun onListenerConnected() {
        refreshSessions(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        refreshSessions(this)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        refreshSessions(this)
    }
}
