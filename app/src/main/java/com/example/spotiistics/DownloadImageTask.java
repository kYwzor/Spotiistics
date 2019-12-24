package com.example.spotiistics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;

// based on https://stackoverflow.com/questions/14332296/how-to-set-image-from-url-using-asynctask/15797963
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private WeakReference<ImageView> imageReference;
    private int size;

    DownloadImageTask(ImageView bmImage, int size) {
        this.imageReference = new WeakReference<>(bmImage);
        this.size = size;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        Bitmap resized = Bitmap.createScaledBitmap(result, size, size, true);
        ImageView bmImage = imageReference.get();
        if (bmImage != null) bmImage.setImageBitmap(resized);
    }
}