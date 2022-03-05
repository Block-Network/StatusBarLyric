package ice.lib.ads.admob

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import ice.lib.ads.admob.ad.InterstitialAdManager
import ice.lib.ads.admob.ad.RewardedAdManager
import ice.lib.ads.admob.result.InterstitialAdLoadResult
import ice.lib.ads.admob.result.InterstitialAdShowResult
import ice.lib.ads.admob.result.RewardedAdLoadResult
import ice.lib.ads.admob.result.RewardedAdShowResult

class AdmobManager(private val context: Context, private val config: AdmobConfig) {
    private val interstitialAdManager by lazy { InterstitialAdManager(config.interstitialAdConfig) }
    private val rewardedAdManager by lazy { RewardedAdManager(config.rewardedAdConfig) }

    fun preloadInterstitialAds(count: Int, callback: ((InterstitialAdLoadResult) -> Unit)? = null) {
        var flag = count
        if (count <= 0) {
            flag = 1
        }
        if (count > 5) {
            flag = 5
        }
        interstitialAdManager.preload(context, flag, callback)
    }

    fun preloadedInterstitialAds(): Int = interstitialAdManager.preloadedAds()

    fun showInterstitialAds(activity: Activity, immediately: Boolean = false, callback: ((InterstitialAdShowResult) -> Unit)? = null) {
        interstitialAdManager.show(activity, immediately, callback)
    }

    fun preloadRewardedAds(count: Int, callback: ((RewardedAdLoadResult) -> Unit)? = null) {
        var flag = count
        if (count <= 0) {
            flag = 1
        }
        if (count > 5) {
            flag = 5
        }
        rewardedAdManager.preload(context, flag, callback)
    }

    fun preloadedRewardedAds(): Int = rewardedAdManager.preloadedAds()

    fun showRewardAds(activity: Activity, immediately: Boolean = false, callback: ((RewardedAdShowResult) -> Unit)? = null) {
        rewardedAdManager.show(activity, immediately, callback)
    }

    companion object {
        fun initSdk(context: Context) {
            MobileAds.initialize(context) {}
        }

        fun getBannerAd(context: Context, config: BannerAdConfig): View {
            val adView = AdView(context)
            adView.adUnitId = config.adId
            adView.adSize = config.adSize
            adView.loadAd(config.adRequestConfig.invoke(AdRequest.Builder()).build())
            config.block.invoke(adView)
            return adView
        }
    }

}