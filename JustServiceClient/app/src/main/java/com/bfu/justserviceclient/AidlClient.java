package com.bfu.justserviceclient;

import android.content.Context;
import android.os.IBinder;

import androidx.annotation.NonNull;

import just.serviceclient.ServiceClient;


public class AidlClient extends ServiceClient<IMyAidlInterface> {

    public AidlClient(@NonNull Context context) {
        super(context, AidlService.class);
    }

    @NonNull
    @Override
    protected IMyAidlInterface asInterface(@NonNull IBinder service) {
        return IMyAidlInterface.Stub.asInterface(service);
    }

}
