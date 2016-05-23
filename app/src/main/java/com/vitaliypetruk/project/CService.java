package com.vitaliypetruk.project;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class CService extends Service {
    private String USERNAME;
    private String PASSWORD;
    private String HOST = "192.168.178.242";
    public static XMPP xmpp;
    private final String TAG = "CService";
    private boolean isLogined = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        Bundle loginData = intent.getExtras();
        if (loginData != null) {
            USERNAME = loginData.getString("login_username");
            PASSWORD = loginData.getString("login_password");
            xmpp = XMPP.getInstance(HOST, "", "");
            xmpp.connect();
            Log.d("XMPP", "Service con - " + XMPP.connection.isConnected());
            performLogin(USERNAME, PASSWORD);
        } else {
            xmpp = XMPP.getInstance(HOST, "", "");
            xmpp.connect();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void performLogin(String username, String password) {
        isLogined = true;
        xmpp.login(username, password);
    }
    public void onDestroy(){

    }
}
