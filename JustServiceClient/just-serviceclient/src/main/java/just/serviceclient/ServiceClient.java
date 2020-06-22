package just.serviceclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * 封装绑定或开启 service 的操作
 *
 * @param <Interface>
 */
public class ServiceClient<Interface> {

    @NonNull
    private final Context context;

    @NonNull
    private final Class<? extends Service> serviceClass;

    public ServiceClient(@NonNull Context context, @NonNull Class<? extends Service> serviceClass) {
        this.context = context;
        this.serviceClass = serviceClass;
    }

    @Nullable
    private Interface binder;

    @NonNull
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = asInterface(service);

            /* 回调 connected. */
            ServiceClient.this.onServiceConnected(binder);

            /* 执行尚未执行的任务. */
            for (PendingRunnable<Interface> pendingRunnable : pendingRunnables) {
                pendingRunnable.run(binder);

                /* 任务执行后将其从 pending 集合移除. */
                pendingRunnables.remove(pendingRunnable);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ServiceClient.this.onServiceDisconnected(name);
            binder = null;
        }
    };

    @Nullable
    public final Interface getBinder() {
        return binder;
    }

    public final boolean isBind() {
        return null != this.binder;
    }

    public final void bind() {
        bind(Context.BIND_AUTO_CREATE);
    }

    public final void bind(int flags) {
        if (!isBind()) {
            final Intent intent = new Intent(context, serviceClass);
            context.bindService(intent, serviceConnection, flags);
            postBindService();
        }
    }

    public final void unBind() {
        if (isBind()) {
            preUnBindService(binder);
            context.unbindService(serviceConnection);
            binder = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    @NonNull
    private final List<PendingRunnable<Interface>> pendingRunnables = new LinkedList<>();

    public final void postAction(@NonNull PendingRunnable<Interface> action) {
        if (isBind()) {
            action.run(binder);
        } else {
            /* 尚未绑定，将其加入队列，待绑定后执行. */
            pendingRunnables.add(action);
        }
    }

    @FunctionalInterface
    public interface PendingRunnable<Interface> {
        void run(@NonNull Interface binder);
    }

    ///////////////////////////////////////////////////////////////////////////
    // start or stop Service
    ///////////////////////////////////////////////////////////////////////////

    public final void startService() {
        startService(null);
    }

    public final void startService(@NonNull String key, @NonNull Parcelable value) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(key, value);
        startService(bundle);
    }

    public final void startService(@NonNull String key, @NonNull String value) {
        final Bundle bundle = new Bundle();
        bundle.putString(key, value);
        startService(bundle);
    }

    public final void startService(@Nullable Bundle bundle) {
        final Intent intent = new Intent(context, serviceClass);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        context.startService(intent);
    }

    public final void stopService() {
        final Intent intent = new Intent(context, serviceClass);
        context.stopService(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // abstract method
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 转换为目标类型
     * <p>
     * general: return (Interface) service
     * <p>
     * AIDL: return IMyAidlInterface.Stub.asInterface(service);
     */
    @NonNull
    protected Interface asInterface(@NonNull IBinder service) {
        //noinspection unchecked
        return (Interface) service;
    }

    /**
     * service 连接成功后回调
     */
    protected void onServiceConnected(@NonNull Interface binder) {

    }

    /**
     * service 断开连接时回调
     */
    protected void onServiceDisconnected(@NonNull ComponentName name) {

    }

    /**
     * bindService 执行之后调用
     */
    protected void postBindService() {

    }

    /**
     * unbindService 执行之前调用
     */
    protected void preUnBindService(@NonNull Interface binder) {

    }


}
