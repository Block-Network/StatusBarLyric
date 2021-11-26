package miui.statusbar.lyric.view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class LyricTextSwitchView extends ViewFlipper {

    LyricTextView lyricTextView;
    LyricTextView lyricTextView2;
    boolean switchLyric = false;

    public LyricTextSwitchView(Context context) {
        super(context);
        lyricTextView = new LyricTextView(context);
        lyricTextView2 = new LyricTextView(context);
        lyricTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        lyricTextView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        lyricTextView.setSingleLine(true);
        lyricTextView.setMaxLines(1);
        lyricTextView2.setSingleLine(true);
        lyricTextView2.setMaxLines(1);
        this.addView(lyricTextView);
        this.addView(lyricTextView2);
    }

    public void setText(String str) {
        // 设置歌词文本
        if (switchLyric) {
            lyricTextView.setText(str);
            switchLyric = false;
        } else {
            lyricTextView2.setText(str);
            switchLyric = true;
        }

        this.showNext();
    }

    public void setWidth(int i) {
        lyricTextView.setWidth(i);
        lyricTextView2.setWidth(i);
    }

    public void setTextColor(int i) {
        lyricTextView.setTextColor(i);
        lyricTextView2.setTextColor(i);
    }

    public void setSourceText(CharSequence charSequence) {
        lyricTextView.setText(charSequence);
        lyricTextView2.setText(charSequence);
    }

    public void setSpeed(float f) {
        lyricTextView.setSpeed(f);
        lyricTextView2.setSpeed(f);
    }

    public CharSequence getText() {
        if (switchLyric) {
            return lyricTextView.getText();
        } else {
            return lyricTextView2.getText();
        }
    }

    public TextPaint getPaint() {
        if (switchLyric) {
            return lyricTextView.getPaint();
        } else {
            return lyricTextView2.getPaint();
        }
    }

    public void setHeight(int i) {
        lyricTextView.setHeight(i);
        lyricTextView2.setHeight(i);
    }

    public void setTypeface(Typeface typeface) {
        lyricTextView.setTypeface(typeface);
        lyricTextView2.setTypeface(typeface);
    }

    public void setTextSize(int i, float i2) {
        lyricTextView.setTextSize(i, i2);
        lyricTextView2.setTextSize(i, i2);
    }

    public void setSingleLine(boolean bool) {
        lyricTextView.setSingleLine(bool);
        lyricTextView2.setSingleLine(bool);
    }

    public void setMaxLines(int i) {
        lyricTextView.setMaxLines(i);
        lyricTextView2.setMaxLines(i);
    }

    public void setMargins(int i, int i1, int i2, int i3) {
        FrameLayout.LayoutParams lyricParams = (FrameLayout.LayoutParams) lyricTextView.getLayoutParams();
        lyricParams.setMargins(i, i1, i2, i3);
        lyricTextView.setLayoutParams(lyricParams);

        FrameLayout.LayoutParams lyricParams2 = (FrameLayout.LayoutParams) lyricTextView.getLayoutParams();
        lyricParams2.setMargins(i, i1, i2, i3);
        lyricTextView2.setLayoutParams(lyricParams2);
    }
}
