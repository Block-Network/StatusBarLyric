@file:Suppress("DEPRECATION")

package cn.fkj233.xposed.statusbarlyric.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import cn.fkj233.xposed.statusbarlyric.R
import cn.fkj233.xposed.statusbarlyric.databinding.ActivitySettingBinding
import cn.fkj233.xposed.statusbarlyric.fragment.NewSettingsFragment
import cn.fkj233.xposed.statusbarlyric.utils.ActivityOwnSP
import cn.fkj233.xposed.statusbarlyric.utils.Utils
import cn.fkj233.xposed.statusbarlyric.view.data.DataHelper
import cn.fkj233.xposed.statusbarlyric.view.data.DataItem
import cn.fkj233.xposed.statusbarlyric.view.miuiview.MIUIDialog
import kotlin.system.exitProcess


class NewSettingsActivity : Activity() {

    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.hide()
        if (!checkLSPosed()) return
        ActivityOwnSP.activity = this
        DataHelper.currentActivity = this
        showFragment(DataItem.Main)
        registerReceiver(HookReceiver(), IntentFilter().apply {
            addAction("Hook_Sure")
        })

    }

    private fun checkLSPosed(): Boolean {
        return try {
            Utils.getSP(this, "Lyric_Config")
            true
        } catch (e: Throwable) {
            MIUIDialog(this).apply {
                setTitle(R.string.Tips)
                setMessage(R.string.NotSupport)
                setButton(R.string.ReStart) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    exitProcess(0)
                }
                setCancelable(false)
                show()
            }
            false
        }
    }

    private fun setMenu(isMain: Boolean) {
        binding.settingsMenu.setImageResource(if (!isMain) R.drawable.abc_ic_ab_back_material else R.drawable.abc_ic_menu_overflow_material)
        binding.settingsMenu.setOnClickListener {
            if (isMain) {
                showFragment(DataItem.Menu)
            } else {
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            val string =
                fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 2).name
            setMenu(string == DataHelper.getTitle(DataItem.Main))
            binding.Title.text = string
            fragmentManager.popBackStack()
        }
    }

    inner class HookReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Handler(Looper.getMainLooper()).post {
                    val message: String = if (intent.getBooleanExtra("hook_ok", false)) {
                        getString(R.string.HookSureSuccess)
                    } else {
                        getString(R.string.HookSureFail)
                    }
                    MIUIDialog(this@NewSettingsActivity).apply {
                        setTitle(getString(R.string.HookSure))
                        setMessage(message)
                        setButton(getString(R.string.Ok)) { dismiss() }
                        show()
                    }
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }

    fun showFragment(dataItem: DataItem) {
        setMenu(dataItem == DataItem.Main)
        val fragment = NewSettingsFragment().setDataItem(dataItem)
        val title = DataHelper.getTitle(dataItem)
        binding.Title.text = title
        fragmentManager.beginTransaction().setCustomAnimations(
            R.animator.slide_right_in,
            R.animator.slide_left_out,
            R.animator.slide_left_in,
            R.animator.slide_right_out
        ).replace(R.id.settings_fragment_container, fragment).addToBackStack(title).commit()
    }

}