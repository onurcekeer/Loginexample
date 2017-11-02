package com.project.onur.playerx;

import android.app.Application;


public class MainApp extends Application {
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        MainApp.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}