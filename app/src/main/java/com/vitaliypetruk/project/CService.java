package com.vitaliypetruk.project;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

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
       if(XMPP.connection!=null)XMPP.connection.disconnect();
        if(intent==null)  Log.d("XMPP", "intent is null");

        Bundle loginData = intent.getExtras();
        xmpp = XMPP.getInstance(HOST, "", "");
        if (loginData != null) {
            USERNAME = loginData.getString("login_username");
            PASSWORD = loginData.getString("login_password");
            loginData.clear();

            xmpp.connect();
            Log.d("XMPP", "Service con inside login " + XMPP.connection.isConnected());

            do{
            try {
                XMPP.connection.login(USERNAME,PASSWORD);
                isLogined = true;
            } catch (XMPPException e) {
                Toast.makeText(getBaseContext(),"Try again",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (SmackException e) {
                Toast.makeText(getBaseContext(),"Try again2",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(getBaseContext(),"Try again3",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }} while(!isLogined);
        } else {
            xmpp = XMPP.getInstance(HOST, "", "");
            xmpp.connect();
        }
        return START_STICKY;
    }

    private void performLogin(String username, String password) {
        isLogined = true;
        xmpp.login(username, password);
    }
    public void onDestroy(){
        Log.d("XMPP","onDestroy");
        XMPP.connection = null;
    }
}
