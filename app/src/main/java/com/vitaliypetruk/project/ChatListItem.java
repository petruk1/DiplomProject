package com.vitaliypetruk.project;

/**
 * Created by vital on 01.06.16.
 */
public class ChatListItem {
    private String name;
    private String lastMessage;
    private String dateOfLastMessage;
    private String jid;
    private byte[] avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDateOfLastMessage() {
        return dateOfLastMessage;
    }

    public void setDateOfLastMessage(String dateOfLastMessage) {
        this.dateOfLastMessage = dateOfLastMessage;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
