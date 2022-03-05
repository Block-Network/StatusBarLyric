package statusbar.lyric.utils

import android.content.pm.IPackageManager
import android.content.pm.PackageInfo
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.os.ServiceManager
import android.util.Log

object IPackageUtils {
    private var pm: IPackageManager? = null
    private var binder: IBinder? = null

    private val recipient: IBinder.DeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            LogUtils.e("pm is dead")
            binder!!.unlinkToDeath(this, 0)
            binder = null
            pm = null
        }
    }

    private fun getPackageInfo(packageName: String?, flags: Int, userId: Int): PackageInfo? {
        val pm = getPackageManager() ?: return null
        return if (Build.VERSION.SDK_INT >= 33) {
            pm.getPackageInfo(packageName, flags.toLong(), userId)
        } else pm.getPackageInfo(packageName, flags, userId)
    }

    fun getPackageInfoFromAllUsers(packageName: String?, flags: Int): Map<Int?, PackageInfo?> {
        val pm = getPackageManager()
        val res: MutableMap<Int?, PackageInfo?> = HashMap()
        if (pm == null) return res
        for (user in UserService.users) {
            val info: PackageInfo? = getPackageInfo(packageName, flags, user.id)
            if (info?.applicationInfo != null) res[user.id] = info
        }
        return res
    }

    private fun getPackageManager(): IPackageManager? {
        if (binder == null && pm == null) {
            binder = ServiceManager.getService("package")
            if (binder == null) return null
            try {
                binder?.linkToDeath(recipient, 0)
            } catch (e: RemoteException) {
                LogUtils.e("Get PackageManage error" + Log.getStackTraceString(e))
            }
            pm = IPackageManager.Stub.asInterface(binder)
        }
        return pm
    }

}