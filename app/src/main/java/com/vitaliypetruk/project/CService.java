package com.vitaliypetruk.project;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;
import java.util.List;

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
    String lastMessageFrom;
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Roster roster =Roster.getInstanceFor(XMPP.connection);

           XMPP.connection.addPacketListener(new PacketListener() {

               @Override
               public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
                   final Presence newPresence = (Presence) stanza;
                   final Presence.Type presenceType = newPresence.getType();
                   final String fromId = newPresence.getFrom();

                   VCard vc = new VCard();
                   try {
                       vc.load(XMPP.connection,fromId);
                   } catch (SmackException.NoResponseException e) {
                       e.printStackTrace();
                   } catch (XMPPException.XMPPErrorException e) {
                       e.printStackTrace();
                   }
                   NotificationCompat.Builder mBuilder =
                           new NotificationCompat.Builder(getBaseContext())
                                   .setSmallIcon(R.drawable.man)

                                   .setContentTitle("You have friend request from")
                                   .setContentText(vc.getFirstName());

                    mBuilder.setAutoCancel(true);
                   Intent resultIntent = new Intent(getBaseContext(), EntryFriend.class);
                   Bundle b = new Bundle();
                   b.putString("userjid", fromId);
                   resultIntent.putExtras(b);
                   TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                   stackBuilder.addParentStack(EntryFriend.class);
                   stackBuilder.addNextIntent(resultIntent);
                   PendingIntent resultPendingIntent =
                           stackBuilder.getPendingIntent(
                                   0,
                                   PendingIntent.FLAG_UPDATE_CURRENT
                           );
                  // mBuilder.addAction(R.drawable.login_button,"Ac",resultPendingIntent);
                   mBuilder.setContentIntent(resultPendingIntent);
                   NotificationManager mNotificationManager =
                           (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                   mNotificationManager.notify(1, mBuilder.build());
               }
           }, new PacketFilter() {

               @Override
               public boolean accept(Stanza stanza) {
                   if (stanza instanceof Presence) {
                       Presence presence = (Presence) stanza;
                       if (presence.getType().equals(Presence.Type.subscribed)
                               || presence.getType().equals(Presence.Type.subscribe)
                               || presence.getType().equals(Presence.Type.unsubscribed)
                               || presence.getType().equals(Presence.Type.unsubscribe)
                               || presence.getType().equals(Presence.Type.available)
                               || presence.getType().equals(Presence.Type.unavailable)) {
                           return true;
                       }
                   }
                   return false;
               }
           });


        XMPP.connection.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {

                Message message = (Message) stanza;
                lastMessageFrom= message.getFrom();


                    final String fromId = stanza.getFrom();
                    VCard vc = new VCard();
                    try {
                        vc.load(XMPP.connection,fromId);
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    }
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getBaseContext())
                                    .setSmallIcon(R.drawable.message)
                                    .setContentTitle(vc.getFirstName())
                                    .setContentText(message.getBody());

                    mBuilder.setAutoCancel(true);
                    Intent resultIntent = new Intent(getBaseContext(), Chats.class);
                    Bundle b = new Bundle();
                    b.putString("userjid",fromId);
                    resultIntent.putExtras(b);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
                    stackBuilder.addParentStack(Chats.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1, mBuilder.build());


            }

        }, new PacketFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                if(stanza instanceof Message){
                    if(((Message) stanza).getBody()!=null){
                        if(!stanza.getFrom().equals(lastMessageFrom))
                        return true;
                    }
                }
                return false;
            }
        });

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
