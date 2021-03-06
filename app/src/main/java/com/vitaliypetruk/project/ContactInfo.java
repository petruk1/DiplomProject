package com.vitaliypetruk.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.FileOutputStream;
import java.util.ArrayList;


public class ContactInfo extends ActionBarActivity {
    ImageView avatarImageView;
    ImageButton chatButton;
    ImageButton removeUserButton;
    ListView contactInfoList;
    FileOutputStream out;
    String selectedContactName;
    String jid;
    VCard vCard = new VCard();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        avatarImageView= (ImageView)findViewById(R.id.contact_info_avatar);
        chatButton=(ImageButton)findViewById(R.id.contact_info_chat);
        removeUserButton=(ImageButton)findViewById(R.id.contact_info_remove);
        contactInfoList = (ListView)findViewById(R.id.userInfo);
       savedInstanceState = getIntent().getExtras();
            jid = savedInstanceState.getString("userjid");

        contactInfoList.setAdapter(new CustomListAdapter(this, getListData()));

    }

    private ArrayList getListData() {
        ArrayList<ContactInfoItem> results = new ArrayList<>();
        try {
            vCard.load(XMPP.connection, jid);
            if (vCard.getAvatar() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(vCard.getAvatar(),0,vCard.getAvatar().length);
                avatarImageView.setImageBitmap(bitmap);
            }

                    ContactInfoItem name = new ContactInfoItem();
            if(vCard.getFirstName()!=null){
                name.setMainText(vCard.getFirstName());
                name.setSubText("Name");
            }else{
                name.setMainText("not Secified");
                name.setSubText("Name");
            }
            results.add(name);

            ContactInfoItem locality = new ContactInfoItem();
            if(vCard.getFirstName()!=null){
                locality.setMainText(vCard.getAddressFieldHome("LOCALITY"));
                locality.setSubText("City");
            }else{
                locality.setMainText("not Secified");
                locality.setSubText("City");
            }
            results.add(locality);

            ContactInfoItem voice = new ContactInfoItem();
            if(vCard.getFirstName()!=null){
                voice.setMainText(vCard.getPhoneWork("VOICE"));
                voice.setSubText("Telephone");
            }else{
                voice.setMainText("not Secified");
                voice.setSubText("Telephone");
            }
            results.add(voice);

            ContactInfoItem work = new ContactInfoItem();
            if(vCard.getFirstName()!=null){
                work.setMainText(vCard.getOrganization());
                work.setSubText("Work");
            }else{
                work.setMainText("not Secified");
                work.setSubText("Work");
            }
            results.add(work);

            ContactInfoItem age = new ContactInfoItem();
            if(vCard.getFirstName()!=null){
                age.setMainText(vCard.getField("AGE"));
                Toast.makeText(this,"_"+vCard.getField("AGE"),Toast.LENGTH_LONG).show();
                age.setSubText("Age");
            }else{
                age.setMainText("not Secified");
                age.setSubText("Age");
            }
            results.add(age);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return results;
    }
    public void loadChat(View view){
        Intent container = new Intent(this,Chats.class);
        Bundle data = new Bundle();
        data.putString("userjid",jid);
        container.putExtras(data);
        startActivity(container);
        finish();
    }
    public void removeContact(View view){
        Roster roster = Roster.getInstanceFor(XMPP.connection);
        try {
            roster.getGroup("Friends").removeEntry(roster.getEntry(jid));
            startActivity(new Intent(this,Home.class));
            finish();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
