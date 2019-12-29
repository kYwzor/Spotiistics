package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.client.Response;

public class PlaylistStatsFragment extends Fragment {
    private WeakReference<PlaylistActivity> activityReference;
    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.playlist_statistics_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReference = new WeakReference<>((PlaylistActivity) getActivity());
    }


    void updateData() {
        final PlaylistActivity pa = activityReference.get();
        if (pa==null) return;

        long minDuration = Long.MAX_VALUE;
        long maxDuration = 0L;
        long totalDuration = 0L;
        for (PlaylistTrack track : pa.playlist.tracks.items){
            long duration_ms = track.track.duration_ms;
            totalDuration += duration_ms;
            if(duration_ms < minDuration){
                minDuration = duration_ms;
            }
            if(duration_ms > maxDuration){
                maxDuration = duration_ms;
            }
        }
        long meanDuration = 0L;
        if(pa.playlist.tracks.items.size() > 0) {
            meanDuration = totalDuration / pa.playlist.tracks.items.size();
        }
        TextView dt = rootview.findViewById(R.id.duracao_total);
        dt.setText(Helper.msToString(totalDuration));

        TextView dm = rootview.findViewById(R.id.duracao_media);
        dm.setText(Helper.msToString(meanDuration));

        TextView tb = rootview.findViewById(R.id.track_maior);
        tb.setText(Helper.msToString(maxDuration));

        TextView ts = rootview.findViewById(R.id.track_menor);
        ts.setText(Helper.msToString(minDuration));

        Map<String, Integer> maxArtist = new HashMap<>();
        for(PlaylistTrack t : pa.playlist.tracks.items){
            for(ArtistSimple a:  t.track.artists)
                if(maxArtist.get(a.id)!=null){
                    maxArtist.put(a.id,maxArtist.get(a.id)+1);
                }else{
                    maxArtist.put(a.id,1);
                }
        }
        List<Map.Entry<String, Integer>> greatestArtists = findGreatest(maxArtist, 5);
        for (Map.Entry<String, Integer> entry : greatestArtists){
            BaseActivity.spotify.getArtist(entry.getKey(), new SpotifyCallback<Artist>() {
                @Override
                public void success(Artist artist, Response response) {
                    LinearLayout base = rootview.findViewById(R.id.top_artists);

                    artistClickListener artistClickListener = new artistClickListener();

                    LinearLayout ll = Helper.createVerticalLinearLayout(artist.name, artist.id, pa);
                    ll.setOnClickListener(artistClickListener);
                    base.addView(ll);
                    if(artist.images.size() !=0){
                        // new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(a.album.images.get(0).url);
                        Glide
                                .with(pa)
                                .load(artist.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .into((ImageView) ll.getChildAt(0));
                    }

                }

                @Override
                public void failure(SpotifyError error) {
                    Toast.makeText(pa.getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
                }
            });
        }

        Map<String, Integer> maxAlbuns = new HashMap<>();
        for(PlaylistTrack t : pa.playlist.tracks.items){
            if(maxAlbuns.get(t.track.album.id)!=null){
                maxAlbuns.put(t.track.album.id,maxAlbuns.get(t.track.album.id)+1    );
            }else{
                maxAlbuns.put(t.track.album.id,1);
            }
        }
        List<Map.Entry<String, Integer>> greatestAlbuns = findGreatest(maxAlbuns, 5);
        for (Map.Entry<String, Integer> entry : greatestAlbuns){
            BaseActivity.spotify.getAlbum(entry.getKey(), new SpotifyCallback<Album>() {
                @Override
                public void success(Album album, Response response) {
                    LinearLayout base = rootview.findViewById(R.id.top_albums);

                    albumClickListener albumClickListener = new albumClickListener();

                    LinearLayout ll = Helper.createVerticalLinearLayout(album.name, album.id, pa);
                    ll.setOnClickListener(albumClickListener);
                    base.addView(ll);
                    if(album.images.size() !=0){
                        // new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(a.album.images.get(0).url);
                        Glide
                                .with(pa)
                                .load(album.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .into((ImageView) ll.getChildAt(0));
                    }
                }

                @Override
                public void failure(SpotifyError error) {
                    Toast.makeText(pa.getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
                }
            });
        }

        BaseLoggedActivity.spotify.getTracksAudioFeatures(join(pa.playlist.tracks.items), new SpotifyCallback<AudioFeaturesTracks>() {
            @Override
            public void success(AudioFeaturesTracks af, Response response) {
                float average_tempo = 0;
                float average_mood = 0;
                for(AudioFeaturesTrack a : af.audio_features){
                    average_tempo += a.tempo;
                    average_mood += a.valence;

                }
                TextView ritmo = rootview.findViewById(R.id.ritmo);
                ritmo.setText(average_tempo/pa.playlist.tracks.total + " bpm");    // TODO: Hardcoded strings

                TextView mood = rootview.findViewById(R.id.mood);
                mood.setText(String.valueOf(average_mood /pa.playlist.tracks.total));
            }

            @Override
            public void failure(SpotifyError error) {
                Toast.makeText(pa.getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
            }
        });

    }

    //https://stackoverflow.com/questions/21465821/how-to-get-5-highest-values-from-a-hashmap
    private static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> findGreatest(Map<K, V> map, int n) {
        Comparator<? super Map.Entry<K, V>> comparator = new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e0, Map.Entry<K, V> e1){
                V v0 = e0.getValue();
                V v1 = e1.getValue();
                return v0.compareTo(v1);
            }
        };
        PriorityQueue<Map.Entry<K, V>> highest = new PriorityQueue<>(n, comparator);
        for (Map.Entry<K, V> entry : map.entrySet()){
            highest.offer(entry);
            while (highest.size() > n){
                highest.poll();
            }
        }

        List<Map.Entry<K, V>> result = new ArrayList<>();
        while (highest.size() > 0){
            result.add(highest.poll());
        }

        return result;
    }

    public class artistClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(ArtistsActivity.class, (String) v.getTag(R.id.ID));
        }
    }

    public class albumClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(AlbumActivity.class, (String) v.getTag(R.id.ID));
        }
    }

    // based on https://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
    private static String join(List<PlaylistTrack> list) {
        StringBuilder sb = new StringBuilder();
        String loopDelim = "";

        for(PlaylistTrack a : list) {
            sb.append(loopDelim);
            sb.append(a.track.id);
            loopDelim = ",";
        }
        return sb.toString();
    }

}
