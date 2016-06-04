package com.vitaliypetruk.project;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by vital on 31.05.16.
 */
public class Chats extends ActionBarActivity {
    private EditText messageEdit;
    //private String user1 = "khushi", user2 = "khushi1";
    private Random random;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    private ListView messageList;
    private ImageButton sendButton;
    private VCard ownCard = new VCard();
    private VCard friendCard = new VCard();
    private String userjid;
    byte[] ownAvarat = null;
    byte[] friendAvatar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        messageEdit = (EditText) findViewById(R.id.messageEditText);
        messageList = (ListView) findViewById(R.id.msgListView);
        sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        messageList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messageList.setStackFromBottom(true);
        chatlist = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(this, chatlist);
        messageList.setAdapter(chatAdapter);
        random = new Random();
        savedInstanceState = getIntent().getExtras();
        userjid = savedInstanceState.getString("userjid");
        try {
            ownCard.load(XMPP.connection);
            ownAvarat = ownCard.getAvatar();
            friendCard.load(XMPP.connection, userjid);
            friendAvatar = friendCard.getAvatar();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        ChatManager.getInstanceFor(XMPP.connection).addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        if (message.getBody() != null) {
                            String messageBody = message.getBody();
                            System.out.println(messageBody);

                            final ChatMessage cm = new ChatMessage("", "", messageBody, "" + random.nextInt(1000), false);
                            cm.setMsgID();
                            cm.body = messageBody;
                            cm.avatar = friendAvatar;
                            cm.Time = CommonMethods.getCurrentTime();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatAdapter.add(cm);
                                    chatAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    public void sendTextMessage(View v) {

        String message = messageEdit.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage("", "",
                    message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = CommonMethods.getCurrentDate();
            chatMessage.avatar = ownAvarat;
            chatMessage.Time = CommonMethods.getCurrentTime();
            messageEdit.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
            try {
                sendMessageToChat(userjid, message);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }


        }

    }

    private void sendMessageToChat(String user, String message) throws SmackException.NotConnectedException {
        ChatManager chatManager = ChatManager.getInstanceFor(XMPP.connection);
        Chat chat = chatManager.createChat(user);
        chat.sendMessage(message);


//                    @Override
//                    public void processMessage(org.jivesoftware.smack.Chat chat, Message message) {
//
//                        ChatItem item = new ChatItem();
//                        item.setMessageText(body);
//                        item.setUsernameTitle(from);
//
//                        item.setAvatar(vCard.getAvatar());
//
//                        adapter.add(item);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    vCard.load(XmppManager.connection,username);
//                                } catch (XMPPException e) {
//                                    e.printStackTrace();
//                                }
//                                chatList.setAdapter(new ChatAdapter(Chat.this, adapter));
//                                scroll();
//                            }
//                        });
//                    }
//                };
//
//                chat = chatManager.createChat("testuser2@vital", messageListener);
//
//                while (true) {
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

    }
   /* private static String recieveMessageFromChat(){


        return messageBody;
    }*/


}
