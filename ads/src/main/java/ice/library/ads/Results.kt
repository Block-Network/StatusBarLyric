package ice.library.ads

enum class InterstitialAdResults {
    AdNotLoad, // ad is null
    AdClicked, // onAdClicked
    AdDismissed, // onAdDismissedFullScreenContent
    AdFailedToShow, // onAdFailedToShowFullScreenContent
    AdShowed, // onAdShowedFullScreenContent
}

enum class RewardAdResults {
    AdNotLoad, // ad is null
    AdClicked, // onAdClicked
    AdDismissed, // onAdDismissedFullScreenContent
    AdFailedToShow, // onAdFailedToShowFullScreenContent
    AdShowed, // onAdShowedFullScreenContent
    AdEarnedReward // onUserEarnedReward
}

enum class LoadAdResults {
    AdLoaded, // onAdLoaded
    AdFailedToLoad, // onAdFailedToLoad
}
