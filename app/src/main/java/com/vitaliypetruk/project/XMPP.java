package com.vitaliypetruk.project;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.jivesoftware.smack.SASLAuthentication.registerSASLMechanism;

/**
 * Created by vital on 17.05.16.
 */
public class XMPP {
    private static final String TAG_ = "XMPP";
    public static String username;
    public static String password;
    private String host;
    public static XMPPTCPConnection connection;

    public static XMPP instance = null;
    public static boolean instanceCreated = false;

    public XMPP(String host, String username,
                String password) {
        this.host = host;
        this.username = username;
        this.password = password;
        initialiseConnection();
    }

    public static XMPP getInstance(String server, String user, String pass) {
        if (instance == null) {
            instance = new XMPP(server, user, pass);
            instanceCreated = true;
        }
        Log.d(TAG_, "XMPP.getInstance(): done");
        return instance;

    }

    private void initialiseConnection() {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName("vital");
        config.setHost("10.42.0.1");
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());

        Log.d(TAG_, "XMPP.initialiseConnection(): done");
    }

    public void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   connection.connect();
                    connection.setPacketReplyTimeout(1000);
                //    Log.d(TAG_, "XMPP.connect()  " + connection.isConnected());
                } catch (SmackException e) {
                    e.printStackTrace();
                    Log.d(TAG_, "XMPP.connect()  " + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG_, "XMPP.connect()  " + e.getMessage());
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Log.d(TAG_, "XMPP.connect()  " + e.getMessage());
                }
            }
        }).start();
//        Log.d(TAG_, "XMPP.connect() - " + connection.isConnected());

    }
    public static void destroyConnection(){
        connection.disconnect();
       connection=null;

    }

    public static void createUser(String username, String password, Map<String, String> setValues) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {

       // Log.d("CON", connection.isConnected() + "");
        AccountManager accountManager = AccountManager.getInstance(connection);
        accountManager.sensitiveOperationOverInsecureConnection(true);
        accountManager.createAccount(username, password, setValues);

        login(username, password);
    }

    public static void login(String username, String psw) {
        try {
            connection.login(username, psw);
        } catch (XMPPException e) {
            e.printStackTrace();
            Log.d("CService", "XMPP.login(): " + e.getMessage());
        } catch (SmackException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("CService", "XMPP.login(): " + e.getMessage());
        }
        Log.d(TAG_, "XMPP.login(): done");

    }

    public static ArrayList searchUser(String find) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        ArrayList result = new ArrayList();
     //   Log.d("CREATOR", " conne" + connection.isConnected()
      //          + "   auto " + connection.isAuthenticated());
        UserSearchManager usm = new UserSearchManager(connection);
        Form serchForm = usm.getSearchForm("search." + connection.getServiceName());
        Form answerForm = serchForm.createAnswerForm();
        answerForm.setAnswer("Username", true);
        answerForm.setAnswer("search", find);

        ReportedData reportedData = usm.getSearchResults(answerForm, "search." + connection.getServiceName());
        for (ReportedData.Row row : reportedData.getRows()) {
            for (String username : row.getValues("username")) {
                result.add(username);
            }
        }
        return result;
    }

}
