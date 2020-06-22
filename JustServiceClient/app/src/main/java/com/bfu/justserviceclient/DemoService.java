package com.bfu.justserviceclient;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

/**
 * 继承自 LifecycleService 成为一个 LifecycleOwner，使得在 Service 中使用 LiveData 时就不需要再手动移除 Observer
 */
public class DemoService extends LifecycleService {

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return new DemoBinderImpl();
    }

    private void toast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public interface DemoBinder extends IBinder {
        void saySomething(@NonNull String message);
    }

    private final class DemoBinderImpl extends Binder implements DemoBinder {

        @Override
        public void saySomething(@NonNull String message) {
            toast(message);
        }
    }


}
