package com.vitaliypetruk.project;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;


public class Login extends Activity {
    private ImageButton performLogin;
    private Button createNewAccount;
    private TextView username;
    private TextView password;
    private boolean isLogined = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        performLogin = (ImageButton)findViewById(R.id.login_performLogin);
        createNewAccount=(Button)findViewById(R.id.login_newAccount);
        username = (TextView)findViewById(R.id.login_username);
        password = (TextView)findViewById(R.id.login_password);
    }
    public void performLogin(View view){
        Intent intent = new Intent(this,CService.class);

        Bundle loginingData =new Bundle();
        loginingData.putString("login_password", password.getText().toString());
        loginingData.putString("login_username", username.getText().toString());
        Log.d("XMPP", "Service con -***** " + password.getText().toString());
        Log.d("XMPP", "Service con -***** " + username.getText().toString());
        intent.putExtras(loginingData);

        startService(intent);
        XMPP.getInstance("10.42.0.1",username.getText().toString(),password.getText().toString()).connect();

        do{
            try {
                XMPP.connection.login(username.getText().toString(),password.getText().toString());
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
        startActivity(new Intent(Login.this, Home.class));
            finish();
    }


    @Override
    public void onResume() {
        super.onResume();

    }
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void loadNewAcc(View view){
        startService(new Intent(this,CService.class));
        startActivity(new Intent(this,NewAccount_MainInfo.class));
    }
}
