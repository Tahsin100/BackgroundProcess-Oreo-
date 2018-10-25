package com.tahsin.pushnotificationnew.workers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tahsin.pushnotificationnew.utils.NotificationUtil;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Created by Tahsin Rahman
 * on 23,October,2018
 */
public class TestWorker extends Worker {


    public Context context;
    public TestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("Worker->", "Working!");
        NotificationUtil util = new NotificationUtil();
        util.buildNotification(context, "Work", "Work is triggered!");
        return Result.SUCCESS;
    }
}
