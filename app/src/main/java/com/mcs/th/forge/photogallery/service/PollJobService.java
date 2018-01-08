package com.mcs.th.forge.photogallery.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.mcs.th.forge.photogallery.FlickrFetchr;
import com.mcs.th.forge.photogallery.GalleryItem;
import com.mcs.th.forge.photogallery.PhotoGalleryActivity;
import com.mcs.th.forge.photogallery.QueryPreferences;
import com.mcs.th.forge.photogallery.R;

import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {
    private static final String TAG = "PollJobService";
    private static final String CHANNEL_ID = "myChannel";
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

    private class PollTask extends AsyncTask<JobParameters, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(JobParameters... jobParameters) {
            JobParameters jobParams = jobParameters[0];
            String query = QueryPreferences.getStoredQuery(PollJobService.this);
            List<GalleryItem> items;

            if (query == null) {
                items = new FlickrFetchr().fetchRecentPhotos(1);
            } else {
                items = new FlickrFetchr().searchPhotos(query);
            }
            jobFinished(jobParams, false);
            return items;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            if (items.size() == 0) {
                return;
            }
            String lastResultId = QueryPreferences.getLastResultId(PollJobService.this);
            String resultId = items.get(0).getId();
            if (resultId.equals(lastResultId)) {
                Log.i(TAG, "Got an old result: " + resultId);
            } else {
                Log.i(TAG, "Got a new result: " + resultId);

                Resources resources = getResources();
                Intent i = PhotoGalleryActivity.newIntent(PollJobService.this);
                PendingIntent pi = PendingIntent.getActivity(PollJobService.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(PollJobService.this, CHANNEL_ID)
                        .setTicker(resources.getString(R.string.new_pictures_title))
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(resources.getString(R.string.new_pictures_title))
                        .setContentText(resources.getString(R.string.new_pictures_text))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(PollJobService.this);
                notificationManager.notify(0, notification);
            }

            QueryPreferences.setLastResultId(PollJobService.this, resultId);
        }
    }
}
