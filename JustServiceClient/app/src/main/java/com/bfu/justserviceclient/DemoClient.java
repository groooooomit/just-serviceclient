package com.bfu.justserviceclient;

import android.content.Context;

import androidx.annotation.NonNull;

import just.serviceclient.ServiceClient;


/**
 * ServiceClient 使用示例
 */
public class DemoClient extends ServiceClient<DemoService.DemoBinder> {

    public DemoClient(@NonNull Context context) {
        super(context, DemoService.class);
    }

}
