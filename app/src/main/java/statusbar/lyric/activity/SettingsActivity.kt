@file:Suppress("DEPRECATION")

package statusbar.lyric.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import statusbar.lyric.R
import statusbar.lyric.databinding.ActivitySettingBinding
import statusbar.lyric.fragment.NewSettingsFragment
import statusbar.lyric.utils.ActivityOwnSP
import statusbar.lyric.utils.Utils
import statusbar.lyric.view.data.DataHelper
import statusbar.lyric.view.data.DataItem
import statusbar.lyric.view.miuiview.MIUIDialog
import kotlin.system.exitProcess


class SettingsActivity : Activity() {

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
                    MIUIDialog(this@SettingsActivity).apply {
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