package com.example.spotiistics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.client.Response;

public class ArtistsInfoFragment extends ItemFragment {
    private static final String TAG = ArtistsInfoFragment.class.getSimpleName();
    private WeakReference<ArtistsActivity> activityReference;
    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.artista_info_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityReference = new WeakReference<>((ArtistsActivity) getActivity());
    }

    @Override
    public void updateData() {
        final ArtistsActivity aa = activityReference.get();
        if (aa==null) return;
        Map<String, Object> options  = new HashMap<>();
        options.put("market", BaseActivity.user.country);
        BaseActivity.spotify.getArtistAlbums(aa.artist.id, options, new SpotifyCallback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> ap, Response response) {
                LinearLayout base = rootview.findViewById(R.id.lista_albums);

                itemClickListener itemClickListener = new itemClickListener();
                for(Album a : ap.items) {
                    LinearLayout ll = aa.createLinearLayout(a.name, a.id);
                    ll.setOnClickListener(itemClickListener);
                    base.addView(ll);
                    if (a.images.size() != 0){
                        // new DownloadImageTask((ImageView) ll.getChildAt(0)).execute(a.images.get(0).url);
                        Glide
                                .with(aa)
                                .load(a.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .into((ImageView) ll.getChildAt(0));
                    }

                }

            }

            @Override
            public void failure(SpotifyError error) {
                Toast.makeText(aa, "Error loading", Toast.LENGTH_LONG).show();
                Log.e(TAG, error.getMessage());
            }
        });
    }

    public class itemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(AlbumActivity.class, (String) v.getTag(R.id.ID));
        }
    }

}
