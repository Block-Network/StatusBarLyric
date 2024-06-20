package statusbar.lyric.activity.page

import android.graphics.Bitmap
import android.graphics.Color
import android.text.InputFilter
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.activity.MIUIActivity.Companion.context
import cn.fkj233.ui.activity.annotation.BMPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.dialog.MIUIDialog
import cn.xiaowine.xkt.AcTool.openURL
import cn.xiaowine.xkt.Tool.shiftString2
import cn.xiaowine.xkt.ViewTool.getRoundedCornerBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import statusbar.lyric.R
import statusbar.lyric.tools.ShellTools.b
import statusbar.lyric.tools.ShellTools.getFakeBootID
import statusbar.lyric.tools.ShellTools.isA
import statusbar.lyric.tools.ShellTools.saveSeniorModeInfo

@BMPage
class PremiumPage : BasePage() {
    private val mAliPay = "kwwsv=22tu1dolsd|1frp2in{435<3|tz}wiy7rvknxfi".shiftString2()
    private val mWxPay = "z{s=22i5i3rWe]0yv|w<bV4Z]k<W{gVzgQfMdl<xgU3HzE:Gw}WtbV|9Hi3T9zQIhmltPvd:F9".shiftString2()

    override fun onCreate() {
        val aBinding = GetDataBinding({ isA }) { view, i, data ->
            if (i == 2) {
                (view as TextView).setText(if (data as Boolean) R.string.already_donated else R.string.not_donated)
                return@GetDataBinding
            }
            view.visibility = if (data as Boolean) View.GONE else View.VISIBLE
        }
        TitleText(textId = R.string.give_op)
        Text(textId = if (isA) R.string.already_donated else R.string.not_donated, dataBindingRecv = aBinding.getRecv(2), colorInt = Color.MAGENTA)
        Line()
        TextSA(textId = R.string.see_how_to_donate, onClickListener = {
            MIUIDialog(context) {
                setTitle(context.getString(R.string.donate))
                setRButton(context.getText(R.string.alipay)) {
                    mAliPay.openURL()
                    dismiss()
                }
                setLButton(context.getText(R.string.wechat)) {
                    MIUIDialog(MIUIActivity.context) {
                        setTitle(R.string.wechat)
                        addView(ImageView(context).apply {
                            setImageBitmap(createQRCodeImage(mWxPay))
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        })
                        setRButton(context.getText(R.string.ok)) {
                            dismiss()
                        }
                    }.show()
                    dismiss()
                }
            }.show()
        })
        TextSA(textId = R.string.enter_the_activation_code, dataBindingRecv = aBinding.getRecv(1), onClickListener = {
            MIUIDialog(context) {
                setTitle(R.string.activationCode)
                setMessage(R.string.enter_the_activation_code_tips)
                setEditText("", getString(R.string.activationCode), config = {
                    it.filters = arrayOf(InputFilter.LengthFilter(64))
                })
                setRButton(R.string.activation) {
                    val c = b(true) == getEditText()
                    aBinding.send(c)
                    isA = c
                    getEditText().saveSeniorModeInfo(true)
                    dismiss()
                }
                setLButton(getString(R.string.cancel)) {
                    dismiss()
                }
            }.show()
        })
        TextSA(textId = R.string.get_the_feature_code, dataBindingRecv = aBinding.getRecv(1), onClickListener = {
            MIUIDialog(context) {
                setTitle(R.string.feature_code)
                setEditText(getFakeBootID(true).toString(), "")
                setLButton(getString(R.string.ok)) {
                    dismiss()
                }
            }.show()
        })
    }

    private fun createQRCodeImage(text: String): Bitmap {
        val width = 700
        val color = Color.WHITE
        val color1 = Color.BLACK
        val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, width, mapOf(EncodeHintType.CHARACTER_SET to "utf-8", EncodeHintType.MARGIN to 1))
        val pixels = IntArray(width * width) { index ->
            val x = index % width
            val y = index / width
            if (bitMatrix[x, y]) {
                color
            } else {
                color1
            }
        }
        val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, width)
        }.getRoundedCornerBitmap(30f)
        return bitmap
    }
}