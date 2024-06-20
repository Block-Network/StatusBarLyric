package statusbar.lyric.tools

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.util.Base64
import java.security.MessageDigest

class SignatureVerifier(private val context: Context) {

    fun isSignatureValid(packageName: String): Boolean {
        val packageInfo = getPackageInfo(packageName) ?: return false
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageInfo.signingInfo.apkContentsSigners
        } else {
            packageInfo.signatures
        }
        val signatureHash = computeSignatureHash(signatures)
        return signatureHash in arrayOf("jnPHRTVR8SsB2Mb+CBJsLo1ccmq76CVfrHND0wYb5ec=","uyCGwrq5ojbpfaLSRROhtk6lb3kXdGRwtAKoGdGjnzk=")
    }

    private fun getPackageInfo(packageName: String): PackageInfo? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun computeSignatureHash(signatures: Array<Signature>): String {
        val md = MessageDigest.getInstance("SHA-256")
        for (signature in signatures) {
            md.update(signature.toByteArray())
        }
        val digest = md.digest()
        return Base64.encodeToString(digest, Base64.DEFAULT)
    }
}
