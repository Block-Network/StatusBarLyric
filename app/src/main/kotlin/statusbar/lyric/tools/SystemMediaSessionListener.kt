package statusbar.lyric.tools

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService

open class SystemMediaSessionListener(context: Context) {
    private var artist: String? = null
    private var mediaSessionManager: MediaSessionManager? = null
    private val activeControllers = mutableListOf<MediaController>()

    // 监听活跃会话的变化
    private val activeSessionsListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        if (controllers?.size == 0) {
            onCleared()
        }
        // 清理之前的回调
        activeControllers.forEach { it.unregisterCallback(mediaControllerCallback) }
        activeControllers.clear()

        controllers?.let {
            it.forEach { controller ->
                activeControllers.add(controller)
                controller.registerCallback(mediaControllerCallback)
                handleMediaController(controller)
            }
        }
    }

    init {
        mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
        mediaSessionManager?.addOnActiveSessionsChangedListener(
            activeSessionsListener,
            ComponentName(context, NotificationListenerService::class.java)
        )
    }

    private fun handleMediaController(controller: MediaController) {
        controller.metadata?.let { metadata ->
            displayMediaMetadata(metadata)
        }

        controller.playbackState?.let { state ->
            onStateChanged(state.state)
        }
    }

    // MediaController 回调
    private val mediaControllerCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            super.onMetadataChanged(metadata)
            metadata?.let {
                displayMediaMetadata(it)
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            state?.let {
                onStateChanged(state.state)
            }
        }
    }

    // 显示媒体元数据
    private fun displayMediaMetadata(metadata: MediaMetadata) {
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
        if (this.artist != artist) {
            this.artist = artist
            onTitleChanged(title)
        }
    }

    fun cleanup() {
        mediaSessionManager?.removeOnActiveSessionsChangedListener(activeSessionsListener)
        activeControllers.forEach { it.unregisterCallback(mediaControllerCallback) }
    }

    open fun onTitleChanged(title: String) {}

    open fun onStateChanged(state: Int) {}

    open fun onCleared() {}
}
