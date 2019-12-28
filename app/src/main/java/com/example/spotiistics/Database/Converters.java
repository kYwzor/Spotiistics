package com.example.spotiistics.Database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

// based on https://stackoverflow.com/a/45071364
public class Converters {
    @TypeConverter
    public static ArrayList<String> stringsFromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringFromStrings(ArrayList<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static ArrayList<Long> longsFromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringFromLongs(ArrayList<Long> list) {
        return new Gson().toJson(list);
    }
}