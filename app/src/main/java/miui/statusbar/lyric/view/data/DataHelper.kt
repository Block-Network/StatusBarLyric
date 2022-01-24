package miui.statusbar.lyric.view.data

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.widget.EditText
import com.microsoft.appcenter.analytics.Analytics
import miui.statusbar.lyric.BuildConfig
import miui.statusbar.lyric.R
import miui.statusbar.lyric.utils.ActivityOwnSP
import miui.statusbar.lyric.utils.ShellUtils
import miui.statusbar.lyric.view.MIUIDialog

@SuppressLint("StaticFieldLeak")
object DataHelper {
    var isMenu = false
    lateinit var currentActivity: Activity

    fun getItems(): ArrayList<Item> = if (isMenu) loadMenuItems() else loadItems()

    private fun loadMenuItems(): ArrayList<Item> {
        val itemList = arrayListOf<Item>()
        itemList.apply {
            add(Item(Text(resId = R.string.ReStartSystemUI, onClickListener = {
                AlertDialog.Builder(currentActivity).apply {
                    setTitle(currentActivity.getString(R.string.RestartUI))
                    setMessage(currentActivity.getString(R.string.RestartUITips))
                    setPositiveButton(R.string.Ok) { _, _ ->
                        ShellUtils.voidShell("pkill -f com.android.systemui", true)
                        Analytics.trackEvent("重启SystemUI")
                    }
                    setNegativeButton(currentActivity.getString(R.string.Cancel), null)
                    show()
                }
            }), null))
            add(Item(Text("Module Version", isTitle = true), null, line = true))
            add(Item(Text("${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})-${BuildConfig.BUILD_TYPE}"), null))
        }
        return itemList
    }

    private fun loadItems(): ArrayList<Item> {
        val itemList = arrayListOf<Item>()
        itemList.apply {
            add(Item(Text(resId = R.string.AllSwitch), Switch("LService")))
            add(Item(Text(resId = R.string.LyricIcon), Switch("I")))
            add(Item(Text(resId = R.string.ReStartSystemUI, onClickListener = {
                MIUIDialog(currentActivity).apply {
                    setTitle("Test")
                    addView(EditText(currentActivity))
                    setButtonText("OK")
                    show()
                }
            }), null))
//            if (OwnSP.ownSP.getBoolean("simpleAnimation", false)) {
//                add(Item(Text(resId = R.string.SimpleWarn, textColor = Color.parseColor("#ff0c0c"), textSize = 4.5f), null))
//            }
//            if (!OwnSP.ownSP.getBoolean("simpleAnimation", false)) {
//                add(Item(Text(resId = R.string.SmoothAnimation), Switch("smoothAnimation")))
//                add(Item(Text(resId = R.string.TaskViewBlurLevel, onClickListener = {}), null)) // TODO Fix Dialog
//            }
//            add(Item(Text(resId = R.string.AnimationLevel, onClickListener = {}), null, line = true)) // TODO Fix Dialog
//
//            add(Item(Text(resId = R.string.AdvancedFeature, isTitle = true), null))
//            add(Item(Text(resId = R.string.UnlockGrids), Switch("unlockGrids")))
//            add(Item(Text(resId = R.string.ShowDockIconTitles), Switch("showDockIconTitles")))
//            add(Item(Text(resId = R.string.HideStatusBar), Switch("hideStatusBar")))
//            add(Item(Text(resId = R.string.MamlDownload), Switch("mamlDownload")))
//            add(Item(Text(resId = R.string.UnlockIcons), Switch("unlockIcons")))
//            if (!OwnSP.ownSP.getBoolean("simpleAnimation", false)) {
//                add(Item(Text(resId = R.string.WallpaperDarken), Switch("wallpaperDarken")))
//            }
//            add(Item(Text(resId = R.string.CategoryHideAll), Switch("categoryHideAll")))
//            add(Item(Text(resId = R.string.CategoryPagingHideEdit), Switch("CategoryPagingHideEdit")))
//            add(Item(Text(resId = R.string.IconTitleFontSize, onClickListener = {}), null)) // TODO Fix Dialog
//            add(Item(Text(resId = R.string.CustomTitleColor, onClickListener = {}), null)) // TODO Fix Dialog
//            add(Item(Text(resId = R.string.RoundCorner, onClickListener = {}), null)) // TODO Fix Dialog
//            add(Item(Text(resId = R.string.AppTextSize, onClickListener = {}), null)) // TODO Fix Dialog
//            add(Item(Text(resId = R.string.VerticalTaskViewOfAppCardSize, onClickListener = {}), null)) // TODO Fix Dialog
//            add(Item(Text(resId = R.string.HorizontalTaskViewOfAppCardSize, onClickListener = {}), null, line = true)) // TODO Fix Dialog
//
//            add(Item(Text(resId = R.string.Folder, isTitle = true), null))
//            if (!OwnSP.ownSP.getBoolean("simpleAnimation", false)) {
//                add(Item(Text(resId = R.string.BlurWhenOpenFolder), Switch("blurWhenOpenFolder")))
//            }
//            add(Item(Text(resId = R.string.CloseFolder), Switch("closeFolder")))
//            add(Item(Text(resId = R.string.FolderWidth), Switch("folderWidth")))
//            add(Item(Text(resId = R.string.FolderColumnsCount, onClickListener = {}), null, line = true)) // TODO Fix Dialog
//
//            if (XposedInit().checkWidgetLauncher()) {
//                add(Item(Text(resId = R.string.Widget, isTitle = true), null))
//                add(Item(Text(resId = R.string.HideWidgetTitles), Switch("hideWidgetTitles")))
//                add(Item(Text(resId = R.string.WidgetToMinus), Switch("widgetToMinus")))
//                add(Item(Text(resId = R.string.AlwaysShowMIUIWidget), Switch("alwaysShowMIUIWidget"), line = true))
//            }
//
//            if (XposedInit.hasHookPackageResources) {
//                add(Item(Text(resId = R.string.ResourceHooks, isTitle = true), null))
//                add(Item(Text(resId = R.string.HideTaskViewAppIcon), Switch("buttonPadding")))
//                add(Item(Text(resId = R.string.HideTaskViewCleanUpIcon), Switch("cleanUp")))
//                add(Item(Text(resId = R.string.HideTaskViewSmallWindowIcon), Switch("smallWindow")))
//                add(Item(Text(resId = R.string.TaskViewAppCardTextSize, onClickListener = {}), null)) // TODO Fix Dialog
//                add(Item(Text(resId = R.string.CustomRecentText, onClickListener = {}), null, line = true)) // TODO Fix Dialog
//            }
//
//            add(Item(Text(resId = R.string.TestFeature, isTitle = true), null))
//            add(Item(Text(resId = R.string.SimpleAnimation), Switch("simpleAnimation", onCheckedChangeListener = { _, _ -> currentActivity?.recreate() })))
//            add(Item(Text(resId = R.string.AppReturnAmin), Switch("appReturnAmin", onCheckedChangeListener = { _, _ -> currentActivity?.recreate()})))
//            add(Item(Text(resId = R.string.InfiniteScroll), Switch("infiniteScroll")))
//            add(Item(Text(resId = R.string.RecommendServer), Switch("recommendServer")))
//            add(Item(Text(resId = R.string.HideSeekPoints), Switch("hideSeekPoints")))
//            add(Item(Text(resId = R.string.SmallWindow), Switch("supportSmallWindow")))
//            add(Item(Text(resId = R.string.LowEndAnim), Switch("lowEndAnim")))
//            add(Item(Text(resId = R.string.LowEndDeviceUseMIUIWidgets), Switch("useMIUIWidgets")))
//            if (!OwnSP.ownSP.getBoolean("appReturnAmin", false)) {
//                add(Item(Text(resId = R.string.BlurRadius, onClickListener = {}), null)) // TODO Fix Dialog
//            }
//            add(Item(line = true))
//
//            add(Item(Text(resId = R.string.OtherFeature, isTitle = true), null))
//            add(Item(Text(resId = R.string.AlwaysShowStatusBarClock), Switch("clockGadget")))
//            add(Item(Text(resId = R.string.DoubleTap), Switch("doubleTap")))
//            if (!OwnSP.ownSP.getBoolean("dockSettings", false) && (Config.AndroidSDK == 30)) {
//                add(Item(Text(resId = R.string.SearchBarBlur), Switch("searchBarBlur")))
//            }
//            add(Item(Text(resId = R.string.DockSettings, onClickListener = {}), null)) // TODO Fix Dialog
//            add(Item(Text(resId = R.string.EveryThingBuild, onClickListener = { BuildWithEverything().init() }), null, line = true))
//
//            add(Item(Text(resId = R.string.BrokenFeature, isTitle = true), null))
//            add(Item(Text(resId = R.string.RealTaskViewHorizontal), Switch("horizontal")))
//            add(Item(Text(resId = R.string.EnableIconShadow), Switch("isEnableIconShadow"), line = true))
//
//            add(Item(Text(resId = R.string.ModuleFeature, isTitle = true), null))
//            add(Item(Text(resId = R.string.CleanModuleSettings, onClickListener = {}), null)) // TODO Fix Dialog
        }
        return itemList
    }
}