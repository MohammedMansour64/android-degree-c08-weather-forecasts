package com.barmej.weatherforecasts.data.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class SyncUtils {

    private static final int SYNC_SERVICE_JOB_ID = 0;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(3);

    public static void startSync(Context context) {
        Intent intent = new Intent(context , SyncIntentService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }

    }

    public static void scheduleSync(Context context){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(SyncWorker.class , 3 , TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork("SyncWorker" , ExistingPeriodicWorkPolicy.KEEP , periodicWorkRequest);
    }

    // this one is by using Service
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleSync2(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName jobServiceName = new ComponentName(context, SyncJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(SYNC_SERVICE_JOB_ID , jobServiceName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(SYNC_INTERVAL_SECONDS)
                .build();

        jobScheduler.schedule(jobInfo);
    }
}
