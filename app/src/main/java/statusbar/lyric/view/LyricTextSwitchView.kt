package statusbar.lyric.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.ViewFlipper
import statusbar.lyric.utils.LogUtils

@SuppressLint("ViewConstructor")
class LyricTextSwitchView(context: Context?, private var hasMeizu: Boolean) : ViewFlipper(context) {
    private var lyricTextView: LyricTextView = LyricTextView(context)
    private var lyricTextView2: LyricTextView = LyricTextView(context)
    private var autoMarqueeTextView: AutoMarqueeTextView = AutoMarqueeTextView(context)
    private var autoMarqueeTextView2: AutoMarqueeTextView = AutoMarqueeTextView(context)
    var switchLyric = false

    init {
        if (hasMeizu) {
            lyricTextView.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            lyricTextView2.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            this.addView(lyricTextView)
            this.addView(lyricTextView2)
        } else {
            autoMarqueeTextView.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            autoMarqueeTextView2.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            autoMarqueeTextView.ellipsize = TextUtils.TruncateAt.MARQUEE
            autoMarqueeTextView2.ellipsize = TextUtils.TruncateAt.MARQUEE
            this.addView(autoMarqueeTextView)
            this.addView(autoMarqueeTextView2)
        }
    }

    fun setText(str: String) {
        LogUtils.e("设置动画歌词 $str | $switchLyric")
        // 设置歌词文本
        if (switchLyric) {
            if (hasMeizu) {
                lyricTextView.setTextT(str)
            } else {
                autoMarqueeTextView.text = str
            }
            switchLyric = false
        } else {
            if (hasMeizu) {
                lyricTextView2.setTextT(str)
            } else {
                autoMarqueeTextView2.text = str
            }
            switchLyric = true
        }
        showNext()
    }

    fun setWidth(i: Int) {
        if (hasMeizu) {
            lyricTextView.width = i
            lyricTextView2.width = i
        } else {
            autoMarqueeTextView.width = i
            autoMarqueeTextView2.width = i
        }
    }

    fun setTextColor(i: Int) {
        if (hasMeizu) {
            lyricTextView.setTextColor(i)
            lyricTextView2.setTextColor(i)
        } else {
            autoMarqueeTextView.setTextColor(i)
            autoMarqueeTextView2.setTextColor(i)
        }
    }

    fun setSourceText(charSequence: CharSequence?) {
        LogUtils.e("设置歌词 $charSequence")
        if (hasMeizu) {
            lyricTextView.text = charSequence
            lyricTextView2.text = charSequence
        } else {
            autoMarqueeTextView.text = charSequence
            autoMarqueeTextView2.text = charSequence
        }
    }

    fun setSpeed(f: Float) {
        if (hasMeizu) {
            lyricTextView.setSpeed(f)
            lyricTextView2.setSpeed(f)
        }
    }

    val text: CharSequence
        get() = if (switchLyric) {
            if (hasMeizu) {
                lyricTextView.text
            } else {
                autoMarqueeTextView.text
            }
        } else {
            if (hasMeizu) {
                lyricTextView2.text
            } else {
                autoMarqueeTextView2.text
            }
        }
    val paint: TextPaint
        get() = if (switchLyric) {
            if (hasMeizu) {
                lyricTextView.paint
            } else {
                autoMarqueeTextView.paint
            }
        } else {
            if (hasMeizu) {
                lyricTextView2.paint
            } else {
                autoMarqueeTextView2.paint
            }
        }

    fun setHeight(i: Int) {
        if (hasMeizu) {
            lyricTextView.height = i
            lyricTextView2.height = i
        } else {
            autoMarqueeTextView.height = i
            autoMarqueeTextView2.height = i
        }
    }

    fun setTypeface(typeface: Typeface?) {
        if (hasMeizu) {
            lyricTextView.typeface = typeface
            lyricTextView2.typeface = typeface
        } else {
            autoMarqueeTextView.typeface = typeface
            autoMarqueeTextView2.typeface = typeface
        }
    }

    fun setTextSize(i: Int, f: Float) {
        if (hasMeizu) {
            lyricTextView.setTextSize(i, f)
            lyricTextView2.setTextSize(i, f)
        } else {
            autoMarqueeTextView.setTextSize(i, f)
            autoMarqueeTextView2.setTextSize(i, f)
        }
    }

    fun setMargins(i: Int, i1: Int, i2: Int, i3: Int) {
        if (hasMeizu) {
            val lyricParams = lyricTextView.layoutParams as LayoutParams
            lyricParams.setMargins(i, i1, i2, i3)
            lyricTextView.layoutParams = lyricParams
            val lyricParams2 = lyricTextView.layoutParams as LayoutParams
            lyricParams2.setMargins(i, i1, i2, i3)
            lyricTextView2.layoutParams = lyricParams2
        } else {
            val lyricParams = autoMarqueeTextView.layoutParams as LayoutParams
            lyricParams.setMargins(i, i1, i2, i3)
            autoMarqueeTextView.layoutParams = lyricParams
            val lyricParams2 = autoMarqueeTextView2.layoutParams as LayoutParams
            lyricParams2.setMargins(i, i1, i2, i3)
            autoMarqueeTextView2.layoutParams = lyricParams2
        }
    }

    fun setMarqueeRepeatLimit(i: Int) {
        if (!hasMeizu) {
            autoMarqueeTextView.marqueeRepeatLimit = i
            autoMarqueeTextView2.marqueeRepeatLimit = i
        }
    }

    fun setSingleLine(bool: Boolean) {
        if (hasMeizu) {
            lyricTextView.isSingleLine = bool
            lyricTextView2.isSingleLine = bool
        } else {
            autoMarqueeTextView.isSingleLine = bool
            autoMarqueeTextView2.isSingleLine = bool
        }
    }

    fun setMaxLines(i: Int) {
        if (hasMeizu) {
            lyricTextView.maxLines = i
            lyricTextView2.maxLines = i
        } else {
            autoMarqueeTextView.maxLines = i
            autoMarqueeTextView2.maxLines = i
        }
    }
}