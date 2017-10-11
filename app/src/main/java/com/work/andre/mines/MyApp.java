package com.work.andre.mines;

import android.app.Application;
import android.util.Log;

import com.work.andre.mines.database.DBase;

public class MyApp extends Application {
    private static DBase myDBase = null;

    @Override
    public void onCreate() {

        super.onCreate();
        Log.d("MyApp", "onCreate111");

        myDBase = new DBase(getApplicationContext());
    }

    public static DBase getMyDBase() {
        return myDBase;
    }
}
