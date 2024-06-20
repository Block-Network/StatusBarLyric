package statusbar.lyric.tools

import cn.xiaowine.xkt.Tool.SHA256Encode
import com.jaredrummler.ktsh.Shell
import java.math.BigInteger

object ShellTools {
    var isA: Boolean = false

    private var dfe1: String = ""
    private var bootID: BigInteger = BigInteger.ZERO
    private var premium = false

    private fun su(su: Boolean) = Shell(if (su) "su" else "sh")


//    fun checkIsSeniorMode(su: Boolean): Boolean {
//        val saveSeniorModeInfo = getSeniorModeInfo(su)
//        try {
//            val jsonObject = JSONObject(saveSeniorModeInfo)
//
//        } catch (_: Exception) {
//            return false
//        }
//    }

    fun havePremium(su: Boolean): Boolean {
        if (premium) return true
        getBootID(su).let {
            if (it == BigInteger.ZERO) {
                return false
            } else {
                getSeniorModeInfo(su).let { s ->
                    if (s.isEmpty()) {
                        return false
                    } else {
                        premium = s == b(su)
                        return premium
                    }
                }
            }
        }
    }

    fun b(su: Boolean): String {
        return "${getBootID(su)}3125".SHA256Encode()
    }

    fun String.c(): String {
        return "${this}3125".SHA256Encode()
    }

    fun String.saveSeniorModeInfo(su: Boolean): Boolean {
        dfe1 = this
        val result = su(su).run(
            "settings put system dfe1 ${
                if (this.isEmpty()) {
                    "null"
                } else b(su)
            }"
        )
        return result.isSuccess
    }

    fun getSeniorModeInfo(su: Boolean): String {
        if (dfe1.isNotEmpty()) return dfe1
        val result = su(su).run("settings get system dfe1")
        return if (result.isSuccess) {
            result.output()
        } else {
            ""
        }
    }

    fun getFakeBootID(su: Boolean): BigInteger {
        getBootID(su).let {
            return BigInteger("${it.toString().replace("3", "6").replace("4", "9")}${"0".repeat(it.toString().length)}")
        }
    }

    fun String.getRealBootID(): BigInteger {
        val bigNumber = BigInteger(this)
        val str = bigNumber.toString()
        val length = str.length / 2
        var count = 0
        var stringWithoutHalfZeros = str

        for (i in str.lastIndex downTo 0) {
            if (str[i] == '0' && count < length) {
                stringWithoutHalfZeros = stringWithoutHalfZeros.removeRange(i..i)
                count++
            }
        }
        return BigInteger(stringWithoutHalfZeros.replace("6", "3").replace("9", "4"))
    }

    fun getBootID(su: Boolean): BigInteger {
        if (bootID != BigInteger.ZERO) {
            return bootID
        }
//        cat /sys/devices/soc0/family
        val family = su(su).run("cat /sys/devices/soc0/family")
        if (family.isSuccess) {
//            Snapdragon
            if (family.output() == "Snapdragon") {
//                cat /sys/devices/soc0/serial_number
                val result = su(su).run("cat /sys/devices/soc0/serial_number")
                bootID = if (result.isSuccess) {
                    val out = result.output()
                    try {
                        BigInteger(out)
                    } catch (_: NumberFormatException) {
                        BigInteger(out.lowercase().replace("0x", "").toLong(16).toString())
                    }
                } else {
                    BigInteger.ZERO
                }
            } else {
//                getprop ro.serialno
                val result = su(su).run("getprop ro.serialno")
                bootID = if (result.isSuccess) {
                    val out = result.output()
                    BigInteger(out.substring(5))
                } else {
                    BigInteger.ZERO
                }
            }
        }
        return bootID
    }


}