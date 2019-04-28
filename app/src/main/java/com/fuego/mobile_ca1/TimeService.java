package com.fuego.mobile_ca1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.firebase.Timestamp;

import java.util.Date;

public class TimeService extends Service {

    private final IBinder myBinder = new MyLocalBinder();

    public class MyLocalBinder extends Binder {
        public TimeService getService() {
            return TimeService.this;
        }
    }

    public TimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public Timestamp getCurrentTime() {
        return new Timestamp(new Date());
    }

}
