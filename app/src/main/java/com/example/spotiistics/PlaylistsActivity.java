package com.example.spotiistics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

public class PlaylistsActivity extends AppCompatActivity {
    UserPrivate user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_playlist);
        Intent intent = getIntent();
        final String mAccessToken = intent.getStringExtra("token");
        user = intent.getParcelableExtra("user");

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        SpotifyService spotify = api.getService();



        Map<String, Object> options  = new HashMap<>();
        options.put("limit", 50);
        spotify.getMyPlaylists(options, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(PlaylistsActivity.this,
                        "Error loading content", Toast.LENGTH_LONG).show();
            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                addImagesToThegallery(playlistSimplePager);
            }
        });

        View searchButton = findViewById(R.id.include).findViewById(R.id.imageView5);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent mIntent = new Intent(PlaylistsActivity.this, SearchActivity.class);
                mIntent.putExtra("token", mAccessToken);
                startActivity(mIntent);
            }
        });


    }



    private void addImagesToThegallery(Pager<PlaylistSimple> playlistSimplePager) {
        LinearLayout yourPlaylist = findViewById(R.id.yourPlaylist);

        LinearLayout followedPlaylist = findViewById(R.id.followedPlaylist);

        int counter = 0;
        AlbumClickListener albumClickListener = new AlbumClickListener();
        for (PlaylistSimple playlist:playlistSimplePager.items) {
            if(playlist.owner.id.equals(user.id)) {
                LinearLayout LL = setLinearLayout(counter);
                LL.setOnClickListener(albumClickListener);
                yourPlaylist.addView(LL);
                LL.addView(getPlaceHolder());
                new DownloadImageTask((ImageView) findViewById(R.id.image), LL).execute(playlist.images.get(0).url);
                LL.addView(getTextView(playlist.name));
            }
            else{
                LinearLayout LL = setLinearLayout(counter);
                LL.setOnClickListener(albumClickListener);
                followedPlaylist.addView(LL);
                LL.addView(getPlaceHolder());
                new DownloadImageTask((ImageView) findViewById(R.id.image), LL).execute(playlist.images.get(0).url);
                LL.addView(getTextView(playlist.name));
            }
            counter ++;
        }
    }

    private View getPlaceHolder() {
        Drawable dr = getResources().getDrawable(R.drawable.noalbum);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,250, 250, true);
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageBitmap(resized);
        return  imageView;
    }

    private LinearLayout setLinearLayout(int counter){
        LinearLayout LL = new LinearLayout(getApplicationContext());
        LL.setTag(counter);
        LL.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 50, 0);
        LL.setLayoutParams(lp);
        return LL;
    }

    private void setImageView(Bitmap result, LinearLayout LL) {
        ImageView imageView = (ImageView) LL.getChildAt(0);
        Bitmap resized = Bitmap.createScaledBitmap(result,250, 250, true);
        imageView.setImageBitmap(resized);

    }

    private View getTextView(String name) {
        TextView tv = new TextView(getApplicationContext());
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxLines(1);
        tv.setMaxWidth(250);
        tv.setText(name);
        return tv;
    }

    //https://stackoverflow.com/questions/14332296/how-to-set-image-from-url-using-asynctask/15797963
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

    public class AlbumClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int position = 0;
            if (v.getTag() instanceof Integer) {
                position = (Integer) v.getTag();
            }
            Toast.makeText(PlaylistsActivity.this,
                    String.valueOf(position), Toast.LENGTH_LONG).show();
        }
    }

}
