package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;


public class SearchActivity extends AppCompatActivity {
    String mAccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        Intent intent = getIntent();
        mAccessToken = intent.getStringExtra("token");

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        final SpotifyService spotify = api.getService();

        View searchButton = findViewById(R.id.search_mag);
        searchButton.setOnClickListener(new View.OnClickListener() {
            EditText et = findViewById(R.id.search_box);
            @Override
            public void onClick(View arg0) {
                spotify.searchTracks(et.getText().toString(), new SpotifyCallback<TracksPager>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                    }

                    @Override
                    public void success(TracksPager tracks, Response response) {
                        Toast.makeText(SearchActivity.this,
                                String.valueOf(response.getStatus()), Toast.LENGTH_LONG).show();
                        displaySearch(tracks);
                    }
                });
            }
        });

    }

    private void displaySearch(TracksPager tracks) {
        Pager<Track> tracksPager = tracks.tracks;
        LinearLayout base = findViewById(R.id.results);
        int counter=0;
        trackClickListener trackClickListener = new trackClickListener();

        for (Track t : tracksPager.items) {
            LinearLayout LL = setLinearLayout(t.id);
            LL.setOnClickListener(trackClickListener);
            base.addView(LL);
            new SearchActivity.DownloadImageTask((ImageView) findViewById(R.id.image), LL).execute(t.album.images.get(0).url);
            LL.addView(getTextView(t.name));

            counter++;
        }
    }

    private LinearLayout setLinearLayout(String counter){
        LinearLayout LL = new LinearLayout(getApplicationContext());
        LL.setTag(counter);
        LL.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 50);
        LL.setLayoutParams(lp);
        return LL;
    }

    private void setImageView(Bitmap result, LinearLayout LL) {
        Bitmap resized = Bitmap.createScaledBitmap(result,250, 250, true);
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageBitmap(resized);
        LL.addView(imageView,0);

    }

    private View getTextView(String name) {
        TextView tv = new TextView(getApplicationContext());
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxLines(1);
        tv.setMaxWidth(250);
        tv.setText(name);
        return tv;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        LinearLayout LL;
        public DownloadImageTask(ImageView bmImage, LinearLayout LL) {
            this.bmImage = bmImage;
            this.LL = LL;
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
            setImageView(result, this.LL);
        }
    }

    public class trackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {


            Intent mIntent = new Intent(SearchActivity.this, TrackActivity.class);
            mIntent.putExtra("token", mAccessToken);
            mIntent.putExtra("track_id", (String) v.getTag());
            startActivity(mIntent);
        }
    }


}
