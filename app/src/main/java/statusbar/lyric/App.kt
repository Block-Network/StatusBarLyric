package statusbar.lyric

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import ice.library.ads.AdsManager

class App: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var app: Context
    }

    override fun onCreate() {
        super.onCreate()
        app = applicationContext
        AdsManager.bannerId = "ca-app-pub-9730534578915916/4576531490"
        AdsManager.bannerId = "ca-app-pub-9730534578915916/8650086997"
        AdsManager.instance.initAdmob(this)
    }
}