package ice.lib.ads.admob

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class AdmobConfig {

    var interstitialAdConfig = InterstitialAdConfig()
    var rewardedAdConfig = RewardedAdConfig()
    var defaultAdRequestConfig: (AdRequest.Builder.() -> AdRequest.Builder) = {
        this
    }

}

data class InterstitialAdConfig(
    val adId: String = "ca-app-pub-3940256099942544/1033173712",
    val adRequestConfig: (AdRequest.Builder.() -> AdRequest.Builder) = { this }
)

data class RewardedAdConfig(
    val adId: String = "ca-app-pub-3940256099942544/5224354917",
    val adRequestConfig: (AdRequest.Builder.() -> AdRequest.Builder) = { this }
)

data class BannerAdConfig(
    val adId: String = "ca-app-pub-3940256099942544/6300978111",
    val adSize: AdSize = AdSize.BANNER,
    val adRequestConfig: (AdRequest.Builder.() -> AdRequest.Builder) = { this },
    val block: (AdView.() -> Unit) = {  }
)
