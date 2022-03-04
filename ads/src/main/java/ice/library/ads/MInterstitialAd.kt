package ice.library.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.concurrent.thread

internal class MInterstitialAd(var adUnitId: String, var adRequest: AdRequest) {
    private var mInterstitialAd: InterstitialAd? = null
    private var loadCallback: ((LoadAdResults, Any?) -> Unit)? = null
    private var isLoading = false

    fun load(context: Context, callback: (LoadAdResults, Any?) -> Unit) {
        loadCallback = callback
        isLoading = true
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                isLoading = false
                callback(LoadAdResults.AdFailedToLoad, error)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                isLoading = false
                callback(LoadAdResults.AdLoaded, interstitialAd)
            }
        })
    }

    fun reload(context: Context) {
        if (loadCallback == null) {
            loadCallback = { _, _ -> }
        }
        load(context, loadCallback!!)
    }

    fun show(activity: Activity, callback: (InterstitialAdResults, Any?) -> Unit, shouldWaitForLoad: Boolean = false) {
        thread {
            while (true) {
                if (!isLoading || !shouldWaitForLoad) break
                Thread.sleep(100)
            }
            if (mInterstitialAd == null) {
                callback(InterstitialAdResults.AdNotLoad, null)
                return@thread
            } else {
                synchronized(mInterstitialAd!!) {
                    val fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            callback(InterstitialAdResults.AdClicked, null)
                        }

                        override fun onAdDismissedFullScreenContent() {
                            callback(InterstitialAdResults.AdDismissed, null)
                            reload(activity)
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            callback(InterstitialAdResults.AdFailedToShow, error)
                        }

                        override fun onAdShowedFullScreenContent() {
                            callback(InterstitialAdResults.AdShowed, null)
                        }
                    }
                    mInterstitialAd!!.fullScreenContentCallback = fullScreenContentCallback
                    activity.runOnUiThread { mInterstitialAd!!.show(activity) }
                }
            }
        }
    }

}