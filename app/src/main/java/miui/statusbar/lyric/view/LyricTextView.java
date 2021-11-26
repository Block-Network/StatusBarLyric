package miui.statusbar.lyric.view;

/*
 * Copyright (C) 2019 The Android Open Source Project
 *               2020 The exTHmUI Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class LyricTextView extends TextView {

    private boolean isStop = true;
    private float textLength = 0f;
    private float viewWidth = 0f;
    private float speed = 4f;
    private float x = 0f;
    private String text;

    public static final int startScrollDelay = 500;
    public static final int invalidateDelay = 10;

    private final Paint mPaint;

    Runnable mStartScrollRunnable = this::startScroll;

    public LyricTextView(Context context) {
        this(context, null);
    }

    public LyricTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0x01010084);
    }

    public LyricTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public LyricTextView(Context context, AttributeSet attrs, int defStyleAttr,
                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint = getPaint();
    }

    private void init() {
        x = 0;
        textLength = getTextLength();
        viewWidth = getWidth();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mStartScrollRunnable);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        stopScroll();
        this.text = text.toString();
        init();
        postInvalidate();
        postDelayed(mStartScrollRunnable, startScrollDelay);
    }

    @Override
    public void setTextColor(int color) {
        if (mPaint != null) mPaint.setColor(color);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null && text != null) {
            float y = getHeight() / 2 + Math.abs(mPaint.ascent() + mPaint.descent()) / 2;
            canvas.drawText(text, x, y, mPaint);
        }
        if (!isStop) {
            if (viewWidth - x + speed >= textLength) {
                x = viewWidth > textLength ? 0 : viewWidth - textLength;
                stopScroll();
            } else {
                x -= speed;
            }
            invalidateAfter(invalidateDelay);
        }
    }

    private void invalidateAfter(long delay) {
        removeCallbacks(invalidateRunnable);
        postDelayed(invalidateRunnable, delay);
    }

    private final Runnable invalidateRunnable = this::invalidate;

    public void startScroll() {
        init();
        isStop = false;
        postInvalidate();
    }

    public void stopScroll() {
        isStop = true;
        removeCallbacks(mStartScrollRunnable);
        postInvalidate();
    }

    private float getTextLength() {
        return mPaint == null ? 0 : mPaint.measureText(text);
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
