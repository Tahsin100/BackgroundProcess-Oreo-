package com.tahsin.pushnotificationnew;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.tahsin.pushnotificationnew.receivers.ConnectivityReceiver;
import com.tahsin.pushnotificationnew.services.ConnectivityService;
import com.tahsin.pushnotificationnew.services.TestService;
import com.tahsin.pushnotificationnew.services.TestService2;
import com.tahsin.pushnotificationnew.workers.TestWorker;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    private static final int TEST_JS = 100;
    private static final int NETWORK_CHECK_JS = 200;

    private TextView textView;
    private Button buttonWork;
    private Button buttonPeriodicWork;
    private Button buttonJobDispatcher;
    private Button buttonJobScheduler;

    private WorkManager mWorkManager;
    private FirebaseJobDispatcher dispatcher;
    private JobScheduler jobScheduler;

    private ConnectivityReceiver connectivityReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWorkManager = WorkManager.getInstance();
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }

        connectivityReceiver = new ConnectivityReceiver();

        textView = findViewById(R.id.textView);
        //textView.setText(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("TOKEN", ""));

        buttonWork = findViewById(R.id.buttonWork);
        buttonPeriodicWork = findViewById(R.id.buttonWorkPeriodic);
        buttonJobDispatcher = findViewById(R.id.buttonJobDisPatcher);
        buttonJobScheduler = findViewById(R.id.buttonJobScheduler);

        setClickListeners();

    }

    private void setClickListeners() {

        buttonWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // One Time WorkManager
                startOneTimeWorkManager();

            }
        });

        buttonPeriodicWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Periodic WorkManager
                resetAll();
                startPeriodicWork();
                Toast.makeText(MainActivity.this, "Periodic work started!", Toast.LENGTH_SHORT).show();

            }
        });

        buttonJobDispatcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetAll();
                scheduleJobDispatcher();
                Toast.makeText(MainActivity.this, "Job dispatcher started!", Toast.LENGTH_SHORT).show();

            }
        });

        buttonJobScheduler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    resetAll();
                    startJobService();
                    Toast.makeText(MainActivity.this, "Job scheduler started!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Job scheduler is not supported for API < 21", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void resetAll() {

        mWorkManager.cancelUniqueWork("PeriodicWork");
        dispatcher.cancel("UniqueTagForYourJob");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler.cancel(100);
        }


    }

    private void startJobService() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           jobScheduler =
                    (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(new JobInfo.Builder(TEST_JS,
                    new ComponentName(this, TestService2.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPeriodic(60*2*1000)
                    .setPersisted(true)
                    .build());
        }
    }

    private void startOneTimeWorkManager() {

        OneTimeWorkRequest someWork = new OneTimeWorkRequest.Builder(TestWorker.class)
                .setConstraints(constraints())
                .build();
        OneTimeWorkRequest oneTimeWorkRequest = someWork;
        mWorkManager.enqueue(oneTimeWorkRequest);

    }

    private void startPeriodicWork() {

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                .Builder(TestWorker.class, 20, TimeUnit.MINUTES, 5, TimeUnit.MINUTES)
                .addTag("PeriodicWork")
                .build();
        mWorkManager.getInstance().enqueue(periodicWorkRequest);

    }

    private Constraints constraints() {

        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();
        return constraints;

    }

    private void scheduleJobDispatcher() {

        Log.d("ScheduleStatus->", "Started");
        //creating new job and adding it with dispatcher
        Job job = createJob(dispatcher);

        dispatcher.mustSchedule(job);

        Log.d("DispatchStatus->", "Done");

    }

    private Job createJob(FirebaseJobDispatcher dispatcher) {

        Job job = dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                //call this service when the criteria are met.
                .setService(TestService.class)
                //unique id of the task
                .setTag("UniqueTagForYourJob")
                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 10 - 20 seconds from now.
                .setTrigger(Trigger.executionWindow((int) TimeUnit.MINUTES.toSeconds(15), (int) TimeUnit.MINUTES.toSeconds(20)))
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        return job;

    }

    public static Job updateJob(FirebaseJobDispatcher dispatcher) {

        Job newJob = dispatcher.newJobBuilder()
                //update if any task with the given tag exists.
                .setReplaceCurrent(true)
                //Integrate the job you want to start.
                .setService(TestService.class)
                .setTag("UniqueTagForYourJob")
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(15*60, 20*60))
                .build();
        return newJob;

    }

    public void cancelJob(Context context) {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //Cancel all the jobs for this package
        dispatcher.cancelAll();
        // Cancel the job for this tag
        dispatcher.cancel("UniqueTagForYourJob");

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            scheduleConnectivityCheck();
        }
        else {
            registerReceiver(connectivityReceiver, new IntentFilter());
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleConnectivityCheck() {

        JobInfo myJob = new JobInfo.Builder(NETWORK_CHECK_JS, new ComponentName(this, ConnectivityService.class))
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(connectivityReceiver);
    }
}
