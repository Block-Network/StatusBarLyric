@file:Suppress("DEPRECATION")

package cn.fkj233.xposed.statusbarlyric.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import android.preference.SwitchPreference
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import cn.fkj233.xposed.statusbarlyric.BuildConfig
import cn.fkj233.xposed.statusbarlyric.R
import cn.fkj233.xposed.statusbarlyric.config.Config
import cn.fkj233.xposed.statusbarlyric.config.IconConfig
import cn.fkj233.xposed.statusbarlyric.utils.ActivityUtils
import cn.fkj233.xposed.statusbarlyric.utils.ActivityUtils.showToastOnLooper
import cn.fkj233.xposed.statusbarlyric.utils.ShellUtils
import cn.fkj233.xposed.statusbarlyric.utils.Utils


@SuppressLint("ExportedPreferenceActivity")
class SettingsActivity : PreferenceActivity() {
    private val activity = this
    private var config: Config? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.root_preferences)
        try {
            config = Utils.getSP(activity, "Lyric_Config")?.let { Config(it) }
            title = getString(R.string.AppName)
            init()
        } catch (ignored: SecurityException) {
            AlertDialog.Builder(activity).apply {
                setTitle(R.string.Tips)
                setIcon(R.mipmap.ic_launcher)
                setMessage(R.string.NotSupport)
                setPositiveButton(R.string.ReStart) { _, _ ->
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    Process.killProcess(Process.myPid())
                }
                setCancelable(false)
                show()
            }
        }
    }

    private fun init() {
        val tips = "Tips1"
        val preferences = activity.getSharedPreferences(tips, MODE_PRIVATE)
        if (!preferences.getBoolean(tips, false)) {
            AlertDialog.Builder(activity).apply {
                setTitle(getString(R.string.Tips))
                setIcon(R.mipmap.ic_launcher)
                setMessage(getString(R.string.AppTips))
                setNegativeButton(R.string.TipsIDone) { _, _ ->
                    preferences.edit().putBoolean(tips, true).apply()
                    ActivityUtils.getNotice(activity)
                }
                setPositiveButton(R.string.Quit) { _, _ -> activity.finish() }
                setNeutralButton(R.string.PrivacyPolicy) { _, _ ->
                    val uri: Uri = Uri.parse("https://github.com/577fkj/StatusBarLyric/blob/main/EUAL.md")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                    init()
                }
                setCancelable(false)
                show()
            }
        } else {
            ActivityUtils.getNotice(activity)
            AppCenter.start(
                application, "6713f7e7-d1f5-4261-bb32-f5a94028a9f4",
                Analytics::class.java, Crashes::class.java
            )
        }


        //使用说明
        val verExplain = findPreference("ver_explain")!!
        verExplain.onPreferenceClickListener = OnPreferenceClickListener {
            AlertDialog.Builder(activity).apply {
                setIcon(R.mipmap.ic_launcher)
                setTitle(getString(R.string.VerExplanation))
                setMessage(
                    String.format(
                        " %s [%s] %s",
                        getString(R.string.CurrentVer),
                        BuildConfig.VERSION_NAME,
                        getString(R.string.VerExp)
                    )
                )
                setNegativeButton(getString(R.string.Done), null)
                create()
                show()
            }
            true
        }

        //模块注意事项
        val warnPoint = findPreference("warn_explain")!!
        warnPoint.onPreferenceClickListener = OnPreferenceClickListener {
            AlertDialog.Builder(activity).apply {
                setIcon(R.mipmap.ic_launcher)
                setTitle(getString(R.string.WarnExplanation))
                setMessage(
                    java.lang.String.format(
                        " %s [%s] %s",
                        getString(R.string.CurrentVer),
                        BuildConfig.VERSION_NAME,
                        getString(R.string.WarnExp)
                    )
                )
                setNegativeButton(getString(R.string.Done), null)
                show()
            }
            true
        }

        // 隐藏桌面图标
        val hIcons = (findPreference("hLauncherIcon") as SwitchPreference)
        hIcons.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            val packageManager: PackageManager = activity.packageManager
            val mode: Int = if (newValue as Boolean) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }
            packageManager.setComponentEnabledSetting(
                ComponentName(activity, "miui.cn.fkj233.xposed.statusbarlyric.launcher"),
                mode,
                PackageManager.DONT_KILL_APP
            )
            true
        }


        // 歌词总开关
        val lyricService = (findPreference("lyricService") as SwitchPreference)
        lyricService.isChecked = config!!.getLyricService()
        lyricService.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            config!!.setLyricService((newValue as Boolean))
            Analytics.trackEvent(String.format("开关 %s", newValue.toString()))
            true
        }

        // 歌词最大自适应宽度
        val lyricMaxWidth = (findPreference("lyricMaxWidth") as EditTextPreference)
        lyricMaxWidth.isEnabled = config!!.getLyricWidth().toString() == "-1"
        lyricMaxWidth.summary = config!!.getLyricMaxWidth().toString()
        if (config!!.getLyricMaxWidth().toString() == "-1") {
            lyricMaxWidth.summary = getString(R.string.Off)
        }
        lyricMaxWidth.dialogMessage =
            String.format("%s%s", getString(R.string.LyricMaxWidthTips), lyricMaxWidth.summary)
        lyricMaxWidth.onPreferenceChangeListener =
            OnPreferenceChangeListener { _, newValue: Any ->
                lyricMaxWidth.dialogMessage = String.format(
                    "%s%s",
                    getString(R.string.LyricMaxWidthTips),
                    getString(R.string.Adaptive)
                )
                lyricMaxWidth.summary = getString(R.string.Adaptive)
                config!!.setLyricMaxWidth(-1)
                try {
                    val value =
                        newValue.toString().replace(" ", "").replace("\n", "")
                            .replace("\\+", "")
                    if ((value.toInt() <= 100) && (value.toInt() >= 0)) {
                        config!!.setLyricMaxWidth(value.toInt())
                        lyricMaxWidth.dialogMessage = String.format(
                            "%s%s",
                            getString(R.string.LyricMaxWidthTips),
                            value
                        )
                        lyricMaxWidth.summary = value
                    } else {
                        showToastOnLooper(activity, getString(R.string.RangeError))
                    }
                } catch (ignored: NumberFormatException) {
                    showToastOnLooper(activity, getString(R.string.RangeError))
                }
                true
            }


        // 歌词宽度
        val lyricWidth = (findPreference("lyricWidth") as EditTextPreference)
        lyricWidth.summary = config!!.getLyricWidth().toString()
        if (config!!.getLyricWidth().toString() == "-1") {
            lyricWidth.summary = getString(R.string.Adaptive)
        }
        lyricWidth.setDefaultValue(config!!.getLyricWidth().toString())
        lyricWidth.dialogMessage = String.format("%s%s", getString(R.string.LyricWidthTips), lyricWidth.summary)
        lyricWidth.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            lyricWidth.dialogMessage = String.format(
                "%s%s",
                getString(R.string.LyricWidthTips),
                getString(R.string.Adaptive)
            )
            lyricMaxWidth.isEnabled = true
            lyricWidth.summary = getString(R.string.Adaptive)
            config!!.setLyricWidth(-1)
            try {
                val value =
                    newValue.toString().replace(" ", "").replace("\n", "")
                        .replace("\\+", "")
                config!!.setLyricWidth(-1)
                if ((value.toInt() <= 100) && (value.toInt() >= 0)) {
                    config!!.setLyricWidth(value.toInt())
                    lyricWidth.dialogMessage = String.format("%s%s", getString(R.string.LyricWidthTips), value)
                    lyricWidth.summary = value
                    lyricMaxWidth.isEnabled = false
                } else {
                    showToastOnLooper(activity, getString(R.string.RangeError))
                }
            } catch (ignored: NumberFormatException) {
                showToastOnLooper(activity, getString(R.string.RangeError))
            }
            true
        }

        // 歌词图标
        val icon = (findPreference("lyricIcon") as SwitchPreference)
        icon.isChecked = config!!.getIcon()
        icon.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setIcon((newValue as Boolean?)!!)
            true
        }


        // 歌词颜色
        val lyricColour = (findPreference("lyricColour") as EditTextPreference)
        lyricColour.summary = config!!.getLyricColor()
        if (config!!.getLyricColor() == "off") {
            lyricColour.summary = getString(R.string.Adaptive)
        }
        lyricColour.setDefaultValue(config!!.getLyricColor())
        lyricColour.dialogMessage = String.format("%s%s", getString(R.string.LyricColorTips), lyricColour.summary)
        lyricColour.isEnabled = !config!!.getUseSystemReverseColor()
        lyricColour.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            lyricColour.dialogMessage = String.format(
                "%s%s",
                getString(R.string.LyricColorTips),
                getString(R.string.Adaptive)
            )
            lyricColour.summary = getString(R.string.Adaptive)
            config!!.setLyricColor("off")
            val value = newValue.toString().replace(" ", "")
            try {
                Color.parseColor(newValue.toString())
                lyricColour.dialogMessage = String.format(
                    "%s%s",
                    getString(R.string.LyricColorTips),
                    config!!.getLyricColor()
                )
                lyricColour.summary = value
                config!!.setLyricColor(value)
            } catch (e: Exception) {
                config!!.setLyricColor("off")
                lyricColour.summary = getString(R.string.Adaptive)
                showToastOnLooper(activity, getString(R.string.LyricColorError))
            }

            true
        }

        // 歌词反色
        val useSystemReverseColor = (findPreference("UseSystemReverseColor") as SwitchPreference)
        useSystemReverseColor.isChecked = config!!.getUseSystemReverseColor()
        useSystemReverseColor.onPreferenceChangeListener =
            OnPreferenceChangeListener { _, newValue: Any? ->
                config!!.setUseSystemReverseColor((newValue as Boolean?)!!)
                lyricColour.isEnabled = (!newValue!!)
                true
            }

        // 暂停关闭歌词
        val lyricOff = (findPreference("lyricOff") as SwitchPreference)
        lyricOff.isChecked = config!!.getLyricAutoOff()
        lyricOff.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setLyricAutoOff((newValue as Boolean?)!!)
            true
        }

        // 歌词动效
        val anim = findPreference("lyricAnim") as ListPreference
        anim.entryValues = arrayOf(
            "off", "top", "lower",
            "left", "right", "random"
        )
        anim.entries = arrayOf(
            getString(R.string.Off), getString(R.string.top), getString(R.string.lower),
            getString(R.string.left), getString(R.string.right), getString(R.string.random)
        )
        val dict: HashMap<String, String> = hashMapOf()
        dict["off"] = getString(R.string.Off)
        dict["top"] = getString(R.string.top)
        dict["lower"] = getString(R.string.lower)
        dict["left"] = getString(R.string.left)
        dict["right"] = getString(R.string.right)
        dict["random"] = getString(R.string.random)
        anim.summary = dict[config!!.getAnim()]
        anim.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            config!!.setAnim(newValue.toString())
            anim.summary = dict[config!!.getAnim()]
            true
        }

        // 歌词滚动一次
        val lShowOnce = (findPreference("lShowOnce") as SwitchPreference)
        lShowOnce.isEnabled = !config!!.getLyricStyle()
        lShowOnce.isChecked = config!!.getLShowOnce()
        lShowOnce.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setLShowOnce((newValue as Boolean?)!!)
            true
        }

        // 歌词速度
        val lyricSpeed = (findPreference("lyricSpeed") as EditTextPreference)
        lyricSpeed.isEnabled = config!!.getLyricStyle()
        lyricSpeed.summary = config!!.getLyricSpeed().toString()
        lyricSpeed.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            config!!.setLyricSpeed(newValue.toString().toFloat())
            lyricSpeed.summary = newValue.toString()
            true
        }

        // 魅族样式歌词
        val lyricStyle = (findPreference("lyricStyle") as SwitchPreference)
        lyricStyle.isChecked = config!!.getLyricStyle()
        lyricStyle.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            config!!.setLyricStyle((newValue as Boolean))
            lyricSpeed.isEnabled = newValue
            lShowOnce.isEnabled = (!newValue)
            if (newValue) {
                config!!.setLShowOnce(false)
                lShowOnce.isChecked = false
            }
            true
        }

        // 歌词大小
        val lyricSize = (findPreference("lyricSize") as EditTextPreference)
        lyricSize.summary = config!!.getLyricSize().toString()
        if (config!!.getLyricSize().toString() == "0") {
            lyricSize.summary = getString(R.string.Default)
        }
        lyricSize.dialogMessage = String.format("%s%s", getString(R.string.LyricSizeTips), lyricSize.summary)
        lyricSize.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            lyricSize.dialogMessage = String.format(
                "%s%s",
                getString(R.string.LyricSizeTips),
                getString(R.string.Default)
            )
            lyricSize.summary = getString(R.string.Default)
            config!!.setLyricSize(0)
            try {
                val value = newValue.toString().replace(" ", "").replace("\n", "")
                if ((value.toInt() <= 50) && (value.toInt() > 0)) {
                    config!!.setLyricSize(value.toInt())
                    lyricSize.dialogMessage = String.format("%s%s", "0~50，当前:", value)
                    lyricSize.summary = value
                } else {
                    showToastOnLooper(activity, getString(R.string.RangeError))
                }
            } catch (ignore: NumberFormatException) {
            }
            true
        }

        // 歌词左右位置
        val lyricPosition = (findPreference("lyricPosition") as EditTextPreference)
        lyricPosition.summary = config!!.getLyricPosition().toString()
        if (config!!.getLyricPosition().toString() == "0") {
            lyricPosition.summary = getString(R.string.Default)
        }
        lyricPosition.dialogMessage = String.format("%s%s", getString(R.string.LyricPosTips), lyricPosition.summary)
        lyricPosition.onPreferenceChangeListener =
            OnPreferenceChangeListener { _, newValue: Any ->
                lyricPosition.dialogMessage = String.format(
                    "%s%s",
                    getString(R.string.LyricPosTips),
                    getString(R.string.Default)
                )
                lyricPosition.summary = getString(R.string.Default)
                config!!.setLyricPosition(0)
                try {
                    val value = newValue.toString().replace(" ", "").replace("\n", "")
                    if (value.toInt() <= 900 && value.toInt() >= -900) {
                        config!!.setLyricPosition(value.toInt())
                        lyricPosition.dialogMessage =
                            String.format("%s%s", getString(R.string.LyricPosTips), value)
                        lyricPosition.summary = value
                    } else {
                        showToastOnLooper(activity, getString(R.string.RangeError))
                    }
                } catch (ignore: NumberFormatException) {
                }
                true
            }

        // 歌词上下位置
        val lyricHigh = (findPreference("lyricHigh") as EditTextPreference)
        lyricHigh.summary = config!!.getLyricHigh().toString()
        if (config!!.getLyricHigh().toString() == "0") {
            lyricHigh.summary = getString(R.string.Default)
        }
        lyricHigh.dialogMessage = String.format("%s%s", getString(R.string.LyricHighTips), lyricHigh.summary)
        lyricHigh.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            lyricHigh.dialogMessage = String.format(
                "%s%s",
                getString(R.string.LyricHighTips),
                config!!.getLyricHigh()
            )
            lyricHigh.summary = getString(R.string.Default)
            config!!.setLyricHigh(0)
            try {
                val value = newValue.toString().replace(" ", "").replace("\n", "")
                if (value.toInt() <= 100 && value.toInt() >= -100) {
                    config!!.setLyricHigh(value.toInt())
                    lyricHigh.dialogMessage = String.format("%s%s", getString(R.string.LyricHighTips), value)
                    lyricHigh.summary = value
                } else {
                    showToastOnLooper(activity, getString(R.string.RangeError))
                }
            } catch (ignore: NumberFormatException) {
            }
            true
        }

//      伪时间样式/自定义文字
        val pseudoTimeStyle = (findPreference("pseudoTimeStyle") as EditTextPreference)
        pseudoTimeStyle.isEnabled = config!!.getPseudoTime()
        pseudoTimeStyle.summary = config!!.getPseudoTimeStyle()
        if (!config!!.getPseudoTime()) {
            pseudoTimeStyle.summary = getString(R.string.Default)
        }
        pseudoTimeStyle.dialogMessage =
            String.format("%s%s", getString(R.string.pseudoTimeStyleTips), pseudoTimeStyle.summary)
        pseudoTimeStyle.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            try {
                val value = newValue.toString().replace(" ", "").replace("\n", "")
                config!!.setPseudoTimeStyle(value)
            } catch (ignore: NumberFormatException) {
            }
            true
        }

//        伪时间
        val pseudoTime = (findPreference("pseudoTime") as SwitchPreference)
        pseudoTime.isChecked = config!!.getPseudoTime()
        pseudoTime.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            config!!.setPseudoTime((newValue as Boolean))
            pseudoTimeStyle.isEnabled = newValue
            true
        }

        // 图标反色
        val iconColor = (findPreference("iconAutoColor") as SwitchPreference)
        iconColor.summary = getString(R.string.Off)
        if (config!!.getIconAutoColor()) {
            iconColor.summary = getString(R.string.On)
        }
        iconColor.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            config!!.setIconAutoColor(newValue as Boolean)
            true
        }

        // 图标上下位置
        val iconHigh = (findPreference("iconHigh") as EditTextPreference)
        iconHigh.summary = config!!.getIconHigh().toString()
        if (config!!.getIconHigh().toString() == "7") {
            iconHigh.summary = getString(R.string.Default)
        }
        iconHigh.dialogMessage = String.format("%s%s", getString(R.string.LyricPosTips), iconHigh.summary)
        iconHigh.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            iconHigh.dialogMessage = String.format(
                "%s%s",
                getString(R.string.LyricPosTips),
                config!!.getIconHigh()
            )
            iconHigh.summary = getString(R.string.Default)
            config!!.setIconHigh(7)
            try {
                val value = newValue.toString().replace(" ", "").replace("\n", "")
                if (value.toInt() <= 100 && value.toInt() >= -100) {
                    config!!.setIconHigh(value.toInt())
                    iconHigh.dialogMessage = String.format("%s%s", getString(R.string.LyricPosTips), value)
                    iconHigh.summary = value
                } else {
                    showToastOnLooper(activity, getString(R.string.RangeError))
                }
            } catch (ignore: NumberFormatException) {
            }
            true
        }

        // 图标大小
        val iconSize = (findPreference("iconSize") as EditTextPreference)
        iconSize.summary = config!!.getIconSize().toString()
        if (config!!.getIconSize() == 0) {
            lyricSize.summary = getString(R.string.Default)
        }
        iconSize.dialogMessage = String.format("%s%s", getString(R.string.LyricSizeTips), iconSize.summary)
        iconSize.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any ->
            iconSize.dialogMessage = String.format(
                "%s%s",
                getString(R.string.LyricSizeTips),
                getString(R.string.Default)
            )
            iconSize.summary = getString(R.string.Default)
            config!!.setIconSize(0)
            try {
                val value = newValue.toString().replace(" ", "").replace("\n", "")
                if ((value.toInt() <= 50) && (value.toInt() > 0)) {
                    config!!.setIconSize(value.toInt())
                    iconSize.dialogMessage = String.format("%s%s", "0~50，当前:", value)
                    iconSize.summary = value
                } else {
                    showToastOnLooper(activity, getString(R.string.RangeError))
                }
            } catch (ignore: NumberFormatException) {
                showToastOnLooper(activity, getString(R.string.RangeError))
            }
            true
        }

        // 自定义图标
        val iconCustomize = findPreference("iconCustomize")!!
        iconCustomize.onPreferenceClickListener = OnPreferenceClickListener {
            val icons =
                arrayOf("Netease", "KuGou", "QQMusic", "Myplayer", "MiGu", "Default")
            val iconConfig = IconConfig(Utils.getSP(activity, "Icon_Config"))
            val actionListener =
                DialogInterface.OnClickListener { _, which ->
                    val iconName = icons[which]
                    val view: View = View.inflate(activity, R.layout.seticon, null)
                    val editText: EditText = view.findViewById(R.id.editText)!!
                    val imageView: ImageView = view.findViewById(R.id.imageView)
                    imageView.foreground = BitmapDrawable(Utils.stringToBitmap(iconConfig.getIcon(iconName)))
                    AlertDialog.Builder(activity).apply {
                        setTitle(iconName)
                        setView(view)
                        setPositiveButton(R.string.Ok) { _, _ ->
                            val editTexts = editText.text
                            if (editTexts.toString().isNotEmpty()) {
                                try {
                                    imageView.foreground =
                                        BitmapDrawable(Utils.stringToBitmap(iconConfig.getIcon(iconName)))
                                    iconConfig.setIcon(iconName, editText.text.toString())
                                } catch (ignore: Exception) {
                                    showToastOnLooper(activity, getString(R.string.IconError))
                                }
                            } else {
                                showToastOnLooper(activity, getString(R.string.IconError))
                            }
                        }
                        setNegativeButton(R.string.Cancel, null)
                        setNeutralButton(
                            getString(R.string.MakeIcon)
                        ) { _, _ ->
                            val componentName =
                                ComponentName(
                                    "com.byyoung.setting",
                                    "com.byyoung.setting.MediaFile.activitys.ImageBase64Activity"
                                )
                            val intent = Intent().setClassName("com.byyoung.setting", "utils.ShortcutsActivity")
                            intent.putExtra("PackageName", componentName.packageName)
                            intent.putExtra("PackageClass", componentName.className)
                            try {
                                activity.startActivity(intent)
                            } catch (ignore: Exception) {
                                showToastOnLooper(activity, getString(R.string.MakeIconError))
                            }
                        }
                        show()
                    }
                }
            AlertDialog.Builder(activity).apply {
                setTitle("图标")
                setItems(icons, actionListener)
                setNegativeButton(R.string.Done, null)
                show()
            }
            true
        }

        // 歌词时间切换
        val lyricSwitch = (findPreference("lyricToTime") as SwitchPreference)
        lyricSwitch.isChecked = config!!.getLyricSwitch()
        lyricSwitch.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setLyricSwitch((newValue as Boolean?)!!)
            true
        }


        // 防烧屏
        val antiBurn = (findPreference("antiBurn") as SwitchPreference)
        antiBurn.isChecked = config!!.getAntiBurn()
        antiBurn.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setAntiBurn((newValue as Boolean?)!!)
            true
        }


        // 锁屏隐藏
        val lockScreenOff = (findPreference("lockScreenOff") as SwitchPreference)
        lockScreenOff.isChecked = config!!.getLockScreenOff()
        lockScreenOff.onPreferenceChangeListener =
            OnPreferenceChangeListener { _, newValue: Any? ->
                config!!.setLockScreenOff((newValue as Boolean?)!!)
                true
            }

        // 隐藏通知图标
        val hNoticeIcon = (findPreference("hNoticeIcon") as SwitchPreference)
        hNoticeIcon.isChecked = config!!.getHNoticeIcon()
        hNoticeIcon.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setHNoticeIcon((newValue as Boolean?)!!)
            true
        }


        // 隐藏实时网速
        val hNetWork = (findPreference("hNetWork") as SwitchPreference)
        hNetWork.isChecked = config!!.getHNetSpeed()
        hNetWork.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setHNetSpeed((newValue as Boolean?)!!)
            true
        }


        // 隐藏运营商名称
        val hCUK = (findPreference("hCUK") as SwitchPreference)
        hCUK.isChecked = config!!.getHCuk()
        hCUK.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setHCuk((newValue as Boolean?)!!)
            true
        }

        // ApiActivity
        val apiAc = findPreference("apiAc")!!
        apiAc.onPreferenceClickListener = OnPreferenceClickListener {
            startActivity(Intent(activity, ApiAPPListActivity::class.java))
            true
        }

        // 自定义Hook
        val hook = findPreference("lyricHook")!!
        hook.summary = config!!.getHook()
        if (config!!.getHook() == "") {
            hook.summary = String.format("%s Hook", getString(R.string.Default))
        }
        hook.onPreferenceClickListener = OnPreferenceClickListener {
            val editText = EditText(activity)
            editText.setText(config!!.getHook())
            AlertDialog.Builder(activity).apply {
                setTitle(getString(R.string.CustomHookTips))
                setView(editText)
                setNegativeButton(
                    getString(R.string.Reset)
                ) { _, _ ->
                    hook.summary = String.format("%s Hook", getString(R.string.Default))
                    config!!.setHook("")
                    showToastOnLooper(activity, getString(R.string.ResetHookTips))
                }
                setPositiveButton(R.string.Ok) { _, _ ->
                    config!!.setHook(editText.text.toString())
                    hook.summary = editText.text.toString()
                    if (config!!.getHook() == "") {
                        hook.summary = String.format("%s Hook", getString(R.string.Default))
                    }
                    showToastOnLooper(
                        activity,
                        String.format(
                            "%s %s%s",
                            getString(R.string.HookSetTips),
                            config!!.getHook(),
                            getString(R.string.RestartSystemUI)
                        )
                    )
                }
                show()
            }
            true
        }

        // Debug模式
        val debug = (findPreference("debug") as SwitchPreference)
        debug.isChecked = config!!.getDebug()
        debug.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue: Any? ->
            config!!.setDebug((newValue as Boolean?)!!)
            true
        }


        // 重启SystemUI
        val reSystemUI = findPreference("restartUI")!!
        reSystemUI.onPreferenceClickListener = OnPreferenceClickListener {
            AlertDialog.Builder(activity).apply {
                setTitle(getString(R.string.RestartUI))
                setMessage(getString(R.string.RestartUITips))
                setPositiveButton(R.string.Ok) { _, _ ->
                    ShellUtils.voidShell("pkill -f com.android.systemui", true)
                    Analytics.trackEvent("重启SystemUI")
                }
                setNegativeButton(getString(R.string.Cancel), null)
                show()
            }
            true
        }

        // 重置模块
        val reset = findPreference("reset")!!
        reset.onPreferenceClickListener = OnPreferenceClickListener {
            AlertDialog.Builder(activity).apply {
                setTitle(getString(R.string.ResetModuleDialog))
                setMessage(getString(R.string.ResetModuleDialogTips))
                setPositiveButton(R.string.Ok) { _, _ ->
                    ActivityUtils.cleanConfig(
                        activity
                    )
                }
                setNegativeButton(getString(R.string.Cancel), null)
                show()
            }
            true
        }


        //检查更新
        val checkUpdate = findPreference("CheckUpdate")!!
        checkUpdate.summary =
            java.lang.String.format("%s：%s", getString(R.string.CurrentVer), BuildConfig.VERSION_NAME)
        checkUpdate.onPreferenceClickListener = OnPreferenceClickListener {
            showToastOnLooper(activity, getString(R.string.StartCheckUpdate))
            ActivityUtils.checkUpdate(activity)
            true
        }

        // 关于Activity
        val about = findPreference("about")!!
        about.onPreferenceClickListener = OnPreferenceClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
            true
        }

        // Test
        val test = findPreference("test")!!
        test.onPreferenceClickListener = OnPreferenceClickListener {
            AlertDialog.Builder(activity).apply {
                setTitle(R.string.Test)
                setMessage(R.string.TestDialogTips)
                setPositiveButton(R.string.Start) { _, _ ->
                    showToastOnLooper(activity, "尝试唤醒界面")
                    activity.sendBroadcast(
                        Intent().apply {
                            action = "Lyric_Server"
                            putExtra("Lyric_Type", "test")
                        }
                    )
                }
                setNegativeButton(R.string.Back, null)
                show()
            }
            true
        }

        // 非MIUI关闭功能
        if (!Utils.hasMiuiSetting) {
            hNoticeIcon.summary = String.format("%s%s", hNoticeIcon.summary, getString(R.string.YouNotMIUI))
            hNetWork.summary = String.format("%s%s", hNetWork.summary, getString(R.string.YouNotMIUI))
            hCUK.summary = String.format("%s%s", hCUK.summary, getString(R.string.YouNotMIUI))
        }
        val hookSure = IntentFilter()
        hookSure.addAction("Hook_Sure")
        activity.registerReceiver(HookReceiver(), hookSure)
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
                    AlertDialog.Builder(activity).apply {
                        setTitle(getString(R.string.HookSure))
                        setMessage(message)
                        setPositiveButton(getString(R.string.Ok), null)
                        create()
                        show()
                    }
                }
            } catch (e: RuntimeException) {
                e.printStackTrace()
            }
        }
    }
}
