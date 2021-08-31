package com.barmej.streetissues.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceLatLong {
    private static final String LAT = "LAT";
    private static final String LON = "LON";

    public static void setLatLng(Context context, double lat, double lon) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(LAT, String.valueOf(lat)).apply();
        sp.edit().putString(LON, String.valueOf(lon)).apply();
    }

    public static double getLatitude(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String lat = sp.getString(LAT, "33.315239");
        return Double.parseDouble(lat);
    }

    public static double getLongitude(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String lat = sp.getString(LON, "44.366147");
        return Double.parseDouble(lat);
    }

}
