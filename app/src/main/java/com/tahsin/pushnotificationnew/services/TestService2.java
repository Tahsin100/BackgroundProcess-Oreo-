package com.tahsin.pushnotificationnew.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.tahsin.pushnotificationnew.utils.NotificationUtil;

/**
 * Created by Tahsin Rahman
 * on 24,October,2018
 */

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class TestService2 extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("JobStatus2->", "Started");
        NotificationUtil util = new NotificationUtil();
        util.buildNotification(getApplicationContext(), "Job Scheduler", "Job scheduler is working from background!");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
