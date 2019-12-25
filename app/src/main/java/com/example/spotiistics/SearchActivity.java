package com.example.spotiistics;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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


public class SearchActivity extends ListsActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        findViewById(R.id.search_mag).setOnClickListener(new View.OnClickListener() {
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

    }

    private void displayTracksSearch(TracksPager tracks) {
        Pager<Track> tracksPager = tracks.tracks;
        LinearLayout base = findViewById(R.id.track_search);
        base.removeAllViews();
        itemClickListener itemClickListener = new itemClickListener();

        for (Track t : tracksPager.items) {
            LinearLayout ll = createLinearLayout(t.name, t.id, 0);
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            new DownloadImageTask((ImageView) ll.getChildAt(0), getItemSize()).execute(t.album.images.get(0).url);
        }
    }

    private void displayAlbumsSearch(AlbumsPager albums) {
        Pager<AlbumSimple> albumPager = albums.albums;
        LinearLayout base = findViewById(R.id.album_search);

        itemClickListener itemClickListener = new itemClickListener();

        for (AlbumSimple a : albumPager.items) {
            LinearLayout ll = createLinearLayout(a.name, a.id, 1);
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            new DownloadImageTask((ImageView) ll.getChildAt(0), getItemSize()).execute(a.images.get(0).url);
        }
    }

    private void displayArtistsSearch(ArtistsPager artists) {
        Pager<Artist> artistPager = artists.artists;
        LinearLayout base = findViewById(R.id.artista_search);

        itemClickListener itemClickListener = new itemClickListener();

        for (Artist a : artistPager.items) {
            LinearLayout ll = createLinearLayout(a.name, a.id,2);
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            if(a.images.size() !=0) new DownloadImageTask((ImageView) ll.getChildAt(0), getItemSize()).execute(a.images.get(0).url);
        }
    }

    private void displayPlaylistsSearch(PlaylistsPager playlists) {
        Pager<PlaylistSimple> playlistPager = playlists.playlists;
        LinearLayout base = findViewById(R.id.playlist_search);
        itemClickListener itemClickListener = new itemClickListener();

        for (PlaylistSimple p : playlistPager.items) {
            LinearLayout ll = createLinearLayout(p.name, p.id, 3);
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
            new DownloadImageTask((ImageView) ll.getChildAt(0), getItemSize()).execute(p.images.get(0).url);
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
                    // TODO
                    // changeActivity(AlbumActivity.class, (String) v.getTag(R.id.ID));
                    break;
                case 2:
                    // TODO
                    // changeActivity(ArtistsActivity.class, (String) v.getTag(R.id.ID));
                    break;
                case 3:
                    changeActivity(UserPlaylistsActivity.class, (String) v.getTag(R.id.ID));
                    break;
                default:
                    Toast.makeText(SearchActivity.this,
                            Integer.toString(type), Toast.LENGTH_LONG).show();
            }
        }
    }


}
