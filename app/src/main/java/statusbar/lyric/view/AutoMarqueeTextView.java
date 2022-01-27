package statusbar.lyric.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author zhanghuagang
 * @url http://blog.csdn.net/hua631150873/article/details/73469604
 */
@SuppressLint("AppCompatCustomView")
public class AutoMarqueeTextView extends TextView {

    public AutoMarqueeTextView(Context context) {
        super(context);
        setFocusable(true);// 在每个构造方法中，将TextView设置为可获取焦点
    }

    public AutoMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
    }

    public AutoMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
    }

    @Override
    public boolean isFocused() {// 这个方法必须返回true，制造假象，当系统调用该方法的时候，会一直以为TextView已经获取了焦点
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {// 这个方法必须删除其方法体内的实现，也就是让他空实现，也就是说，TextView的焦点获取状态永远都不会改变
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

}