package com.byyang.choose;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ActivityResultFragment extends Fragment {
    private final Map<Integer, PublishSubject<ActivityResult>> mSubjects = new HashMap<>();
    private final Map<Integer, ActivityResultCallback<ActivityResult>> mCallbacks = new HashMap<>();

    public ActivityResultFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Observable<ActivityResult> startForResult(final Intent intent) {
        final PublishSubject<ActivityResult> subject = PublishSubject.create();
        return subject.doOnSubscribe(disposable -> {
            int requestCode = generateRequestCode();
            mSubjects.put(requestCode, subject);
            startActivityForResult(intent, requestCode);
        });
    }

    public void startForResult(Intent intent, ActivityResultCallback<ActivityResult> callback) {
        int requestCode = generateRequestCode();
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //rxjava方式的处理
        PublishSubject<ActivityResult> subject = mSubjects.remove(requestCode);
        if (subject != null) {
            subject.onNext(new ActivityResult(resultCode, data));
            subject.onComplete();
        }

        //callback方式的处理
        ActivityResultCallback<ActivityResult> callback = mCallbacks.remove(requestCode);
        if (callback != null) {
            ActivityResult activityResult = new ActivityResult(resultCode, data);
            callback.onActivityResult(activityResult);
        }
    }

    private int generateRequestCode() {
        Random random = new Random();
        for (; ; ) {
            int code = random.nextInt(6536);
            if (!mSubjects.containsKey(code) && !mCallbacks.containsKey(code)) {
                return code;
            }
        }
    }

}