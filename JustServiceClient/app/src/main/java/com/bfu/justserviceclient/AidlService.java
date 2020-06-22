package com.bfu.justserviceclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class AidlService extends Service {
    
    private static final String TAG = "AidlService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AidlBinderImpl();
    }

    private void print(@NonNull String message) {
        Log.d(TAG, "Thread: " + Thread.currentThread() + "  print: " + message);
    }

    private class AidlBinderImpl extends IMyAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void saySomething(String message) throws RemoteException {
            print(message);
        }
    }


}
