package statusbar.lyric.activity.page

import android.text.InputFilter
import android.text.InputType
import android.view.View
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.changeConfig

@BMPage
class ExtendPage : BasePage() {
    override fun onCreate() {
        val indexMaps: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>().apply {
            this[0] = getString(R.string.add_location_start)
            this[1] = getString(R.string.add_location_end)
        }
        TextSSp(textId = R.string.lyric_add_location, currentValue = indexMaps[ActivityOwnSP.config.viewIndex].toString(), data = {
            indexMaps.forEach {
                add(it.value) { ActivityOwnSP.config.viewIndex = it.key }
            }
        })
        TextSw(textId = R.string.hide_notification_icon, key = "hideNotificationIcon", onClickListener = { changeConfig() })
        TextSSw(textId = R.string.limit_visibility_change, tipsId = R.string.limit_visibility_change_tips, key = "limitVisibilityChange")
        TextSw(textId = R.string.hide_lyric_when_lock_screen, key = "hideLyricWhenLockScreen", defValue = true)
        val lyricColorScheme: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>().apply {
            this[0] = getString(R.string.color_scheme1)
            this[1] = getString(R.string.color_scheme2)
        }
        TextSSp(textId = R.string.lyric_color_scheme, currentValue = lyricColorScheme[ActivityOwnSP.config.lyricColorScheme].toString(), data = {
            lyricColorScheme.forEach {
                add(it.value) { ActivityOwnSP.config.lyricColorScheme = it.key }
            }
        })
        TextSw(textId = R.string.dynamic_lyric_speed, key = "dynamicLyricSpeed", onClickListener = { changeConfig() })
        TextSw(textId = R.string.click_status_bar_to_hide_lyric, key = "clickStatusBarToHideLyric")
        TextSw(textId = R.string.long_click_status_bar_stop, key = "longClickStatusBarStop")
        Line()
        val lyricBlurredEdgesRadiusBinding = GetDataBinding({ ActivityOwnSP.config.lyricBlurredEdges }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSSw(textId = R.string.lyric_blurred_edges, key = "lyricBlurredEdges", defValue = false, onClickListener = {
            lyricBlurredEdgesRadiusBinding.send(it)
        })
        TextSA(textId = R.string.lyric_blurred_edges_radius, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.lyric_blurred_edges_radius))
                setMessage(getString(R.string.lyric_blurred_edges_radius_tips))
                setEditText(ActivityOwnSP.config.lyricBlurredEdgesRadius.toString(), "40", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(3))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 0..100) {
                            ActivityOwnSP.config.lyricBlurredEdgesRadius = value
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = lyricBlurredEdgesRadiusBinding.binding.getRecv(1))
        val lyricBlurredEdgesType: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>().apply {
            this[0] = getString(R.string.lyric_blurred_edges_type_all)
            this[1] = getString(R.string.lyric_blurred_edges_type_start)
            this[2] = getString(R.string.lyric_blurred_edges_type_end)
        }
        TextSSp(textId = R.string.lyric_blurred_edges_type, currentValue = lyricBlurredEdgesType[ActivityOwnSP.config.lyricBlurredEdgesType].toString(), data = {
            lyricBlurredEdgesType.forEach {
                add(it.value) { ActivityOwnSP.config.lyricBlurredEdgesType = it.key }
            }
        }, dataBindingRecv = lyricBlurredEdgesRadiusBinding.binding.getRecv(1))
        Line()
        val slideStatusBarCutSongsBinding = GetDataBinding({ ActivityOwnSP.config.slideStatusBarCutSongs }) { view, _, data ->
            view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
        }
        TextSw(textId = R.string.slide_status_bar_cut_songs, key = "slideStatusBarCutSongs", onClickListener = { slideStatusBarCutSongsBinding.send(it) })
        TextSA(textId = R.string.slide_status_bar_cut_songs_x_radius, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.slide_status_bar_cut_songs_x_radius))
                setMessage(getString(R.string.slide_status_bar_cut_songs_x_radius_tips))
                setEditText(ActivityOwnSP.config.slideStatusBarCutSongsXRadius.toString(), "150", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 20..2000) {
                            ActivityOwnSP.config.slideStatusBarCutSongsXRadius = value
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = slideStatusBarCutSongsBinding.binding.getRecv(1))
        TextSA(textId = R.string.slide_status_bar_cut_songs_y_radius, onClickListener = {
            MIUIDialog(activity) {
                setTitle(getString(R.string.slide_status_bar_cut_songs_y_radius))
                setMessage(getString(R.string.slide_status_bar_cut_songs_y_radius_tips))
                setEditText(ActivityOwnSP.config.slideStatusBarCutSongsYRadius.toString(), "25", config = {
                    it.inputType = InputType.TYPE_CLASS_NUMBER
                    it.filters = arrayOf(InputFilter.LengthFilter(4))
                })
                setRButton(getString(R.string.ok)) {
                    try {
                        val value = getEditText().toInt()
                        if (value in 10..100) {
                            ActivityOwnSP.config.slideStatusBarCutSongsYRadius = value
                        } else {
                            throw Exception()
                        }
                    } catch (_: Exception) {
                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
                    }
                }
                setLButton(getString(R.string.cancel))
                finally { dismiss() }
            }.show()
        }, dataBindingRecv = slideStatusBarCutSongsBinding.binding.getRecv(1))
        Line()
    }
}