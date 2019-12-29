package com.example.spotiistics;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.spotiistics.Database.ArtistData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ArtistsInfoFragment extends ItemFragment {
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
        this.mFragmentListener.onFragmentSet(false);
        activityReference = new WeakReference<>((ArtistsActivity) getActivity());
    }


    void updateData(ArtistData artistData, ArrayList<ImageView> albumIvs) {
        LinearLayout base = rootview.findViewById(R.id.lista_albums);

        itemClickListener itemClickListener = new itemClickListener();
        for (int i=0; i<artistData.albumNames.size(); i++){
            //LinearLayout ll = Helper.createLinearLayout(artistData.albumNames.get(i), artistData.albumIds.get(i), albumIvs.get(i), getContext());
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOnClickListener(itemClickListener);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 0, 10, 50);   //TODO: should be scaling
            ll.setLayoutParams(lp);
            ll.setTag(R.id.ID, artistData.albumIds.get(i));

            ImageView iv = albumIvs.get(i);
            ll.addView(iv);
            iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);
            iv.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);

            TextView tv = new TextView(getContext());
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setMaxLines(1);
            tv.setText(artistData.albumNames.get(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(15,0,0,0);    //TODO: should be scaling
            tv.setLayoutParams(params);
            tv.setTextColor(Color.WHITE);

            Typeface tf = ResourcesCompat.getFont(getContext(), R.font.roboto_light);
            tv.setTypeface(tf);
            tv.setPadding(0,10, 0,0);
            ll.addView(tv);

            base.addView(ll);
        }
    }

    public class itemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            activityReference.get().changeActivity(AlbumActivity.class, (String) v.getTag(R.id.ID));
        }
    }

}
