package com.mcs.th.forge.photogallery.service;

import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class PollServiceSelector {

    private static final String TAG = "PollServiceSelector";
    private static final int JOB_ID = 1;

    public static void startService(Context context, boolean isOn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "LOLLIPOP");
            JobScheduler scheduler = (JobScheduler) context
                    .getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (isOn) {
                ComponentName serviceName = new ComponentName(context, PollJobService.class);
                JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        .setPeriodic(1000 * 60)
                        .setPersisted(true)
                        .build();
                int result = scheduler.schedule(jobInfo);
                if (result == JobScheduler.RESULT_SUCCESS) {
                    Log.d(TAG, "SERVICE SCHEDULED");
                }
            } else {
                if (scheduler != null) {
                    scheduler.cancel(JOB_ID);
                    Log.d(TAG, "JobService Canceled");
                }
            }
        } else {
            PollService.setServiceAlarm(context, isOn);
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        boolean isServiceOn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (scheduler != null) {
                for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
                    if (jobInfo.getId() == JOB_ID) {
                        isServiceOn = true;
                    }
                }
            }
        } else {
            Intent i = PollService.newIntent(context);
            PendingIntent pi = PendingIntent
                    .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
            isServiceOn = pi != null;
        }
        return isServiceOn;
    }

}
