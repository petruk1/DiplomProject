package com.vitaliypetruk.project;

/**
 * Created by vital on 25.05.16.
 */
public class ContactListItem {
    private String name;
    private String status;
    private String jid;
    private byte[] avatar;

    public String getName(){return name;}
    public void setNAme(String name){this.name = name;}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public void setAvatarr(byte[] avatar) {
        this.avatar = avatar;
    }
}
