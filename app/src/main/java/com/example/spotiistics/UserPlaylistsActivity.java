package com.example.spotiistics;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.spotiistics.Database.UserPlaylistsData;
import com.example.spotiistics.Database.UserPlaylistsDataDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import retrofit.client.Response;

public class UserPlaylistsActivity extends SyncableActivity {
    private static final String TAG = UserPlaylistsActivity.class.getSimpleName();
    UserPlaylistsDataDao userPlaylistsDataDao;
    UserPlaylistsData userPlaylistsData;
    boolean inDatabase;
    boolean dataReady;

    ArrayList<ImageView> ownIvs;
    ArrayList<ImageView> otherIvs;

    @Override
    public void onPlaylistsButtonClicked(View view){
        // Override to do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_playlist);

        userPlaylistsDataDao = database.userPlaylistsDataDao();
        UserPlaylistsData[] ups =  userPlaylistsDataDao.get(user.id);
        if(ups.length==0){
            startSync();
        }
        else {
            inDatabase = true;
            userPlaylistsData = ups[0];

            ownIvs = new ArrayList<>();
            for (String playlistId : userPlaylistsData.ownPlaylistIds){
                ImageView iv = new ImageView(this);
                ownIvs.add(iv);
                loadBitmap(playlistId, iv);
            }

            otherIvs = new ArrayList<>();
            for (String playlistId : userPlaylistsData.otherPlaylistIds){
                ImageView iv = new ImageView(this);
                otherIvs.add(iv);
                loadBitmap(playlistId, iv);
            }
            dataReady = true;
            updateView();
        }
    }

    @Override
    void startSync() {
        dataReady = false;
        userPlaylistsData = new UserPlaylistsData(user.id);
        Map<String, Object> options  = new HashMap<>();
        options.put("limit", 50);
        spotify.getMyPlaylists(options, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                Toast.makeText(getApplicationContext(), R.string.sync_fail, Toast.LENGTH_LONG).show();
                Log.e(TAG, spotifyError.getMessage());
            }

            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, Response response) {
                ArrayList<String> ownPlaylistNames = new ArrayList<>();
                ArrayList<String> ownPlaylistIds = new ArrayList<>();
                ArrayList<String> otherPlaylistNames = new ArrayList<>();
                ArrayList<String> otherPlaylistIds = new ArrayList<>();

                ownIvs = new ArrayList<>();
                otherIvs = new ArrayList<>();
                for (final PlaylistSimple playlist : playlistSimplePager.items) {
                    ImageView iv = new ImageView(UserPlaylistsActivity.this);
                    if(playlist.owner.id.equals(user.id)){
                        ownPlaylistNames.add(playlist.name);
                        ownPlaylistIds.add(playlist.id);
                        ownIvs.add(iv);
                    }
                    else {
                        otherPlaylistNames.add(playlist.name);
                        otherPlaylistIds.add(playlist.id);
                        otherIvs.add(iv);
                    }
                    if(playlist.images.size() != 0) {
                        Glide
                                .with(UserPlaylistsActivity.this)
                                .load(playlist.images.get(0).url)
                                .placeholder(R.drawable.noalbum)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        saveBitmap(playlist.id, Helper.drawableToBitmap(resource));
                                        return false;
                                    }
                                })
                                .into(iv);
                    }
                }
                userPlaylistsData.ownPlaylistNames = ownPlaylistNames;
                userPlaylistsData.ownPlaylistIds = ownPlaylistIds;
                userPlaylistsData.otherPlaylistNames = otherPlaylistNames;
                userPlaylistsData.otherPlaylistIds = otherPlaylistIds;

                dataReady = true;
                if(inDatabase){
                    userPlaylistsDataDao.update(userPlaylistsData);
                }
                else {
                    userPlaylistsDataDao.insert(userPlaylistsData);
                    inDatabase = true;
                }
                onSyncDone();
                updateView();
            }
        });
    }

    @Override
    boolean isReady() {
        return dataReady;
    }

    private void updateView() {
        TextView sync = findViewById(R.id.sync_date);
        sync.setText(Helper.timestampToReadable(userPlaylistsData.timestamp));
        playlistClickListener playlistClickListener = new playlistClickListener();

        LinearLayout yourPlaylist = findViewById(R.id.yourPlaylist);
        yourPlaylist.removeAllViews();
        for (int i = 0; i<userPlaylistsData.ownPlaylistNames.size(); i++){
            LinearLayout ll = Helper.createVerticalLinearLayout(userPlaylistsData.ownPlaylistNames.get(i), userPlaylistsData.ownPlaylistIds.get(i), ownIvs.get(i), getApplicationContext());
            ll.setOnClickListener(playlistClickListener);
            yourPlaylist.addView(ll);
        }

        LinearLayout followedPlaylist = findViewById(R.id.followedPlaylist);
        followedPlaylist.removeAllViews();
        for (int i = 0; i<userPlaylistsData.otherPlaylistNames.size(); i++){
            LinearLayout ll = Helper.createVerticalLinearLayout(userPlaylistsData.otherPlaylistNames.get(i), userPlaylistsData.otherPlaylistIds.get(i), otherIvs.get(i), getApplicationContext());
            ll.setOnClickListener(playlistClickListener);
            followedPlaylist.addView(ll);
        }
    }

    public class playlistClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            changeActivity(PlaylistActivity.class, (String) v.getTag(R.id.ID));
        }
    }

}
