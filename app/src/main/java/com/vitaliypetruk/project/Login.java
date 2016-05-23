package com.vitaliypetruk.project;

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


public class Login extends ActionBarActivity {
    private ImageButton performLogin;
    private Button createNewAccount;
    private TextView username;
    private TextView password;

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
        Log.d("CService", "password " + password.getText());
        Bundle loginingData =new Bundle();
        loginingData.putString("login_password", password.getText().toString());
        loginingData.putString("login_username", username.getText().toString());

        intent.putExtras(loginingData);
        startService(intent);

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
