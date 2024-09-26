//package statusbar.lyric.activity.page
//
//import android.text.InputFilter
//import android.text.InputType
//import android.view.View
//import statusbar.lyric.R
//import statusbar.lyric.config.ActivityOwnSP
//import statusbar.lyric.tools.ActivityTools
//
//class ExtendPage : BasePage() {
//    override fun onCreate() {
//        val indexMaps: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>().apply {
//            this[0] = getString(R.string.add_location_start)
//            this[1] = getString(R.string.add_location_end)
//        }
//        TextSSp(textId = R.string.lyric_add_location, currentValue = indexMaps[ActivityOwnSP.config.viewIndex].toString(), data = {
//            indexMaps.forEach {
//                add(it.value) { ActivityOwnSP.config.viewIndex = it.key }
//            }
//        })
//        Line()
//        val lyricBlurredEdgesRadiusBinding = GetDataBinding({ ActivityOwnSP.config.lyricBlurredEdges }) { view, _, data ->
//            view.visibility = if (data) View.VISIBLE else View.GONE
//        }
//        TextSSw(
//            textId = R.string.lyric_blurred_edges,
//            key = "lyricBlurredEdges",
//            defValue = false,
//            onClickListener = { lyricBlurredEdgesRadiusBinding.send(it) })
//        TextSA(textId = R.string.lyric_blurred_edges_radius, onClickListener = {
//            MIUIDialog(activity) {
//                setTitle(getString(R.string.lyric_blurred_edges_radius))
//                setMessage(getString(R.string.lyric_blurred_edges_radius_tips))
//                setEditText(ActivityOwnSP.config.lyricBlurredEdgesRadius.toString(), "40", config = {
//                    it.inputType = InputType.TYPE_CLASS_NUMBER
//                    it.filters = arrayOf(InputFilter.LengthFilter(3))
//                })
//                setRButton(getString(R.string.ok)) {
//                    try {
//                        val value = getEditText().toInt()
//                        if (value in 0..100) {
//                            ActivityOwnSP.config.lyricBlurredEdgesRadius = value
//                        } else {
//                            throw Exception()
//                        }
//                    } catch (_: Exception) {
//                        ActivityTools.showToastOnLooper(getString(R.string.input_error))
//                    }
//                }
//                setLButton(getString(R.string.cancel))
//                finally { dismiss() }
//            }.show()
//        }, dataBindingRecv = lyricBlurredEdgesRadiusBinding.binding.getRecv(1))
//        val lyricBlurredEdgesType: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>().apply {
//            this[0] = getString(R.string.lyric_blurred_edges_type_all)
//            this[1] = getString(R.string.lyric_blurred_edges_type_start)
//            this[2] = getString(R.string.lyric_blurred_edges_type_end)
//        }
//        TextSSp(
//            textId = R.string.lyric_blurred_edges_type,
//            currentValue = lyricBlurredEdgesType[ActivityOwnSP.config.lyricBlurredEdgesType].toString(),
//            data = {
//                lyricBlurredEdgesType.forEach {
//                    add(it.value) { ActivityOwnSP.config.lyricBlurredEdgesType = it.key }
//                }
//            },
//            dataBindingRecv = lyricBlurredEdgesRadiusBinding.binding.getRecv(1)
//        )
//    }
//}