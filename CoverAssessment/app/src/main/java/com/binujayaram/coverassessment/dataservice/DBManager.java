package com.binujayaram.coverassessment.dataservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.binujayaram.coverassessment.Constants;
import com.binujayaram.coverassessment.interfaces.Response;
import com.binujayaram.coverassessment.models.Carriers;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class DBManager {
    public static void saveSelectedPlace(Context applicationContext, String selectedPlace){
        saveToDB(applicationContext, Constants.KEY_DB_PLCAE, selectedPlace);
    }

    public static void saveSelectedCarrier(Context applicationContext, String selectedCarrier){
        saveToDB(applicationContext, Constants.KEY_DB_CARRIER, selectedCarrier);
    }

    public static String getSelectedPlace(Context applicationContext){
        return getFromDB(applicationContext, Constants.KEY_DB_PLCAE);
    }

    public static String getSelectedCarrier(Context applicationContext){
        return getFromDB(applicationContext, Constants.KEY_DB_CARRIER);
    }

    private static void saveToDB(Context applicationContext, String key, String value){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(
                applicationContext.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    private static String getFromDB(Context applicationContext, String key){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(
                applicationContext.getPackageName(), Context.MODE_PRIVATE);
       return sharedPreferences.getString(key, "");
    }

    public static void loadJSONFromAsset(final Context context, final Response response) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String json = null;
                try {
                    InputStream is = context.getAssets().open("carriers.json");

                    int size = 0;
                    try {
                        size = is.available();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] buffer = new byte[size];

                    is.read(buffer);

                    is.close();

                    json = new String(buffer, "UTF-8");
                    Gson gson = new Gson();
                    Carriers carriers = gson.fromJson(json, Carriers.class);
                    if(carriers==null) throw new NullPointerException();
                    response.onSuccess(carriers);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    response.onFaliure(null);
                }
                catch (NullPointerException e){
                    response.onFaliure(null);
                }
            }
        });
    }



    public static void setCurrentFragment(Context applicationContext, int pageNo){
        saveToSharedPref(applicationContext, Constants.KEY_DB_CURRENT_FRAGMENT, pageNo);
    }

    public static int getCurrentFragment(Context applicationContext){
       return getFromSharedPref(applicationContext, Constants.KEY_DB_CURRENT_FRAGMENT);
    }

    private static void saveToSharedPref(Context applicationContext, String key, int value){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(
                applicationContext.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    private static int getFromSharedPref(Context applicationContext, String key){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(
                applicationContext.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static void clearAll(Context applicationContext, Response response){
    /*    SharedPreferences pref = applicationContext.getSharedPreferences(
                applicationContext.getPackageName(), Context.MODE_PRIVATE);
        pref.edit().clear().apply();*/
        SharedPreferences pref = applicationContext.getSharedPreferences(
                applicationContext.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        response.onSuccess(null);
//      saveSelectedPlace(applicationContext, null);
//      saveSelectedCarrier(applicationContext, null);
//      setCurrentFragment(applicationContext, 0);
//        response.onSuccess(null);
    }
}
