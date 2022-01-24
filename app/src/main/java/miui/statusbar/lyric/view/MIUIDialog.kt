package miui.statusbar.lyric.view

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import miui.statusbar.lyric.R


class MIUIDialog(context: Context) : Dialog(context, R.style.CustomDialog) {
    var view: View

    init {
        window?.setGravity(Gravity.BOTTOM)
        view = createView(context, R.layout.dialog_layout)
    }

    private fun createView(context: Context, dialog_layout: Int): View {
        val inflate: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflate.inflate(dialog_layout, null)
        setContentView(view)
        return view
    }

    fun addView(mView: View) {
        view.findViewById<LinearLayout>(R.id.View).addView(mView)
    }

    override fun setTitle(title: CharSequence?) {
        view.findViewById<TextView>(R.id.Title).text = title
    }

    override fun setTitle(titleId: Int) {
        view.findViewById<TextView>(R.id.Title).setText(titleId)
    }

    fun setButtonText(text: CharSequence?) {
        view.findViewById<Button>(R.id.Button).text = text
    }

    fun setButtonText(textId: Int) {
        view.findViewById<Button>(R.id.Button).setText(textId)
    }

    override fun show() {
        super.show()
        val layoutParams = window!!.attributes
        layoutParams.dimAmount = 0.3F
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = layoutParams
    }

}
