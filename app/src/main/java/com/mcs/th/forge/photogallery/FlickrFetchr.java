package com.mcs.th.forge.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    private static final String TAG = "FlickFetchr";
    private static final String API_KEY = "e8a8277280bea546406c2151a8d8c4c9";
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private String buildUrl(String method, String query, Integer page) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);
        switch (method) {
            case SEARCH_METHOD:
                uriBuilder.appendQueryParameter("text", query);
                break;
            case FETCH_RECENTS_METHOD:
                uriBuilder.appendQueryParameter("page", String.valueOf(page));
                break;
        }
        /*if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text", query);
        } else if (method.equals(FETCH_RECENTS_METHOD)) {
            uriBuilder.appendQueryParameter("page", String.valueOf(page));
        }*/
        return uriBuilder.build().toString();
    }

    public List<GalleryItem> fetchRecentPhotos(int page) {
        String url = buildUrl(FETCH_RECENTS_METHOD, null, page);
        Log.d(TAG, "URL: "+url);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query) {
        String url = buildUrl(SEARCH_METHOD, query, null);
        return downloadGalleryItems(url);
    }

    private List<GalleryItem> downloadGalleryItems(String url) {

        List<GalleryItem> items = new ArrayList<>();
//        Log.d(TAG, "Loading page " + page);
        try {
            /*String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", FETCH_RECENTS_METHOD)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("page", String.valueOf(page))
                    .appendQueryParameter("extras", "url_s")
                    .build()
                    .toString();*/

            String jsonString = getUrlString(url);
//            Log.d(TAG, "Received JSON: " + jsonString);
            items = parseItems(jsonString);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to tech items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return items;
    }

    private List<GalleryItem> parseItems(String jsonString)
            throws IOException, JSONException {
        Type galleryListType = new TypeToken<List<GalleryItem>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        JSONObject jsonBody = new JSONObject(jsonString);
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject obj = photoJsonArray.getJSONObject(i);
            if (!obj.has("url_s")) {
                Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ALARM!!!!!");
            }
        }
        return gson.fromJson(photoJsonArray.toString(), galleryListType);
    }
}
