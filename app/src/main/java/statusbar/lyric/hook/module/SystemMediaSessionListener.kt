package statusbar.lyric.hook.module

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import statusbar.lyric.tools.LogTools.log

open class SystemMediaSessionListener(context: Context) {
    private var mediaSessionManager: MediaSessionManager? = null
    private val activeControllers = mutableListOf<MediaController>()

    // 监听活跃会话的变化
    private val activeSessionsListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        "activeSessionsListener: ${controllers?.size}".log()

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
            displayPlaybackState(state)
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
                displayPlaybackState(it)
            }
        }
    }

    // 显示媒体元数据
    private fun displayMediaMetadata(metadata: MediaMetadata) {
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown Title"
        "title: $title".log()
        onTitleChanged(title)
    }

    // 显示播放状态
    private fun displayPlaybackState(state: PlaybackState) {
        val stateString = when (state.state) {
            PlaybackState.STATE_PLAYING -> "Playing"
            PlaybackState.STATE_PAUSED -> "Paused"
            PlaybackState.STATE_STOPPED -> "Stopped"
            PlaybackState.STATE_BUFFERING -> "Buffering"
            else -> "Unknown State"
        }
        "Playback state: $stateString".log()
        stateCallback(state.state)
    }

    fun cleanup() {
        mediaSessionManager?.removeOnActiveSessionsChangedListener(activeSessionsListener)
        activeControllers.forEach { it.unregisterCallback(mediaControllerCallback) }
    }


    open fun onTitleChanged(title: String) {}

    open fun stateCallback(state: Int) {}
}
