package ice.lib.ads.admob.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import ice.lib.ads.admob.InterstitialAdConfig
import ice.lib.ads.admob.result.InterstitialAdLoadResult
import ice.lib.ads.admob.result.InterstitialAdShowResult

internal class InterstitialAdManager(private val config: InterstitialAdConfig) {

    private val adList by lazy { arrayListOf<InterstitialAd>() }
    private val interstitialAdId = config.adId

    fun preload(context: Context, count: Int, callback: ((InterstitialAdLoadResult) -> Unit)?) {
        for (i in 1..count) {
            InterstitialAd.load(context, interstitialAdId, config.adRequestConfig.invoke(AdRequest.Builder()).build(), object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    callback?.invoke(InterstitialAdLoadResult(InterstitialAdLoadResult.Enum.Failure, error))
                }
                override fun onAdLoaded(ad: InterstitialAd) {
                    adList.add(ad)
                    callback?.invoke(InterstitialAdLoadResult(InterstitialAdLoadResult.Enum.Success, ad))
                }
            })
        }
    }

    fun preloadedAds(): Int = adList.size

    private fun getFullScreenContentCallback(callback: ((InterstitialAdShowResult) -> Unit)?): FullScreenContentCallback {
        return object : FullScreenContentCallback() {
            override fun onAdClicked() {
                callback?.invoke(InterstitialAdShowResult(InterstitialAdShowResult.Enum.OnClicked, null))
            }
            override fun onAdDismissedFullScreenContent() {
                callback?.invoke(InterstitialAdShowResult(InterstitialAdShowResult.Enum.OnDismissed, null))
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                callback?.invoke(InterstitialAdShowResult(InterstitialAdShowResult.Enum.OnFailedToShow, error))
            }
            override fun onAdImpression() {
                callback?.invoke(InterstitialAdShowResult(InterstitialAdShowResult.Enum.OnImpression, null))
            }
            override fun onAdShowedFullScreenContent() {
                callback?.invoke(InterstitialAdShowResult(InterstitialAdShowResult.Enum.OnShowed, null))
            }
        }
    }

    fun show(activity: Activity, immediately: Boolean, callback: ((InterstitialAdShowResult) -> Unit)?) {
        if (adList.isEmpty()) {
            callback?.invoke(InterstitialAdShowResult(InterstitialAdShowResult.Enum.Null, null))
            if (immediately) {
                InterstitialAd.load(activity, interstitialAdId, config.adRequestConfig.invoke(AdRequest.Builder()).build(), object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        callback?.invoke(InterstitialAdShowResult(InterstitialAdShowResult.Enum.ImmediatelyLoadFailed, error))
                    }
                    override fun onAdLoaded(ad: InterstitialAd) {
                        callback?.invoke(InterstitialAdShowResult(InterstitialAdShowResult.Enum.ImmediatelyLoaded, ad))
                        ad.fullScreenContentCallback = getFullScreenContentCallback(callback)
                        ad.show(activity)
                    }
                })
                return
            }
            return
        }
        adList.iterator().run {
            while (hasNext()) {
                val next = next()
                next.fullScreenContentCallback = getFullScreenContentCallback(callback)
                next.show(activity)
                remove()
                break
            }
        }
    }

}