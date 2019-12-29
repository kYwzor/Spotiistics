package com.example.spotiistics;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
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


public class SearchActivity extends BaseLoggedActivity {
    EditText et;

    @Override
    public void onSearchButtonClicked(View view){
        // Override to do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        et = findViewById(R.id.search_box);
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search();
                return false;
            }
        });
        findViewById(R.id.search_mag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                search();
            }
        });

    }

    private void search(){
        Map<String, Object> options = new HashMap<>();
        options.put("market", user.country);
        spotify.searchArtists(et.getText().toString(), options, new SpotifyCallback<ArtistsPager>() {
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

        spotify.searchAlbums(et.getText().toString(), options, new SpotifyCallback<AlbumsPager>() {
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

        spotify.searchTracks(et.getText().toString(), options, new SpotifyCallback<TracksPager>() {
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

        spotify.searchPlaylists(et.getText().toString(), options, new SpotifyCallback<PlaylistsPager>() {
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

    private void displayTracksSearch(TracksPager tracks) {
        Pager<Track> tracksPager = tracks.tracks;
        LinearLayout base = findViewById(R.id.track_search);
        base.removeAllViews();
        itemClickListener itemClickListener = new itemClickListener();

        for (Track t : tracksPager.items) {
            LinearLayout ll = Helper.createVerticalLinearLayout(t.name, t.id, getApplicationContext());
            ll.setTag(R.id.TYPE, 0);
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            // new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(t.album.images.get(0).url);
            if(t.album.images.size() != 0) {
                Glide
                        .with(this)
                        .load(t.album.images.get(0).url)
                        .placeholder(R.drawable.noalbum)
                        .into((ImageView) ll.getChildAt(0));
            }
        }
    }

    private void displayAlbumsSearch(AlbumsPager albums) {
        Pager<AlbumSimple> albumPager = albums.albums;
        LinearLayout base = findViewById(R.id.album_search);
        base.removeAllViews();

        itemClickListener itemClickListener = new itemClickListener();

        for (AlbumSimple a : albumPager.items) {
            LinearLayout ll = Helper.createVerticalLinearLayout(a.name, a.id, getApplicationContext());
            ll.setTag(R.id.TYPE, 1);
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            //new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(a.images.get(0).url);
            if(a.images.size() != 0) {
                Glide
                        .with(this)
                        .load(a.images.get(0).url)
                        .placeholder(R.drawable.noalbum)
                        .into((ImageView) ll.getChildAt(0));
            }
        }
    }

    private void displayArtistsSearch(ArtistsPager artists) {
        Pager<Artist> artistPager = artists.artists;
        LinearLayout base = findViewById(R.id.artista_search);
        base.removeAllViews();

        itemClickListener itemClickListener = new itemClickListener();

        for (Artist a : artistPager.items) {
            LinearLayout ll = Helper.createVerticalLinearLayout(a.name, a.id, getApplicationContext());
            ll.setTag(R.id.TYPE, 2);
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            if(a.images.size() !=0){
                //new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(a.images.get(0).url);
                Glide
                        .with(this)
                        .load(a.images.get(0).url)
                        .placeholder(R.drawable.noalbum)
                        .into((ImageView) ll.getChildAt(0));
            }
        }
    }

    private void displayPlaylistsSearch(PlaylistsPager playlists) {
        Pager<PlaylistSimple> playlistPager = playlists.playlists;
        LinearLayout base = findViewById(R.id.playlist_search);
        base.removeAllViews();

        itemClickListener itemClickListener = new itemClickListener();

        for (PlaylistSimple p : playlistPager.items) {
            LinearLayout ll = Helper.createVerticalLinearLayout(p.name, p.id, getApplicationContext());
            ll.setTag(R.id.TYPE, 3);
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            if(p.images.size() !=0){
                //new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(p.images.get(0).url);
                Glide
                        .with(this)
                        .load(p.images.get(0).url)
                        .placeholder(R.drawable.noalbum)
                        .into((ImageView) ll.getChildAt(0));
            }
        }
    }


    public class itemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int type = (int) v.getTag(R.id.TYPE);
            switch (type){
                case 0:
                    changeActivity(TrackActivity.class, (String) v.getTag(R.id.ID));
                    break;
                case 1:
                    changeActivity(AlbumActivity.class, (String) v.getTag(R.id.ID));
                    break;
                case 2:
                    changeActivity(ArtistActivity.class, (String) v.getTag(R.id.ID));
                    break;
                case 3:
                    changeActivity(PlaylistActivity.class, (String) v.getTag(R.id.ID));
                    break;
                default:
                    Toast.makeText(SearchActivity.this,
                            Integer.toString(type), Toast.LENGTH_LONG).show();
            }
        }
    }


}
