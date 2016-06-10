package com.vitaliypetruk.project;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class NewAccount_MainInfo extends ActionBarActivity {
    private EditText username;
    private EditText name;
    private EditText mail;
    private EditText password;
    private EditText confirmPassword;
    private Button loadExtendsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account__main_info);
        username = (EditText) findViewById(R.id.newAcc_mi_username);
        name = (EditText) findViewById(R.id.newAcc_mi_name);
        mail = (EditText) findViewById(R.id.newAcc_mi_mail);
        password = (EditText) findViewById(R.id.newAcc_mi_password);
        confirmPassword = (EditText) findViewById(R.id.newAcc_mi_conf_password);
        loadExtendsInfo = (Button) findViewById(R.id.newAcc_mi_next);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_account__main_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveMi_loadExtendsInfo(View view) {

        Map<String, String> setValues = new HashMap<String, String>();
        setValues.put("name", name.getText().toString());
        setValues.put("email", mail.getText().toString());
        try {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                XMPP.createUser(username.getText().toString(), password.getText().toString(), setValues);
                XMPP.login(username.getText().toString(), password.getText().toString());
                startActivity(new Intent(this, Extends_info.class));
            } else
                Toast.makeText(this, "Password must be like confirm password", Toast.LENGTH_LONG).show();
        } catch (SmackException.NotConnectedException e) {
            Toast.makeText(this, "Sorry, but connection is close", Toast.LENGTH_LONG).show();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Toast.makeText(this, "Sorry, but this username is also occupy", Toast.LENGTH_LONG).show();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Sorry, no response from the server ", Toast.LENGTH_LONG).show();
        }

    }

}
