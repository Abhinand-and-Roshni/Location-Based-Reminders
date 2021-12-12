package com.example.reminderapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference
{
    static final String PREF_PHONE_NO= "phoneno";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setPhoneNo(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PHONE_NO, userName);
        editor.commit();
    }

    public static String getPhoneNo(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PHONE_NO, "");
    }

    public static void clearPhoneNo(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }
}