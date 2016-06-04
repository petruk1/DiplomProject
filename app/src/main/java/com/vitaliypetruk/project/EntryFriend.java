package com.vitaliypetruk.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.FileOutputStream;
import java.util.ArrayList;


public class EntryFriend extends ActionBarActivity {
    String jid;
    VCard vCard = new VCard();
    ImageView avatarImageView;
    ImageButton accept;
    ImageButton denay;
    ListView contactInfoList;
    FileOutputStream out;
    String selectedContactName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_friend);
        avatarImageView= (ImageView)findViewById(R.id.newFriend_info_avatar);
        accept=(ImageButton)findViewById(R.id.newFriend_info_accept);
        denay=(ImageButton)findViewById(R.id.newFriend_denay);
        contactInfoList = (ListView)findViewById(R.id.newFriend_userInfo);
        savedInstanceState = getIntent().getExtras();
        jid = savedInstanceState.getString("userjid");

        contactInfoList.setAdapter(new CustomListAdapter(this, getListData()));
    }

    private ArrayList getListData() {
        ArrayList<ContactInfoItem> results = new ArrayList<>();
        try {
            vCard.load(XMPP.connection,jid);
            Bitmap bitmap = BitmapFactory.decodeByteArray(vCard.getAvatar(), 0, vCard.getAvatar().length);
            avatarImageView.setImageBitmap(bitmap);
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
    public void acceptNewFriend(View view){
        Roster roster = Roster.getInstanceFor(XMPP.connection);
        try {
            roster.createEntry(jid,vCard.getFirstName(),new String[]{"Friends"});
            startActivity(new Intent(this,Home.class));
            finish();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
    public void denay(View view ){
        startActivity(new Intent(this,Home.class));
        finish();
    }


}
