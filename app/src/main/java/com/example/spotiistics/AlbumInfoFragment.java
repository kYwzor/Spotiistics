package com.example.spotiistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.spotiistics.Database.AlbumData;

public class AlbumInfoFragment extends ItemFragment {
    private View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.album_info_tab, container, false);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mFragmentListener.onFragmentSet(false);
    }

    void updateData(AlbumData a) {
        TextView data_lancamento = rootview.findViewById(R.id.album_data);
        data_lancamento.setText(a.releaseDate);

        TextView n_track = rootview.findViewById(R.id.n_track);
        String n_track_string = String.format(getResources().getString(R.string.n_tracks), a.trackNames.size());
        n_track.setText(n_track_string);

        LinearLayout base = rootview.findViewById(R.id.lista_tracks);
        itemClickListener itemClickListener = new itemClickListener();

        for (int i=0; i <a.trackNames.size(); i++){
            TextView tv = Helper.createTextView(a.trackNames.get(i), getContext());
            tv.setOnClickListener(itemClickListener);
            tv.setTag(a.trackIds.get(i));
            base.addView(tv);
        }
    }

    public class itemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ((AlbumActivity)getActivity()).changeActivity(TrackActivity.class, (String) v.getTag());
        }
    }
}
