package statusbar.lyric

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import ice.lib.ads.admob.AdmobConfig
import ice.lib.ads.admob.AdmobManager
import ice.lib.ads.admob.RewardedAdConfig

class App: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var app: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var admobManager: AdmobManager
    }

    override fun onCreate() {
        super.onCreate()
        app = applicationContext
        AdmobManager.initSdk(this)
        val adConfig = AdmobConfig().also {
            it.rewardedAdConfig = RewardedAdConfig("ca-app-pub-9730534578915916/4576531490")
        }
        admobManager = AdmobManager(this, adConfig)
    }
}