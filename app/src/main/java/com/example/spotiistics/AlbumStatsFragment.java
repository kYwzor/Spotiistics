package com.example.spotiistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.client.Response;

public class AlbumStatsFragment extends ItemFragment {
    private static final String TAG = AlbumStatsFragment.class.getSimpleName();
    private WeakReference<AlbumActivity> activityReference;
    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.album_statistics_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReference = new WeakReference<>((AlbumActivity) getActivity());
    }

    @Override
    public void updateData() {
        final AlbumActivity aa = activityReference.get();
        if (aa==null) return;
        TextView duracao = rootview.findViewById(R.id.duracao_total);
        long duracao_total = 0;
        long minDuration = Long.MAX_VALUE;
        long maxDuration = 0L;
        float average_tempo = 0;

        for(TrackSimple t : aa.album.tracks.items){

            duracao_total += t.duration_ms;
            if(t.duration_ms < minDuration){
                minDuration = t.duration_ms;
            }
            if(t.duration_ms > maxDuration){
                maxDuration = t.duration_ms;
            }
        }
        duracao.setText(Helper.msToString(duracao_total));

        TextView duracao_media = rootview.findViewById(R.id.duracao_media);
        duracao_media.setText(Helper.msToString(duracao_total/aa.album.tracks.total));

        TextView track_maior = rootview.findViewById(R.id.track_maior);
        track_maior.setText(Helper.msToString(maxDuration));

        TextView track_menor = rootview.findViewById(R.id.track_menor);
        track_menor.setText(Helper.msToString(minDuration));

        BaseLoggedActivity.spotify.getTracksAudioFeatures(join(aa.album.tracks.items), new SpotifyCallback<AudioFeaturesTracks>() {
            @Override
            public void success(AudioFeaturesTracks af, Response response) {
                float average_tempo = 0;
                float average_mood = 0;
                for(AudioFeaturesTrack a : af.audio_features){
                    average_tempo += a.tempo;
                    average_mood += a.valence;

                }
                TextView ritmo = rootview.findViewById(R.id.ritmo);
                ritmo.setText(average_tempo/aa.album.tracks.total + " bpm");    // TODO: Hardcoded strings

                TextView mood = rootview.findViewById(R.id.mood);
                mood.setText(String.valueOf(average_mood / aa.album.tracks.total));
            }

            @Override
            public void failure(SpotifyError error) {
                Toast.makeText(aa.getApplicationContext(), "Error loading", Toast.LENGTH_LONG).show();
                Log.e(TAG, error.getMessage());
            }
        });

    }

    // based on https://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java
    private static String join(List<TrackSimple> list) {
        StringBuilder sb = new StringBuilder();
        String loopDelim = "";

        for(TrackSimple a : list) {
            sb.append(loopDelim);
            sb.append(a.id);
            loopDelim = ",";
        }
        return sb.toString();
    }
}
