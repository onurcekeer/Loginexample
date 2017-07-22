package com.project.onur.playerx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by onur on 22.7.2017 at 22:20.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent) {
        Log.d("app","Network connectivity change");

        if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            Toast.makeText(context,"internet bağlantısı yok",Toast.LENGTH_LONG).show();
        }
    }

}