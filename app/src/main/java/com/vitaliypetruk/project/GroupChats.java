package com.vitaliypetruk.project;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GroupChats extends ActionBarActivity {
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
    MultiUserChatManager mucManager;
    MultiUserChat muc;
    Map<String,byte[]> users = new HashMap<>();
    ArrayList<String> jids = new ArrayList<>();
    VCard card;
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
        try {
            ownCard.load(XMPP.connection);
            ownAvarat = ownCard.getAvatar();
          //  friendCard.load(XMPP.connection, userjid);
            //friendAvatar = friendCard.getAvatar();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        savedInstanceState = getIntent().getExtras();



        jids.addAll((Collection<? extends String>) savedInstanceState.getSerializable("users"));
        for(int i = 0; i<jids.size();i++){

            card = new VCard();
            try {
                card.load(XMPP.connection,(String)jids.get(i));
                users.put(jids.get(i),card.getAvatar());
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        try {
            mucManager = MultiUserChatManager.getInstanceFor(XMPP.connection);
            muc = mucManager.getMultiUserChat(random.nextInt()+"@conference.vital");
            muc.create(XMPP.connection.getUser() + "1");
            muc.sendConfigurationForm(new Form(DataForm.Type.submit));
            for (int i = 0; i<jids.size();i++)
                muc.invite(jids.get(i),"");
            muc.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    if (message.getBody() != null) {
                        String  messageBody = message.getBody();
                        System.out.println(messageBody);
                        final ChatMessage cm = new ChatMessage("","",messageBody,""
                                +random.nextInt(),false);
                        cm.setMsgID();
                        for(int i = 0; i<users.size();i++)
                            if(users.containsKey(message.getFrom()))
                                cm.avatar = users.get(message.getFrom());
                            else cm.avatar = null;
                        cm.body = messageBody;
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
            Toast.makeText(this,"ok",Toast.LENGTH_LONG).show();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        System.out.println("***********************************************************************************************************");
        System.out.println(users);
        System.out.println("*****************************************************************************************************************");


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
                muc.sendMessage(message);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }


        }

    }
    private static void sendMessageToMUC(String roomJID,String messagText, String []usersJID ) throws XMPPException.XMPPErrorException, SmackException {




    }
    private void sendMessageToChat(String user, String message) throws SmackException.NotConnectedException {
        ChatManager chatManager = ChatManager.getInstanceFor(XMPP.connection);
        Chat chat = chatManager.createChat(user);
        chat.sendMessage(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_croup_chats, menu);
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
}
