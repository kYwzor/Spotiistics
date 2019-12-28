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
                    LinearLayout ll = new LinearLayout(aa.getApplicationContext());
                    ll.setOnClickListener(itemClickListener);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(20, 0, 10, 50);
                    ll.setLayoutParams(lp);
                    ll.setTag(R.id.ID, a.id);


                    ImageView iv = new ImageView(aa.getApplicationContext());
                    iv.setImageDrawable(getResources().getDrawable(R.drawable.noalbum));    //placeholder
                    ll.addView(iv);
                    iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);
                    iv.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);


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
