package ice.lib.ads.admob.result

data class InterstitialAdLoadResult(val result: InterstitialAdLoadResult.Enum, val any: Any?) {
    enum class Enum {
        Success,
        Failure
    }
}