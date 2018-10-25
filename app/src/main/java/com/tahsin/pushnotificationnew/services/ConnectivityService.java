package com.tahsin.pushnotificationnew.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.tahsin.pushnotificationnew.receivers.ConnectivityReceiver;

/**
 * Created by Tahsin Rahman
 * on 24,October,2018
 */

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class ConnectivityService extends JobService implements ConnectivityReceiver.ConnectivityStatusListener {

    ConnectivityReceiver connectivityReceiver;
    @Override
    public boolean onStartJob(JobParameters params) {
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        unregisterReceiver(connectivityReceiver);
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        connectivityReceiver = new ConnectivityReceiver(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        String message = isConnected ? "Connected to Internet!" : "Disconnected!";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
