package cn.fkj233.xposed.statusbarlyric.view.miuiview

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import cn.fkj233.xposed.statusbarlyric.R
import cn.fkj233.xposed.statusbarlyric.databinding.DialogLayoutBinding


class MIUIDialog(context: Context) : Dialog(context, R.style.CustomDialog) {

    private lateinit var binding: DialogLayoutBinding

    init {
        window?.setGravity(Gravity.BOTTOM)
        createView()
    }

    private fun createView() {
        binding = DialogLayoutBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
    }

    fun addView(mView: View) {
        binding.View.addView(mView)
    }

    override fun setTitle(title: CharSequence?) {
        binding.Title.text = title
    }

    override fun setTitle(titleId: Int) {
        binding.Title.setText(titleId)
    }

    fun setButton(text: CharSequence?, callBacks: (it: View) -> Unit) {
        binding.Button.apply {
            setText(text)
            setOnClickListener {
                callBacks(it)
            }
        }
    }

    fun setButton(textId: Int, callBacks: () -> Unit) {
        binding.Button.apply {
            setText(textId)
            setOnClickListener {
                callBacks()
            }
        }
    }

    fun setCancelButton(text: CharSequence?, callBacks: (it: View) -> Unit) {
        binding.CancelButton.apply {
            setText(text)
            setOnClickListener {
                callBacks(it)
            }
            visibility = View.VISIBLE
        }
    }

    fun setCancelButton(textId: Int, callBacks: () -> Unit) {
        binding.CancelButton.apply {
            setText(textId)
            setOnClickListener {
                callBacks()
            }
            visibility = View.VISIBLE
        }
    }

    override fun show() {
        window!!.setWindowAnimations(R.style.DialogAnim)
        super.show()
        val layoutParams = window!!.attributes
        layoutParams.dimAmount = 0.3F
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window!!.attributes = layoutParams
    }

    fun setMessage(textId: Int) {
        binding.Message.apply {
            setText(textId)
            visibility = View.VISIBLE
        }
    }

    fun setMessage(text: CharSequence?) {
        binding.Message.apply {
            this.text = text
            visibility = View.VISIBLE
        }
    }

    fun setEditText(text: String, hint: String) {
        binding.EditText.apply {
            setText(text.toCharArray(), 0, text.length)
            this.hint = hint
            visibility = View.VISIBLE
        }
    }

    fun getEditText(): String = binding.EditText.text.toString()
}
