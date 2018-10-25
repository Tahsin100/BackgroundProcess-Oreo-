package com.tahsin.pushnotificationnew.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Tahsin Rahman
 * on 24,October,2018
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    private ConnectivityStatusListener connectivityStatusListener;

    public ConnectivityReceiver(){ }

    public ConnectivityReceiver(ConnectivityStatusListener connectivityStatusListener) {
        this.connectivityStatusListener = connectivityStatusListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (isConnected(context)) {
            try {
                Toast.makeText(context, "Network is connected", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Network is changed or reconnected", Toast.LENGTH_LONG).show();
        }

        // For API > 24
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityStatusListener.onNetworkConnectionChanged(isConnected(context));
        }

    }

    public boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
    }


    public interface ConnectivityStatusListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}

