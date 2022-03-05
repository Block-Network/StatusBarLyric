package ice.lib.ads.admob.ad

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import ice.lib.ads.admob.RewardedAdConfig
import ice.lib.ads.admob.result.RewardedAdLoadResult
import ice.lib.ads.admob.result.RewardedAdShowResult

class RewardedAdManager(private val config: RewardedAdConfig) {

    private val adList by lazy { arrayListOf<RewardedAd>() }
    private val rewardedAdId = config.adId

    fun preload(context: Context, count: Int, callback: ((RewardedAdLoadResult) -> Unit)?) {
        for (i in 1..count) {
            RewardedAd.load(context, rewardedAdId, config.adRequestConfig.invoke(AdRequest.Builder()).build(), object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    callback?.invoke(RewardedAdLoadResult(RewardedAdLoadResult.Enum.Failure, error))
                }
                override fun onAdLoaded(ad: RewardedAd) {
                    adList.add(ad)
                    callback?.invoke(RewardedAdLoadResult(RewardedAdLoadResult.Enum.Success, ad))
                }
            })
        }
    }

    fun preloadedAds(): Int = adList.size

    private fun getFullScreenContentCallback(callback: ((RewardedAdShowResult) -> Unit)?): FullScreenContentCallback {
        return object : FullScreenContentCallback() {
            override fun onAdClicked() {
                callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.OnClicked, null))
            }
            override fun onAdDismissedFullScreenContent() {
                callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.OnDismissed, null))
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.OnFailedToShow, error))
            }
            override fun onAdImpression() {
                callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.OnImpression, null))
            }
            override fun onAdShowedFullScreenContent() {
                callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.OnShowed, null))
            }
        }
    }

    fun show(activity: Activity, immediately: Boolean, callback: ((RewardedAdShowResult) -> Unit)?) {
        if (adList.isEmpty()) {
            callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.Null, null))
            if (immediately) {
                RewardedAd.load(activity, rewardedAdId, config.adRequestConfig.invoke(AdRequest.Builder()).build(), object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.ImmediatelyLoadFailed, error))
                    }
                    override fun onAdLoaded(ad: RewardedAd) {
                        callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.ImmediatelyLoaded, ad))
                        ad.fullScreenContentCallback = getFullScreenContentCallback(callback)
                        ad.show(activity) { rewardItem: RewardItem -> callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.OnEarnedReward, rewardItem)) }
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
                next.show(activity) { rewardItem: RewardItem -> callback?.invoke(RewardedAdShowResult(RewardedAdShowResult.Enum.OnEarnedReward, rewardItem)) }
                remove()
                break
            }
        }
    }

}