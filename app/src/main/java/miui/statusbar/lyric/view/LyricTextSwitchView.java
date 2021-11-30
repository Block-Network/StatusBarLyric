package miui.statusbar.lyric.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

@SuppressLint("ViewConstructor")
public class LyricTextSwitchView extends ViewFlipper {

    LyricTextView lyricTextView;
    LyricTextView lyricTextView2;
    AutoMarqueeTextView autoMarqueeTextView;
    AutoMarqueeTextView autoMarqueeTextView2;
    boolean switchLyric = false;
    boolean hasMeizu;

    public LyricTextSwitchView(Context context, boolean isMeizu) {
        super(context);
        hasMeizu = isMeizu;
        if (isMeizu) {
            lyricTextView = new LyricTextView(context);
            lyricTextView2 = new LyricTextView(context);

            lyricTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            lyricTextView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            this.addView(lyricTextView);
            this.addView(lyricTextView2);
        } else {
            autoMarqueeTextView = new AutoMarqueeTextView(context);
            autoMarqueeTextView2 = new AutoMarqueeTextView(context);

            autoMarqueeTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            autoMarqueeTextView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            autoMarqueeTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            autoMarqueeTextView2.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            this.addView(autoMarqueeTextView);
            this.addView(autoMarqueeTextView2);
        }
    }

    public void setText(String str) {
        // 设置歌词文本
        if (switchLyric) {
            if (hasMeizu) {
                lyricTextView.setTextT(str);
            } else {
                autoMarqueeTextView.setText(str);
            }
            switchLyric = false;
        } else {
            if (hasMeizu) {
                lyricTextView2.setTextT(str);
            } else {
                autoMarqueeTextView2.setText(str);
            }
            switchLyric = true;
        }

        this.showNext();
    }

    public void setWidth(int i) {
        if (hasMeizu) {
            lyricTextView.setWidth(i);
            lyricTextView2.setWidth(i);
        } else {
            autoMarqueeTextView.setWidth(i);
            autoMarqueeTextView2.setWidth(i);
        }
    }

    public void setTextColor(int i) {
        if (hasMeizu) {
            lyricTextView.setTextColor(i);
            lyricTextView2.setTextColor(i);
        } else {
            autoMarqueeTextView.setTextColor(i);
            autoMarqueeTextView2.setTextColor(i);
        }
    }

    public void setSourceText(CharSequence charSequence) {
        if (hasMeizu) {
            lyricTextView.setText(charSequence);
            lyricTextView2.setText(charSequence);
        } else {
            autoMarqueeTextView.setText(charSequence);
            autoMarqueeTextView2.setText(charSequence);
        }
    }

    public void setSpeed(float f) {
        if (hasMeizu) {
            lyricTextView.setSpeed(f);
            lyricTextView2.setSpeed(f);
        }
    }

    public CharSequence getText() {
        if (switchLyric) {
            if (hasMeizu) {
                return lyricTextView.getText();
            } else {
                return autoMarqueeTextView.getText();
            }
        } else {
            if (hasMeizu) {
                return lyricTextView2.getText();
            } else {
                return autoMarqueeTextView2.getText();
            }
        }
    }

    public TextPaint getPaint() {
        if (switchLyric) {
            if (hasMeizu) {
                return lyricTextView.getPaint();
            } else {
                return autoMarqueeTextView.getPaint();
            }
        } else {
            if (hasMeizu) {
                return lyricTextView2.getPaint();
            } else {
                return autoMarqueeTextView2.getPaint();
            }
        }
    }

    public void setHeight(int i) {
        if (hasMeizu) {
            lyricTextView.setHeight(i);
            lyricTextView2.setHeight(i);
        } else {
            autoMarqueeTextView.setHeight(i);
            autoMarqueeTextView2.setHeight(i);
        }
    }

    public void setTypeface(Typeface typeface) {
        if (hasMeizu) {
            lyricTextView.setTypeface(typeface);
            lyricTextView2.setTypeface(typeface);
        } else {
            autoMarqueeTextView.setTypeface(typeface);
            autoMarqueeTextView2.setTypeface(typeface);
        }
    }

    public void setTextSize(int i, float f) {
        if (hasMeizu) {
            lyricTextView.setTextSize(i, f);
            lyricTextView2.setTextSize(i, f);
        } else {
            autoMarqueeTextView.setTextSize(i, f);
            autoMarqueeTextView2.setTextSize(i, f);
        }
    }

    public void setMargins(int i, int i1, int i2, int i3) {
        if (hasMeizu) {
            FrameLayout.LayoutParams lyricParams = (FrameLayout.LayoutParams) lyricTextView.getLayoutParams();
            lyricParams.setMargins(i, i1, i2, i3);
            lyricTextView.setLayoutParams(lyricParams);

            FrameLayout.LayoutParams lyricParams2 = (FrameLayout.LayoutParams) lyricTextView.getLayoutParams();
            lyricParams2.setMargins(i, i1, i2, i3);
            lyricTextView2.setLayoutParams(lyricParams2);
        } else {
            FrameLayout.LayoutParams lyricParams = (FrameLayout.LayoutParams) autoMarqueeTextView.getLayoutParams();
            lyricParams.setMargins(i, i1, i2, i3);
            autoMarqueeTextView.setLayoutParams(lyricParams);

            FrameLayout.LayoutParams lyricParams2 = (FrameLayout.LayoutParams) autoMarqueeTextView2.getLayoutParams();
            lyricParams2.setMargins(i, i1, i2, i3);
            autoMarqueeTextView2.setLayoutParams(lyricParams2);
        }
    }

    public void setMarqueeRepeatLimit(int i) {
        if (!hasMeizu) {
            autoMarqueeTextView.setMarqueeRepeatLimit(i);
            autoMarqueeTextView2.setMarqueeRepeatLimit(i);
        }
    }

    public void setSingleLine(boolean bool) {
        if (hasMeizu) {
            lyricTextView.setSingleLine(bool);
            lyricTextView2.setSingleLine(bool);
        } else {
            autoMarqueeTextView.setSingleLine(bool);
            autoMarqueeTextView2.setSingleLine(bool);
        }
    }

    public void setMaxLines(int i) {
        if (hasMeizu) {
            lyricTextView.setMaxLines(i);
            lyricTextView2.setMaxLines(i);
        } else {
            autoMarqueeTextView.setMaxLines(i);
            autoMarqueeTextView2.setMaxLines(i);
        }
    }
}
