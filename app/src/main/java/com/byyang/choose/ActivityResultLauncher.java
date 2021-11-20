package com.byyang.choose;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;

import androidx.annotation.NonNull;

import io.reactivex.Observable;


public class ActivityResultLauncher {
    private static final String TAG = "ActivityResult";
    private final Activity activity;
    private ActivityResultFragment mActivityResultFragment;
    private ActivityResultCallback<ActivityResult> activityResultCallback;

    public ActivityResultLauncher(Activity activity) {
        this.activity = activity;
    }

    public ActivityResultLauncher(Fragment fragment) {
        this(fragment.getActivity());
    }

    private static ActivityResultFragment getActivityResultFragment(Activity activity) {
        ActivityResultFragment activityResultFragment = findActivityResultFragment(activity);
        if (activityResultFragment == null) {
            activityResultFragment = new ActivityResultFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(activityResultFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }

        return activityResultFragment;
    }

    private static ActivityResultFragment findActivityResultFragment(Activity activity) {
        return (ActivityResultFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    @Deprecated
    public Observable<ActivityResult> startForResult(Intent intent) {
        return mActivityResultFragment.startForResult(intent);
    }

    @Deprecated
    public Observable<ActivityResult> startForResult(@NonNull Class<?> clazz) {
        Intent intent = new Intent(mActivityResultFragment.getActivity(), clazz);
        return startForResult(intent);
    }

    public void launch(@NonNull Intent intent) {
        mActivityResultFragment = getActivityResultFragment(activity);
        mActivityResultFragment.startForResult(intent, activityResultCallback);
    }

    public ActivityResultLauncher registerForActivityResult(ActivityResultCallback<ActivityResult> callback) {
        activityResultCallback = callback;
        return this;
    }

}