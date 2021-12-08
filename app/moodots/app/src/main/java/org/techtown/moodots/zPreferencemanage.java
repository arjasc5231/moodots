package org.techtown.moodots;

import android.content.Context;
import android.content.SharedPreferences;

public class zPreferencemanage {
    public static final String preferencename= "preference";
    private static SharedPreferences getPrefetences(Context context){
        return context.getSharedPreferences(preferencename, Context.MODE_PRIVATE);
    }
    public static void setInt(Context context, String key, int value){
        SharedPreferences sharedPreferences= getPrefetences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public static void setString(Context context, String key, String value){
        SharedPreferences sharedPreferences = getPrefetences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static int getInt(Context context, String key){
        SharedPreferences sharedPreferences = getPrefetences(context);
        int value = sharedPreferences.getInt(key, -3);
        return value;
    }
    public static String getString(Context context, String key){
        SharedPreferences sharedPreferences = getPrefetences(context);
        String value = sharedPreferences.getString(key, "font/nanumsquareround.otf");
        return value;
    }
    public static void removeKey(Context context, String key){
        SharedPreferences sharedPreferences = getPrefetences(context);
        SharedPreferences.Editor edit= sharedPreferences.edit();
        edit.remove(key);
        edit.commit();
    }
    public static void clear(Context context){
        SharedPreferences sharedPreferences = getPrefetences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        edit.commit();
    }
}
