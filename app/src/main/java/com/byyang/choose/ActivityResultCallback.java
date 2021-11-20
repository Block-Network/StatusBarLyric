package com.byyang.choose;

import android.annotation.SuppressLint;

public interface ActivityResultCallback<O> {

    /**
     * Called when result is available
     */
    void onActivityResult(@SuppressLint("UnknownNullness") O result);
}
