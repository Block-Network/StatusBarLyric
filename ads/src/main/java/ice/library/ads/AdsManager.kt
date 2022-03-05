package ice.library.ads

import android.app.Activity
import android.app.Application
import android.util.DisplayMetrics
import android.view.ViewGroup
import com.google.android.gms.ads.*

class AdsManager private constructor(val debug: Boolean = false, interstitialId: String = "", rewardId: String = "", bannerId: String = "") {

    private var mInterstitialAd: MInterstitialAd? = null
    private var mRewardedAd: MRewardedAd? = null
    private lateinit var context: Application

    private val interstitial: String = if (debug) "ca-app-pub-3940256099942544/1033173712" else interstitialId
    private val reward: String = if (debug) "ca-app-pub-3940256099942544/5224354917" else rewardId
    private val banner: String = if (debug) "ca-app-pub-3940256099942544/6300978111" else bannerId

    fun requestLoadInterstitialAd(adRequest: AdRequest = AdRequest.Builder().build(), callback: (LoadAdResults, Any?) -> Unit) {
        mInterstitialAd = MInterstitialAd(interstitial, adRequest)
        mInterstitialAd?.load(context, callback)
    }

    fun showInterstitialAd(activity: Activity, shouldWaitForLoad: Boolean = false, callback: (InterstitialAdResults, Any?) -> Unit) {
        mInterstitialAd?.show(activity, callback, shouldWaitForLoad)
    }

    fun requestLoadRewardAd(adRequest: AdRequest = AdRequest.Builder().build(), callback: (LoadAdResults, Any?) -> Unit) {
        mRewardedAd = MRewardedAd(reward, adRequest)
        mRewardedAd?.load(context, callback)
    }

    fun showRewardAd(activity: Activity, shouldWaitForLoad: Boolean = false, callback: (RewardAdResults, Any?) -> Unit) {
        mRewardedAd?.show(activity, callback, shouldWaitForLoad)
    }

    /**
     * get a Banner Ad
     *  @param adSize [AdSize]
     * @param adUnitId [String] default: Test ad unit id
     * @param adRequest [AdRequest] default: AdRequest.Builder().build()
     * @param customSet (block) default: null
     *
     */
    fun getBannerAd(adSize: AdSize, adRequest: AdRequest = AdRequest.Builder().build(), customSet: (AdView.() -> Unit)? = null): AdView {
        val adView = AdView(context)
        adView.adSize = adSize
        adView.adUnitId = banner
        customSet?.invoke(adView)
        adView.loadAd(adRequest)
        return adView
    }

    fun initAdmob(application: Application) {
        MobileAds.initialize(application) {}
        context = application
    }

    companion object {
        var debug: Boolean = false
        var interstitialId: String = ""
        var rewardId: String = ""
        var bannerId: String = ""
        val instance: AdsManager by lazy { AdsManager(debug, interstitialId, rewardId, bannerId) }
    }

}

fun Activity.getAdaptiveBannerAdSize(container: ViewGroup): AdSize {
    val display = windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)
    val density = outMetrics.density
    var adWidthPixels = container.width.toFloat()
    if (adWidthPixels == 0f) {
        adWidthPixels = outMetrics.widthPixels.toFloat()
    }
    val adWidth = (adWidthPixels / density).toInt()
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
}
