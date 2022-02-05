/*
 * BlockMIUI
 * Copyright (C) 2022 fkj@fkj233.cn
 * https://github.com/577fkj/BlockMIUI
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/BlockMIUI/blob/main/LICENSE>.
 */

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