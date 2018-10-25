package com.tahsin.pushnotificationnew.services;


import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.tahsin.pushnotificationnew.utils.NotificationUtil;

/**
 * Created by Tahsin Rahman
 * on 22,October,2018
 */
public class TestService extends JobService {

    private String ADMIN_CHANNEL_ID = "test_id";

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d("JobStatus->", "Started");
        NotificationUtil util = new NotificationUtil();
        util.buildNotification(getApplicationContext(), "Job Dispatcher", "Job dispatcher is started from background!");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

}
