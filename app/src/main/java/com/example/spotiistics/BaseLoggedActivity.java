package com.example.spotiistics;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public abstract class BaseLoggedActivity extends BaseActivity {
    protected String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("id"); // Might be null but it's ok
    }

    public void onPlaylistsButtonClicked(View view){
        //Toast.makeText(getApplicationContext(), "Switching to playlists", Toast.LENGTH_LONG).show();
        changeActivity(UserPlaylistsActivity.class);
    }

    public void onSearchButtonClicked(View view){
        //Toast.makeText(getApplicationContext(), "Switching to search", Toast.LENGTH_LONG).show();
        changeActivity(SearchActivity.class);
    }

    public void onSyncButtonClicked(View view){
        //Toast.makeText(getApplicationContext(), "Switching to sync", Toast.LENGTH_LONG).show();
        //TODO
        //changeActivity(SyncActivity.class);
    }
    void changeActivity(Class c){
        Intent mIntent = new Intent(getApplicationContext(), c);
        startActivity(mIntent);
    }

    void changeActivity(Class c, String id){
        Intent mIntent = new Intent(getApplicationContext(), c);
        mIntent.putExtra("id", id);
        startActivity(mIntent);
    }

    LinearLayout createLinearLayout(String name, String id){
        LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.imageview_thumbnail_size), (int) getResources().getDimension(R.dimen.imageview_thumbnail_size));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 10, 0);   //TODO: should be scaling
        ll.setLayoutParams(lp);

        ImageView iv = new ImageView(getApplicationContext());
        iv.setImageDrawable(getResources().getDrawable(R.drawable.noalbum));    //placeholder
        ll.addView(iv);
        iv.getLayoutParams().height = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);
        iv.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);

        ll.addView(createTextView(name));

        ll.setTag(R.id.ID, id);
        return ll;
    }

    LinearLayout createLinearLayout(String name, String id, int type){
        LinearLayout ll = createLinearLayout(name, id);
        ll.setTag(R.id.TYPE, type);
        return ll;
    }

    TextView createTextView(String name) {
        TextView tv = new TextView(getApplicationContext());
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxLines(1);
        //tv.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);
        //tv.setMaxWidth(getItemSize());
        tv.setText(name);
        tv.setTextColor(Color.WHITE);

        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light);
        tv.setTypeface(tf);
        tv.setPadding(0,15, 0,0);   //TODO: should be scaling
        return tv;
    }
}
