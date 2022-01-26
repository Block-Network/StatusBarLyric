package cn.fkj233.xposed.statusbarlyric.view.miuiview

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.xposed.statusbarlyric.R


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

    fun setButton(text: CharSequence?, callBacks: (it: View) -> Unit) {
        view.findViewById<Button>(R.id.Button).apply {
            setText(text)
            setOnClickListener {
                callBacks(it)
            }
        }
    }

    fun setButton(textId: Int, callBacks: () -> Unit) {
        view.findViewById<Button>(R.id.Button).apply {
            setText(textId)
            setOnClickListener {
                callBacks()
            }
        }
    }

    fun setCancelButton(text: CharSequence?, callBacks: (it: View) -> Unit) {
        view.findViewById<Button>(R.id.CancelButton).apply {
            setText(text)
            setOnClickListener {
                callBacks(it)
            }
            visibility = View.VISIBLE
        }
    }

    fun setCancelButton(textId: Int, callBacks: () -> Unit) {
        view.findViewById<Button>(R.id.CancelButton).apply {
            setText(textId)
            setOnClickListener {
                callBacks()
            }
            visibility = View.VISIBLE
        }
    }

    override fun show() {
        super.show()
        val layoutParams = window!!.attributes
        layoutParams.dimAmount = 0.3F
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = layoutParams
    }

    fun setMessage(textId: Int) {
        view.findViewById<TextView>(R.id.Message).apply {
            setText(textId)
            visibility = View.VISIBLE
        }
    }

    fun setMessage(text: CharSequence?) {
        view.findViewById<TextView>(R.id.Message).apply {
            this.text = text
            visibility = View.VISIBLE
        }
    }

    fun setEditText(text: String, hint: String) {
        view.findViewById<EditText>(R.id.EditText).apply {
            setText(text.toCharArray(), 0, text.length)
            this.hint = hint
            visibility = View.VISIBLE
        }
    }

    fun getEditText(): String = view.findViewById<EditText>(R.id.EditText).text.toString()
}
