package ice.lib.ads.admob.result

data class RewardedAdShowResult(val result: RewardedAdShowResult.Enum, val any: Any?) {
    enum class Enum {
        Null,
        OnClicked,
        OnDismissed,
        OnFailedToShow,
        OnImpression,
        OnShowed,
        ImmediatelyLoadFailed,
        ImmediatelyLoaded,
        OnEarnedReward
    }
}