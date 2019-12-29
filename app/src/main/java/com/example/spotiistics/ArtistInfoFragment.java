package com.example.spotiistics;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.spotiistics.Database.ArtistData;

import java.util.ArrayList;

public class ArtistInfoFragment extends ItemFragment {

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
    }


    void updateData(ArtistData artistData, ArrayList<ImageView> albumIvs) {
        LinearLayout base = rootview.findViewById(R.id.lista_albums);
        base.removeAllViews();
        itemClickListener itemClickListener = new itemClickListener();
        for (int i=0; i<artistData.albumNames.size(); i++){
            LinearLayout ll = Helper.createHorizontalLinearLayout(artistData.albumNames.get(i), artistData.albumIds.get(i), albumIvs.get(i), getContext());
            ll.setOnClickListener(itemClickListener);
            base.addView(ll);
        }
    }

    public class itemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((ArtistActivity) getActivity()).changeActivity(AlbumActivity.class, (String) v.getTag(R.id.ID));
        }
    }

}
