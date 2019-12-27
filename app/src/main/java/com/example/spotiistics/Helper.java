package com.example.spotiistics;

abstract class Helper {
    static String msToString(long ms){
        long seconds = ms / 1000;
        return seconds / 60 + "min " + seconds % 60 + "sec";   // TODO: hardcoded strings
    }
}
