package com.example.spotiistics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.HashMap;

abstract class Helper {
    static String msToString(long ms){
        long seconds = ms / 1000;
        return seconds / 60 + "min " + seconds % 60 + "sec";   // TODO: hardcoded strings
    }

    static String stringPop(int pop){
        HashMap<Integer,String> hashmap = new HashMap();
        // TODO: hardcoded strings
        hashmap.put(0,"1- O que é isto?");
        hashmap.put(1,"2- Underground");
        hashmap.put(2,"3- Só para alguns");
        hashmap.put(3,"4- Bastante Popular");
        hashmap.put(4,"5- Topo das tabelas");
        hashmap.put(5,"6- A mais popular");

        int score = pop / 20; //6 categories

        return hashmap.get(score);
    }

    // copied from https://stackoverflow.com/a/10600736
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

    static TextView createTextView(String name, Context context) {
        TextView tv = new TextView(context);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxLines(1);
        //tv.getLayoutParams().width = (int) getResources().getDimension(R.dimen.imageview_thumbnail_size);
        //tv.setMaxWidth(getItemSize());
        tv.setText(name);
        tv.setTextColor(Color.WHITE);

        Typeface tf = ResourcesCompat.getFont(context, R.font.roboto_light);
        tv.setTypeface(tf);
        tv.setPadding(0,15, 0,0);   //TODO: should be scaling
        return tv;
    }
}
