package com.vitaliypetruk.project;

import java.util.Random;

/**
 * Created by vital on 31.05.16.
 */
public class ChatMessage {
    public String body, sender, receiver, senderName;
    public String Date, Time;
    public String msgid;

    public boolean isMine;// Did I send the message.

    public ChatMessage(String Sender, String Receiver, String messageString,
                       String ID, boolean isMINE) {
        body = messageString;
        isMine = isMINE;
        sender = Sender;
        msgid = ID;
        receiver = Receiver;
        senderName = "Batler";
    }

    public void setMsgID() {

        msgid += "-" + String.format("%02d", new Random().nextInt(100));
        ;
    }
}
