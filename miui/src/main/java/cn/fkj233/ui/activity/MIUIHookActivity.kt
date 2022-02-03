package cn.fkj233.ui.activity

import android.content.Context
import android.os.Bundle

class MIUIHookActivity(private val moduleClassLoader: ClassLoader, private val hookAppClassLoader: ClassLoader): MIUIActivity() {
    class FixedClassLoader(private val mModuleClassLoader: ClassLoader, private val mHostClassLoader: ClassLoader): ClassLoader(
        mBootstrap
    ) {

        companion object {
            private val mBootstrap: ClassLoader = Context::class.java.classLoader!!
        }

        override fun loadClass(name: String, resolve: Boolean): Class<*> {
            runCatching {
                return mBootstrap.loadClass(name)
            }

            runCatching {
                if ("androidx.lifecycle.ReportFragment" == name) {
                    return mHostClassLoader.loadClass(name)
                }
            }

            return try {
                mModuleClassLoader.loadClass(name)
            } catch (e: Exception) {
                mHostClassLoader.loadClass(name)
            }
        }

    }

    override fun getClassLoader(): ClassLoader {
        return FixedClassLoader(moduleClassLoader, hookAppClassLoader)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val windowState = savedInstanceState.getBundle("android:viewHierarchyState")
        windowState?.let {
            it.classLoader = MIUIHookActivity::class.java.classLoader
        }
        super.onRestoreInstanceState(savedInstanceState)
    }
}