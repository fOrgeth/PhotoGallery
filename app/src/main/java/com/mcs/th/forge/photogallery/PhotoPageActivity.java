package com.mcs.th.forge.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

public class PhotoPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (fragment instanceof PhotoPageFragment) {
            if (((PhotoPageFragment) fragment).webViewCanGoBack()) {
                ((PhotoPageFragment) fragment).webViewGoBack();
            } else {
                super.onBackPressed();
            }
        }
    }
}
