package miui.statusbar.lyric.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import miui.statusbar.lyric.R
import miui.statusbar.lyric.utils.ActivityUtils
import miui.statusbar.lyric.view.data.Item
import miui.statusbar.lyric.view.miuiview.SettingsSwitch


class ItemAdapter(private val itemList: List<Item>): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val settingsText: TextView = view.findViewById(R.id.settings_text)
        val settingSwitch: SettingsSwitch = view.findViewById(R.id.settings_switch)
        val settingLine: View = view.findViewById(R.id.settings_line)
        val settingSeekBar: SeekBar = view.findViewById(R.id.settings_seekbar)
        val rightArrow: ImageView = view.findViewById(R.id.RightArrow)
        val layout: LinearLayout = view.findViewById(R.id.layout)

        val spinnerSelect: TextView = view.findViewById(R.id.settings_select)
        val spinnerLayout: LinearLayout = view.findViewById(R.id.settings_select_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("RtlHardcoded")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        val textInfo = item.text
        val switchInfo = item.switch
        val seekBarInfo = item.seekBar
        val spinnerInfo = item.spinner
        val context = holder.settingsText.context

        textInfo?.let {
            textInfo.text?.let { holder.settingsText.text = it }
            textInfo.resId?.let { holder.settingsText.setText(it) }
            textInfo.textSize?.let { holder.settingsText.textSize = sp2px(context, it) }
            textInfo.textColor?.let { holder.settingsText.setTextColor(it) }
            textInfo.onClickListener?.let { holder.layout.setOnClickListener(it) }
            if (textInfo.showArrow && switchInfo == null && seekBarInfo == null && spinnerInfo == null) {
                holder.rightArrow.visibility = View.VISIBLE
            }
            if (textInfo.isTitle) {
                holder.settingsText.textSize = sp2px(context, 4.5f)
                holder.settingsText.setTextColor(Color.parseColor("#9399b3"))
            }
            holder.settingsText.visibility = View.VISIBLE
        }

        switchInfo?.let {
            switchInfo.onCheckedChangeListener?.let { holder.settingSwitch.customCheckedChangeListener = it }
            if (! switchInfo.key.isNullOrEmpty()) {
                holder.settingSwitch.key = switchInfo.key
                holder.settingSwitch.visibility = View.VISIBLE
            }
        }

        seekBarInfo?.let {
            seekBarInfo.callBacks?.let { holder.settingSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    it(p1, holder.settingsText)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }) }
            seekBarInfo.min?.let { holder.settingSeekBar.min = it }
            seekBarInfo.max?.let { holder.settingSeekBar.max = it }
            seekBarInfo.progress?.let { holder.settingSeekBar.progress = it }
            holder.settingSeekBar.visibility = View.VISIBLE
        }

        spinnerInfo?.let {
            val mListPop = ListPopupWindow(spinnerInfo.context)
            mListPop.setBackgroundDrawable(spinnerInfo.context.getDrawable(R.drawable.rounded_corners_pop))
            mListPop.setAdapter(ArrayAdapter(spinnerInfo.context, android.R.layout.simple_list_item_1, spinnerInfo.array))
            mListPop.verticalOffset = -ActivityUtils.dp2px(100F)
            mListPop.width = ActivityUtils.dp2px(150F)
            mListPop.height = ViewGroup.LayoutParams.WRAP_CONTENT
            mListPop.isModal = true

            mListPop.setOnItemClickListener { parent, _, position, _ ->
                holder.spinnerSelect.text = parent.getItemAtPosition(position).toString()
                spinnerInfo.callBacks?.let { it1 -> it1(parent.getItemAtPosition(position).toString()) }
                mListPop.dismiss()
            }
            holder.spinnerLayout.setOnClickListener {
                mListPop.horizontalOffset = 0
                mListPop.setDropDownGravity(Gravity.RIGHT)
                mListPop.anchorView = it
                mListPop.show()
            }
            holder.layout.setOnClickListener {
                mListPop.horizontalOffset = ActivityUtils.dp2px(24F)
                mListPop.setDropDownGravity(Gravity.LEFT)
                mListPop.anchorView = it
                mListPop.show()
            }
            spinnerInfo.select?.let { it1 -> holder.spinnerSelect.text = it1 }
            holder.spinnerLayout.visibility = View.VISIBLE
        }

        if (item.line) {
            holder.settingLine.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = itemList.size

    private fun sp2px(context: Context, spValue: Float): Float = (context.resources.displayMetrics.scaledDensity * spValue + 0.5f)

    private fun getSystemColor(context: Context): Int {
        val typedValue = TypedValue()
        val contextThemeWrapper = ContextThemeWrapper(context, android.R.style.Theme_DeviceDefault)
        contextThemeWrapper.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
        return typedValue.data
    }

}