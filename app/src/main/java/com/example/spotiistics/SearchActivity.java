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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistsPager;
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
                        Toast.makeText(SearchActivity.this,
                                "Error loading content", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void success(TracksPager tracks, Response response) {
                        displayTracksSearch(tracks);
                    }
                });

                spotify.searchAlbums(et.getText().toString(), new SpotifyCallback<AlbumsPager>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Toast.makeText(SearchActivity.this,
                                "Error loading content", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void success(AlbumsPager albums, Response response) {
                        displayAlbumsSearch(albums);
                    }
                });

                spotify.searchArtists(et.getText().toString(), new SpotifyCallback<ArtistsPager>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Toast.makeText(SearchActivity.this,
                                "Error loading content", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void success(ArtistsPager artists, Response response) {
                        displayArtistsSearch(artists);
                    }
                });

                spotify.searchPlaylists(et.getText().toString(), new SpotifyCallback<PlaylistsPager>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Toast.makeText(SearchActivity.this,
                                "Error loading content", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void success(PlaylistsPager playlists, Response response) {
                        displayPlaylistsSearch(playlists);
                    }
                });
            }

        });

        View playlistButton = findViewById(R.id.include).findViewById(R.id.imageView4);
        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent mIntent = new Intent(SearchActivity.this, PlaylistsActivity.class);
                mIntent.putExtra("token", mAccessToken);
                startActivity(mIntent);
            }
        });

    }

    private void displayTracksSearch(TracksPager tracks) {
        Pager<Track> tracksPager = tracks.tracks;
        LinearLayout base = findViewById(R.id.results);
        base.removeAllViews();
        trackClickListener trackClickListener = new trackClickListener();

        for (Track t : tracksPager.items) {
            LinearLayout LL = setLinearLayout(1,t.id);
            LL.setOnClickListener(trackClickListener);
            base.addView(LL);
            new SearchActivity.DownloadImageTask((ImageView) findViewById(R.id.image), LL).execute(t.album.images.get(0).url);

            LL.addView(getTextView(t.name));
        }
    }

    private void displayAlbumsSearch(AlbumsPager albums) {
        Pager<AlbumSimple> albumPager = albums.albums;
        LinearLayout base = findViewById(R.id.results);

        trackClickListener trackClickListener = new trackClickListener();

        for (AlbumSimple a : albumPager.items) {
            LinearLayout LL = setLinearLayout(2,a.id);
            LL.setOnClickListener(trackClickListener);
            base.addView(LL);
            new SearchActivity.DownloadImageTask((ImageView) findViewById(R.id.image), LL).execute(a.images.get(0).url);

            LL.addView(getTextView(a.name));
        }
    }

    private void displayArtistsSearch(ArtistsPager artists) {
        Pager<Artist> artistPager = artists.artists;
        LinearLayout base = findViewById(R.id.results);
        trackClickListener trackClickListener = new trackClickListener();

        for (Artist a : artistPager.items) {
            LinearLayout LL = setLinearLayout(3,a.id);
            LL.setOnClickListener(trackClickListener);
            base.addView(LL);
            if(a.images.size() !=0) new SearchActivity.DownloadImageTask((ImageView) findViewById(R.id.image), LL).execute(a.images.get(0).url);

            LL.addView(getTextView(a.name));
        }
    }

    private void displayPlaylistsSearch(PlaylistsPager playlists) {
        Pager<PlaylistSimple> playlistPager = playlists.playlists;
        LinearLayout base = findViewById(R.id.results);
        trackClickListener trackClickListener = new trackClickListener();

        for (PlaylistSimple p : playlistPager.items) {
            LinearLayout LL = setLinearLayout(4,p.id);
            LL.setOnClickListener(trackClickListener);
            base.addView(LL);
            new SearchActivity.DownloadImageTask((ImageView) findViewById(R.id.image), LL).execute(p.images.get(0).url);

            LL.addView(getTextView(p.name));
        }
    }

    private LinearLayout setLinearLayout(int counter, String id){
        LinearLayout LL = new LinearLayout(getApplicationContext());
        LL.setTag(R.id.TYPE, counter);
        LL.setTag(R.id.ID, id);
        LL.setOrientation(LinearLayout.VERTICAL);
        LL.addView(getPlaceHolder());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 50);
        LL.setLayoutParams(lp);
        return LL;
    }

    private View getPlaceHolder() {
        Drawable dr = getResources().getDrawable(R.drawable.noalbum);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap,250, 250, true);
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageBitmap(resized);
        return  imageView;
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

    public class trackClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if((int) v.getTag(R.id.TYPE)==1) {
                Intent mIntent = new Intent(SearchActivity.this, TrackActivity.class);
                mIntent.putExtra("token", mAccessToken);
                mIntent.putExtra("track_id", (String) v.getTag(R.id.ID));
                startActivity(mIntent);
            }else {
                Toast.makeText(SearchActivity.this,
                        (String) v.getTag(R.id.ID), Toast.LENGTH_LONG).show();
            }
        }
    }


}
