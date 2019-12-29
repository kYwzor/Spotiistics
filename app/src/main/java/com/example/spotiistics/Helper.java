package com.example.spotiistics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

abstract class Helper {
    static CharSequence timestampToReadable(long timestamp){
        //return DateFormat.getDateInstance().format(new Date(timestamp));
        return DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    static String msToString(String placeholder, long ms){
        long seconds = ms / 1000;
        return String.format(placeholder, seconds / 60, seconds % 60);
    }

    // taken from https://stackoverflow.com/a/10600736
    static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // taken from https://stackoverflow.com/a/21466546
    static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> findGreatest(Map<K, V> map, int n) {
        Comparator<? super Map.Entry<K, V>> comparator = new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e0, Map.Entry<K, V> e1){
                V v0 = e0.getValue();
                V v1 = e1.getValue();
                return v0.compareTo(v1);
            }
        };
        PriorityQueue<Map.Entry<K, V>> highest = new PriorityQueue<>(n, comparator);
        for (Map.Entry<K, V> entry : map.entrySet()){
            highest.offer(entry);
            while (highest.size() > n){
                highest.poll();
            }
        }

        List<Map.Entry<K, V>> result = new ArrayList<>();
        while (highest.size() > 0){
            result.add(highest.poll());
        }

        return result;
    }

    static LinearLayout createHorizontalLinearLayout(String name, String id, ImageView iv, Context context){
        Resources resources = context.getResources();
        LinearLayout ll = new LinearLayout(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) resources.getDimension(R.dimen.dpTwenty), 0, (int) resources.getDimension(R.dimen.dpTen), (int) resources.getDimension(R.dimen.dpFifty));
        ll.setLayoutParams(lp);

        ll.addView(iv);
        iv.getLayoutParams().height = (int) resources.getDimension(R.dimen.imageview_thumbnail_size);
        iv.getLayoutParams().width = (int) resources.getDimension(R.dimen.imageview_thumbnail_size);

        TextView tv = new TextView(context);
        tv.setMaxLines(4);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setText(name);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) resources.getDimension(R.dimen.dpFifteen),0,0,0);
        tv.setLayoutParams(params);
        tv.setTextColor(Color.WHITE);

        Typeface tf = ResourcesCompat.getFont(context, R.font.roboto_light);
        tv.setTypeface(tf);
        tv.setPadding(0,(int) resources.getDimension(R.dimen.dpTen), 0,0);
        ll.addView(tv);

        ll.setTag(R.id.ID, id);
        return ll;
    }

    static LinearLayout createVerticalLinearLayout(String name, String id, ImageView iv, Context context){
        Resources resources = context.getResources();
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) resources.getDimension(R.dimen.dpTen), 0, (int) resources.getDimension(R.dimen.dpTen), 0);
        ll.setLayoutParams(lp);

        ll.addView(iv);
        iv.getLayoutParams().height = (int) resources.getDimension(R.dimen.imageview_thumbnail_size);
        iv.getLayoutParams().width = (int) resources.getDimension(R.dimen.imageview_thumbnail_size);

        ll.addView(Helper.createTextView(name, context));

        ll.setTag(R.id.ID, id);
        return ll;
    }

    static LinearLayout createVerticalLinearLayout(String name, String id, Context context){
        ImageView iv = new ImageView(context);
        iv.setImageDrawable(context.getResources().getDrawable(R.drawable.noalbum));    //placeholder
        return createVerticalLinearLayout(name, id, iv, context);
    }

    static TextView createTextView(String name, Context context) {
        TextView tv = new TextView(context);
        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setText(name);
        tv.setTextColor(Color.WHITE);

        Typeface tf = ResourcesCompat.getFont(context, R.font.roboto_light);
        tv.setTypeface(tf);
        tv.setPadding(0,(int) context.getResources().getDimension(R.dimen.dpFifteen), 0,0);
        return tv;
    }
}
