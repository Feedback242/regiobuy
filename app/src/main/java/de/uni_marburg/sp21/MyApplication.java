package de.uni_marburg.sp21;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    public static String APP_TAG = "REGIOBUY";
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    /**
     * A simple way to get the App-Context, so you don't have to pass it to every method
     * @return the App-Context
     */
    public static Context getAppContext() {
        return MyApplication.context;
    }
}
