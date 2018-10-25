package com.tahsin.pushnotificationnew.notifications;

import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tahsin.pushnotificationnew.utils.NotificationUtil;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Message->", "Gotcha!");

        NotificationUtil util = new NotificationUtil();
        util.buildNotification(getApplicationContext(), "Push", "Push notification is working!");

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("Token->", s);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("TOKEN", s).apply();
    }


}
