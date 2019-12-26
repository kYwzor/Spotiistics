package com.example.spotiistics;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.client.Response;

public class AlbumInfoFragment extends ItemFragment {
    WeakReference<AlbumActivity> activityReference;
    View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.album_info_tab, container, false);
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
        TextView data_lancamento = rootview.findViewById(R.id.album_data);
        data_lancamento.setText(aa.album.release_date);

        TextView n_track = rootview.findViewById(R.id.n_track);
        n_track.setText(aa.album.tracks.total + " tracks");

        LinearLayout base = rootview.findViewById(R.id.lista_tracks);
        itemClickListener itemClickListener = new itemClickListener();

        for( TrackSimple a : aa.album.tracks.items) {
            LinearLayout ll = new LinearLayout(aa.getApplicationContext());
            ll.setOnClickListener(itemClickListener);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 0, 10, 50);
            ll.setLayoutParams(lp);
            ll.setTag(a.id);

            TextView tv = new TextView(aa.getApplicationContext());
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setMaxLines(1);
            tv.setText(a.name);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(15,0,0,0);
            tv.setLayoutParams(params);
            tv.setTextColor(Color.WHITE);

            Typeface tf = ResourcesCompat.getFont(aa.getApplicationContext(), R.font.roboto_light);
            tv.setTypeface(tf);
            tv.setPadding(0,10, 0,0);
            ll.addView(tv);

            base.addView(ll);
        }

    }

    public class itemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(TrackActivity.class, (String) v.getTag());
        }
    }
}
