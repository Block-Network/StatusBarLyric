package ice.lib.ads.admob.result

data class RewardedAdLoadResult(val result: RewardedAdLoadResult.Enum, val any: Any?) {
    enum class Enum {
        Success,
        Failure
    }
}