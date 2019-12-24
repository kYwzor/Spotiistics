package com.example.spotiistics;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ListsActivity extends BaseLoggedActivity {
    @Override
    public int getItemSize() {
        return 250; // TODO: this should be a scaling value
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    LinearLayout createLinearLayout(String name, String id){
        LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 50);
        ll.setLayoutParams(lp);

        ImageView iv = new ImageView(getApplicationContext());
        setPlaceHolder(iv);
        ll.addView(iv);
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
        tv.setMaxWidth(getItemSize());
        tv.setText(name);
        return tv;
    }
}
