@file:Suppress("DEPRECATION")

package statusbar.lyric.hook.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.PlaybackState
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import dalvik.system.DexFile
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import statusbar.lyric.utils.AppleMusicUtil
import statusbar.lyric.utils.Lyric
import statusbar.lyric.utils.LyricInfo
import statusbar.lyric.utils.Utils.sendLyric
import statusbar.lyric.utils.ktx.callMethod
import statusbar.lyric.utils.ktx.callStaticMethod
import statusbar.lyric.utils.ktx.getObjectField
import statusbar.lyric.utils.ktx.hookAfterMethod
import statusbar.lyric.utils.ktx.hookBeforeMethod
import java.lang.reflect.Constructor


class AppleMusic internal constructor(lpparam: LoadPackageParam) {
    private var curLyricObj: Any? = null
    private var curSongInfo: Any? = null
    private var playbackStateCompat: Any? = null
    private var context: Context? = null
    private var timeStarted = false
    private val handler: Handler
    private var mediaMetadataCompatClass: Class<*>? = null
    private var localeUtilClass: Class<*>? = null
    private var lyricConvertConstructor: Constructor<*>? = null
    private var lyricReqConstructor: Constructor<*>? = null
    private var curLyrics = Lyric()
    private var curInfo: LyricInfo? = null
    private var nextUpdateTime = 0
    private var last = ""
    private var curId = ""
    private var requested = false
    private var lastLyrics = ""
    private var clazz: Class<*>? = null

    init {
        val classLoader = lpparam.classLoader
        val handlerThread = HandlerThread("lyric_thread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        mediaMetadataCompatClass = classLoader.loadClass("android.support.v4.media.MediaMetadataCompat")
        val lyricsSectionVectorClass = classLoader.loadClass("com.apple.android.music.ttml.javanative.model.LyricsSectionVector")
        localeUtilClass = classLoader.loadClass("com.apple.android.music.playback.util.LocaleUtil")
        clazz = classLoader.loadClass("com.apple.android.mediaservices.javanative.common.StringVector\$StringVectorNative")
        val musicCls = classLoader.loadClass("com.apple.android.music.model.Song")
        Thread {

            val onLoadCallbackClass = classLoader.loadClass("com.apple.android.music.ttml.javanative.LyricsController\$LyricsControllerNative\$OnLoadCallback")
            val dexFile = DexFile(AppleMusicUtil.getSourceDir(lpparam))
            val classNames = dexFile.entries()
            while (classNames.hasMoreElements()) {
                val classname = classNames.nextElement()
                runCatching {
                    val cls = lpparam.classLoader.loadClass(classname)
                    if (cls.superclass == onLoadCallbackClass) {
                        handleLyricReqHook(cls, classLoader)
                    } else {
                        for (constructor in cls.constructors) {
                            if (constructor.parameterTypes.size == 1 && constructor.parameterTypes[0] == lyricsSectionVectorClass && cls.fields.size == 1) {
                                lyricConvertConstructor = constructor
                            }
                        }
                    }
                }
            }
        }.start()
        "com.apple.android.music.model.BaseContentItem".hookAfterMethod("setId", String::class.java) {
            val trace = Log.getStackTraceString(Exception())
            if (musicCls.isInstance(it.thisObject) && (trace.contains("getItemAtIndex") && trace.contains("i7.u.accept") || trace.contains("e3.h.w"))) {
                curId = it.args[0] as String
                reqLyric(curId.toLong())
            }
        }


        //            hook metaDATA change
        "android.support.v4.media.session.MediaControllerCompat\$a\$a".hookBeforeMethod("onMetadataChanged", MediaMetadata::class.java) {
            val metadataCompat = mediaMetadataCompatClass!!.callStaticMethod("a", it.args[0])
            val metadata = metadataCompat!!.getObjectField("t") as MediaMetadata
            val newTitle = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
            if (last != newTitle) {
                last = newTitle
                requested = false
                curLyrics.clean()
                context?.sendBroadcast(Intent().apply {
                    action = "Lyric_Server"
                    putExtra("Lyric_Type", "app_stop")
                })

            }
        }
        // 获得类实例以及context
        "com.apple.android.music.AppleMusicApplication".hookAfterMethod("onCreate") {
            val application = it.thisObject as Application
            context = application.baseContext
        }
//             播放状态，时间，是否暂停等
        "android.support.v4.media.session.MediaControllerCompat\$a\$b".hookBeforeMethod("handleMessage", Message::class.java) {
            val m = it.args[0] as Message
            if (m.what == 2) {
                playbackStateCompat = m.obj
                playbackStateCompat!!.getObjectField("D") ?: return@hookBeforeMethod
                updateTime()
            }
        }

    }

    private fun handleLyricReqHook(callBack: Class<*>, classLoader: ClassLoader) {
        val lyricReqCls = callBack.enclosingClass
        lyricReqConstructor = lyricReqCls.getConstructor(Context::class.java, Long::class.javaPrimitiveType, Long::class.javaPrimitiveType, Long::class.javaPrimitiveType, clazz, Boolean::class.javaPrimitiveType)
        callBack.hookBeforeMethod("call", classLoader.loadClass("com.apple.android.music.ttml.javanative.model.SongInfo\$SongInfoPtr"), Int::class.javaPrimitiveType, Long::class.javaPrimitiveType) {
            if (lyricConvertConstructor == null) {
                return@hookBeforeMethod
            }
            val appleCb = it.thisObject.getObjectField("s")
            curSongInfo = it.args[0].callMethod("get")
            if (curSongInfo == null) {
                if (appleCb == null) {
                    it.result = null
                    return@hookBeforeMethod
                }
                return@hookBeforeMethod
            }
            val lyricsSectionVector = curSongInfo!!.callMethod("getSections")
            curLyricObj = lyricConvertConstructor!!.newInstance(lyricsSectionVector)
            updateLyricDict()

            context?.sendBroadcast(Intent().apply {
                action = "Lyric_Server"
                putExtra("Lyric_Type", "app_stop")
            })

            if (appleCb == null) {
                it.result = null
            }
        }
    }

    private fun reqLyric(songId: Long) {

        if (requested || lyricReqConstructor == null) {
            return
        }
        requested = true
        val localeVector = clazz!!.newInstance()
        val lyricClient = lyricReqConstructor!!.newInstance(context, songId, songId, 0, localeVector, false)
        lyricClient.callMethod("subscribe", null as Any?)
        Handler(Looper.getMainLooper()).postDelayed({ requested = false }, 1000)

    }

    private fun updateLyricDict() {
        curLyrics.clean()
        var i = 0
        var lyricsLinePtr = curLyricObj!!.callMethod("a", i)
        while (lyricsLinePtr != null) {
            val lyricsLine = lyricsLinePtr.callMethod("get")
            val str: String = lyricsLine!!.callMethod("getHtmlLineText") as String
            val begin = lyricsLine.callMethod("getBegin") as Int
            val end = lyricsLine.callMethod("getEnd") as Int
            curLyrics.addInfo(begin, end, str)
            i++
            lyricsLinePtr = curLyricObj!!.callMethod("a", i)
        }
    }

    private fun updateTime() {
        if (timeStarted) {
            return
        }
        timeStarted = true
        handler.postDelayed(object : Runnable {
            override fun run() {
                val d = playbackStateCompat!!.getObjectField("D")
                val playbackState = d as PlaybackState
                if (!onUpdate(playbackState)) {
                    timeStarted = false
                    return
                }
                handler.postDelayed(this, 400)
            }
        }, 400)
    }

    fun onUpdate(playbackState: PlaybackState?): Boolean {
        if (playbackState!!.state == PlaybackState.STATE_PAUSED) {
            nextUpdateTime = 0
            curInfo = null
            return false
        }
        val currentPosition = ((SystemClock.elapsedRealtime() - playbackState.lastPositionUpdateTime) * playbackState.playbackSpeed + playbackState.position).toLong()
        curInfo = curLyrics.getLyricByPosition(currentPosition)
        if (curInfo != null) {
            nextUpdateTime = curInfo!!.end
            if (currentPosition > nextUpdateTime) {
                curInfo = curLyrics.getLyricByPosition(currentPosition)
            }
            if (lastLyrics != curInfo!!.lyricStr) {
                sendLyric(context, curInfo!!.lyricStr, "APM")
                lastLyrics = curInfo!!.lyricStr
            }
        }
        return true
    }
}