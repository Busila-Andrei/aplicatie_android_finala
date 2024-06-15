package com.example.aplicatie_android_finala.config;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

public class TimerService extends Service {
    private static final String TAG = "TimerService";
    private CountDownTimer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        startTimer();
        return START_STICKY;
    }

    private void startTimer() {
        Log.d(TAG, "Starting timer");
        timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "Timer onTick: " + millisUntilFinished / 1000 + " seconds remaining");
            }

            public void onFinish() {
                Log.d(TAG, "Timer finished");
                // Add any additional action you want to perform when the timer finishes
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        if (timer != null) {
            timer.cancel();
            Log.d(TAG, "Timer cancelled");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
