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
    private val activeControllers = mutableMapOf<MediaController, MediaControllerCallback>()

    // 监听活跃会话的变化
    private val activeSessionsListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        if (controllers?.size == 0) {
            onCleared()
        }
        // 清理之前的回调
        activeControllers.entries.forEach { it.key.unregisterCallback(it.value) }
        activeControllers.clear()

        controllers?.let {
            it.forEach { controller ->
                val callback = MediaControllerCallback(controller)
                activeControllers[controller] = callback
                controller.registerCallback(callback)
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
            displayMediaMetadata(controller.packageName, metadata)
        }

        controller.playbackState?.let { state ->
            onStateChanged(controller.packageName, state.state)
        }
    }

    // MediaController 回调
    private inner class MediaControllerCallback(private val controller: MediaController) :
        MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            super.onMetadataChanged(metadata)
            metadata?.let {
                displayMediaMetadata(controller.packageName, it)
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            state?.let {
                onStateChanged(controller.packageName, state.state)
            }
        }
    }

    // 显示媒体元数据
    private fun displayMediaMetadata(changedApp: String, metadata: MediaMetadata) {
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown Artist"
        if (this.artist != artist) {
            this.artist = artist
            onTitleChanged(changedApp, title)
        }
    }

    fun cleanup() {
        mediaSessionManager?.removeOnActiveSessionsChangedListener(activeSessionsListener)
        activeControllers.entries.forEach { it.key.unregisterCallback(it.value) }
    }

    open fun onTitleChanged(changedApp: String, title: String) {}

    open fun onStateChanged(changedApp: String, state: Int) {}

    open fun onCleared() {}
}
