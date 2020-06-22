package just.serviceclient;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

import java.util.List;


public final class ServiceUtils {

    /**
     * 工具类不可被实例化
     */
    private ServiceUtils() {
        throw new RuntimeException("ServiceUtils cannot be instantiated");
    }


    private static final String TAG = "ServiceUtils";

    public static boolean isInServiceProcess(Context context, Class<? extends Service> serviceClass) {
        final PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_SERVICES);
        } catch (Exception e) {
            // "Could not get package info for " + context.getPackageName()
            return false;
        }
        final String mainProcess = packageInfo.applicationInfo.processName;
        final ComponentName component = new ComponentName(context, serviceClass);
        ServiceInfo serviceInfo;
        try {
            serviceInfo = packageManager.getServiceInfo(component, PackageManager.GET_DISABLED_COMPONENTS);
        } catch (PackageManager.NameNotFoundException ignored) {
            // Service is disabled.
            return false;
        }

        if (serviceInfo.processName.equals(mainProcess)) {
            // "Did not expect service " + serviceClass + " to run in main process "+mainProcess
            // Technically we are in the service process, but we're not in the service dedicated process.
            return false;
        }

        final int myPid = android.os.Process.myPid();
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningAppProcessInfo myProcess = null;
        List<ActivityManager.RunningAppProcessInfo> runningProcesses;
        try {
            runningProcesses = activityManager.getRunningAppProcesses();
        } catch (SecurityException exception) {
            // https://github.com/square/leakcanary/issues/948
            // Could not get running app processes
            return false;
        }
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
                if (process.pid == myPid) {
                    myProcess = process;
                    break;
                }
            }
        }
        if (myProcess == null) {
            // Could not find running process for + myPid
            return false;
        }
        return myProcess.processName.equals(serviceInfo.processName);
    }

}
