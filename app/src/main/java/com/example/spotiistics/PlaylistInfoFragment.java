package com.example.spotiistics;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.client.Response;

public class PlaylistInfoFragment extends ItemFragment {
    private WeakReference<PlaylistActivity> activityReference;
    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.playlist_info_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReference = new WeakReference<>((PlaylistActivity) getActivity());
    }

    @Override
    public void updateData() {
        final PlaylistActivity pa = activityReference.get();
        if (pa==null) return;
        TextView n_track = rootview.findViewById(R.id.playlist_n_track);
        n_track.setText(pa.playlist.tracks.total + " tracks");     //TODO: Hardcoded text);

        LinearLayout base = rootview.findViewById(R.id.lista_tracks);


        for(final PlaylistTrack track : pa.playlist.tracks.items) {
            BaseActivity.spotify.getAlbum(track.track.album.id, new SpotifyCallback<Album>() {
                @Override
                public void success(Album a, Response response) {
                    LinearLayout base = rootview.findViewById(R.id.lista_tracks);

                    itemClickListener itemClickListener = new itemClickListener();

                    LinearLayout ll = new LinearLayout(pa.getApplicationContext());
                    ll.setOnClickListener(itemClickListener);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(20, 0, 10, 50);
                    ll.setLayoutParams(lp);
                    ll.setTag(R.id.ID, track.track.id);


                    ImageView iv = new ImageView(pa.getApplicationContext());
                    iv.setImageDrawable(getResources().getDrawable(R.drawable.noalbum));    //placeholder
                    ll.addView(iv);
                    iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);
                    iv.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);


                    TextView tv = new TextView(pa.getApplicationContext());
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                    tv.setMaxLines(1);
                    tv.setText(track.track.name);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(15,0,0,0);
                    tv.setLayoutParams(params);
                    tv.setTextColor(Color.WHITE);

                    Typeface tf = ResourcesCompat.getFont(pa.getApplicationContext(), R.font.roboto_light);
                    tv.setTypeface(tf);
                    tv.setPadding(0,10, 0,0);
                    ll.addView(tv);

                    base.addView(ll);
                    if (a.images.size() != 0){
                        // new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(a.images.get(0).url);
                        Glide
                                .with(pa)
                                .load(a.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .into((ImageView) ll.getChildAt(0));
                    }



                }

                @Override
                public void failure(SpotifyError error) {
                    Toast.makeText(pa, "Error loading", Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    public class itemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(TrackActivity.class, (String) v.getTag(R.id.ID));
        }
    }
}
