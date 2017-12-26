package com.mcs.th.forge.photogallery;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

public class PollJobService extends JobService {
    private PollTask mCurrentTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        mCurrentTask = new PollTask();
        mCurrentTask.execute(params);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mCurrentTask != null) {
            mCurrentTask.cancel(true);
        }
        return true;
    }

    private class PollTask extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... jobParameters) {
            JobParameters jobParams = jobParameters[0];
            jobFinished(jobParams, false);
            return null;
        }
    }
}
