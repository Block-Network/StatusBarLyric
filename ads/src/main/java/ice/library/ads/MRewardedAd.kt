package ice.library.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlin.concurrent.thread

internal class MRewardedAd(var adUnitId: String, var adRequest: AdRequest) {
    private var mRewardAd: RewardedAd? = null
    private var loadCallback: ((LoadAdResults, Any?) -> Unit)? = null
    private var isLoading = false

    fun load(context: Context, callback: (LoadAdResults, Any?) -> Unit) {
        loadCallback = callback
        isLoading = true
        RewardedAd.load(context, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                isLoading = false
                callback(LoadAdResults.AdFailedToLoad, error)
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                mRewardAd = rewardedAd
                isLoading = false
                callback(LoadAdResults.AdLoaded, rewardedAd)
            }
        })
    }

    fun reload(context: Context) {
        if (loadCallback == null) {
            loadCallback = { _, _ -> }
        }
        load(context, loadCallback!!)
    }

    fun show(activity: Activity, callback: (RewardAdResults, Any?) -> Unit, shouldWaitForLoad: Boolean = false) {
        thread {
            while (true) {
                if (!isLoading || !shouldWaitForLoad) break
                Thread.sleep(100)
            }
            if (mRewardAd == null) {
                callback(RewardAdResults.AdNotLoad, null)
                return@thread
            } else {
                synchronized(mRewardAd!!) {
                    val fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            callback(RewardAdResults.AdClicked, null)
                        }

                        override fun onAdDismissedFullScreenContent() {
                            callback(RewardAdResults.AdDismissed, null)
                            reload(activity)
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            callback(RewardAdResults.AdFailedToShow, error)
                        }

                        override fun onAdShowedFullScreenContent() {
                            callback(RewardAdResults.AdShowed, null)
                        }
                    }
                    mRewardAd!!.fullScreenContentCallback = fullScreenContentCallback
                    activity.runOnUiThread { mRewardAd!!.show(activity) { callback(RewardAdResults.AdEarnedReward, it) } }
                }
            }
        }
    }

}