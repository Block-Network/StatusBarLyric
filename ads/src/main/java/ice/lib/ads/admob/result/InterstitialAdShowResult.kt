package ice.lib.ads.admob.result

data class InterstitialAdShowResult(val result: InterstitialAdShowResult.Enum, val any: Any?) {
    enum class Enum {
        Null,
        OnClicked,
        OnDismissed,
        OnFailedToShow,
        OnImpression,
        OnShowed,
        ImmediatelyLoadFailed,
        ImmediatelyLoaded,
    }
}