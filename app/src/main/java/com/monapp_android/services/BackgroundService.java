
package com.monapp_android.services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.monapp_android.DAOs.MessageDAO;
import com.monapp_android.DTOs.ApplicationDTO;
import com.monapp_android.DTOs.ChildDTO;
import com.monapp_android.DTOs.MessageDTO;
import com.monapp_android.R;
import com.monapp_android.activities.BlockedApp;
import com.monapp_android.activities.MainActivity;
import com.monapp_android.application.MONAPP;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import okhttp3.OkHttpClient;

import static com.monapp_android.application.MONAPP.CHANNEL_ID;

/**
 * Created by deepshikha on 24/11/16.
 */

public class BackgroundService extends Service implements LocationListener {

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    List<TimerTask> tasks;
    private HashMap<String, String> appList;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    ChildDTO childDTO;
    MessageDAO messageDAO;
    File file;


    public BackgroundService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        tasks = new ArrayList<>();
        appList = new HashMap<>();
        intent = new Intent(str_receiver);
        childDTO = MONAPP.getChild();
        messageDAO = new MessageDAO();
        file = new File(getFilesDir().getAbsolutePath() + File.pathSeparator + childDTO.getToken() + "_messages");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MONAPP Service")
                .setContentText("Running")
                .setSmallIcon(R.drawable.ic_monapp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        startTasks();

    }

    @Override
    public void onDestroy() {
        for (TimerTask task : tasks) {
            task.cancel();
        }

        mTimer.cancel();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void startTasks() {
        mTimer = new Timer();

        TimerTaskToGetLocation timerTaskToGetLocation = new TimerTaskToGetLocation();
        TimerTaskToLockApps timerTaskToLockApps = new TimerTaskToLockApps();

        tasks.add(new TimerTaskToGetLocation());
        tasks.add(new TimerTaskToLockApps());

        mTimer.schedule(timerTaskToLockApps, 0 , 1 * 1000);
        mTimer.schedule(timerTaskToGetLocation, 0 , 10 * 1000);
    }

    @SuppressLint("MissingPermission")
    private void fn_getlocation() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {
            Log.v("WARN", "Can't retrieve gps coordinates");
            return;
        }
        else {
            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.e("latitude", location.getLatitude() + "");
                        Log.e("longitude", location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                        return;
                    }
                }
            }
            if (isNetworkEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }
            }

        }

    }

    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });
        }
    }

    private void fn_update(Location location) {
        String isMocked = checkMockedLocation(location);

        Log.e("Sending", "request");

        AndroidNetworking.post(MONAPP.ADDRESS + "/rest/child_coordinates/{token}")
                .addPathParameter("token", childDTO.getToken())
                .addBodyParameter("latitude", Double.toString(location.getLatitude()))
                .addBodyParameter("longitude", Double.toString(location.getLongitude()))
                .addBodyParameter("applications", getApplicationList().toString())
                .addBodyParameter("isMocked", isMocked)
                .setTag("information")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            processMessages(response.getJSONArray("messages"));
                            processApplications(response.getJSONArray("applications"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("ERROR", error.toString());
                    }
                });
    }

    private String checkMockedLocation(Location location) {
        return location.isFromMockProvider() ? "1" : "0";
    }

    private void createNotification(MessageDTO messageDTO) {
        Notification notification = new Notification.Builder(MONAPP.getAppContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle("New message!")
                .setContentText(messageDTO.getText())
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);

    }

    private JSONArray getApplicationList() {
        JSONArray applications = new JSONArray();
        PackageManager packageManager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<PackageInfo> packs = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo packageInfo = packs.get(i);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                continue;
            }
            if (applicationInfo != null) {
                JSONObject application = new JSONObject();
                try {
                    application.put("name", packageManager.getApplicationLabel(applicationInfo).toString());
                    application.put("blocked", "N");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                applications.put(application);
            }
        }
      return applications;

    }

    private void processMessages(JSONArray messages) throws JSONException {
        for (int i = 0; i < messages.length(); i++) {
            MessageDTO messageDTO = null;
            messageDTO = new MessageDTO(messages.getJSONObject(i));
            messageDAO.storeMessage(file, messageDTO);
            createNotification(messageDTO);
        }
    }

    private void processApplications(JSONArray applications) throws JSONException{
        HashMap<String, String> processedApps = new HashMap<>();
        for (int i = 0; i< applications.length(); i++){
            ApplicationDTO app =  new ApplicationDTO(applications.getJSONObject(i));
            processedApps.put(app.getName(), app.getBlocked());
        }
        appList = processedApps;
    }

    private class TimerTaskToLockApps extends TimerTask{
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    monitorApps();
                }
            });
        }
    }

    private void monitorApps(){
        if (isAccessGranted()) {
            String currentApp = currentApp();
            String status = appList.get(currentApp);
            Log.i("INFO", "Running app: " + currentApp);
            if ( "Y".equals(status) ){
                Intent intent = new Intent(MONAPP.getAppContext(), BlockedApp.class);
                startActivity(intent);
                Log.i("INFO", "Application blocked!");
            }
        }
        else {
            Log.w("WARN", "Access was not granted!");
        }
    }

    private String currentApp() {
        String currentApp = "NULL";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            UsageStatsManager usageStatsManager = (UsageStatsManager) this.getSystemService(MONAPP.getAppContext().USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (sortedMap != null && !sortedMap.isEmpty()) {
                    try {
                        currentApp = packageManager.getApplicationLabel(packageManager.getApplicationInfo(sortedMap.get(sortedMap.lastKey()).getPackageName(), PackageManager.GET_META_DATA)).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            ActivityManager activityManager = (ActivityManager) this.getSystemService(MONAPP.getAppContext().ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = activityManager.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        return currentApp;
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(MONAPP.getAppContext().APP_OPS_SERVICE);
            int mode = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}


                                        
