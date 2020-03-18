package com.slastanna.questory.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.slastanna.questory.MainActivity;

import androidx.annotation.Nullable;

public class userService extends Service {

    public userService(){

    }
    public void onCreate() {
        super.onCreate();

        Log.d("MyTag", "Service is working2");

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
