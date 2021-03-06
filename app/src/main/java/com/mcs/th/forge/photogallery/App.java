package com.mcs.th.forge.photogallery;

import android.app.Application;
import android.os.StrictMode;

public class App extends Application {
    private static final boolean DEVELOPER_MODE = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }
}
