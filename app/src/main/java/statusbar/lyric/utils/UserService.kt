package statusbar.lyric.utils

import android.content.pm.UserInfo
import android.os.*
import android.text.TextUtils
import android.util.Log
import java.util.*


object UserService {
    private var um: IUserManager? = null
    private var binder: IBinder? = null
    private val recipient: IBinder.DeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            LogUtils.e("um is dead")
            binder?.unlinkToDeath(this, 0)
            binder = null
            um = null
        }
    }
    private val userManager: IUserManager?
        get() {
            if (binder == null && um == null) {
                binder = ServiceManager.getService("user")
                if (binder == null) return null
                try {
                    binder?.linkToDeath(recipient, 0)
                } catch (e: RemoteException) {
                    LogUtils.e(Log.getStackTraceString(e))
                }
                um = IUserManager.Stub.asInterface(binder)
            }
            return um
        }

    // lenovo hides user [900, 910) for app cloning
    val users: List<UserInfo>
        get() {
            val um = userManager
            var users: MutableList<UserInfo> = LinkedList()
            if (um == null) return users
            users = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                um.getUsers(true, true, true)
            } else {
                try {
                    um.getUsers(true)
                } catch (e: NoSuchMethodError) {
                    um.getUsers(true, true, true)
                }
            }
            if (!TextUtils.isEmpty(SystemProperties.get("ro.lenovo.region"))) { // lenovo hides user [900, 910) for app cloning
                val gotUsers = BooleanArray(10)
                for (user in users) {
                    val residual = user.id - 900
                    if (residual in 0..9) gotUsers[residual] = true
                }
                for (i in 900..909) {
                    val user = um.getUserInfo(i)
                    if (user != null && !gotUsers[i - 900]) {
                        users.add(user)
                    }
                }
            }
            return users
        }

}