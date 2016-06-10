package com.vitaliypetruk.project;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SelectUserForChat extends ActionBarActivity {
    boolean sett = false;
    boolean done = false;
    ArrayList newContactList = new ArrayList();
    Map<Integer,ContactListItem> map = new HashMap<>();
    ListView contactList ;
    ArrayList<String> forChat = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_for_chat);
        contactList = (ListView)findViewById(R.id.for_chat_list);
        contactList.setAdapter(new ContactListAdapter(this,getContactsList()));
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.GREEN);
                String jid = ((TextView)view.findViewById(R.id.contact_item_jid)).getText().toString();
                if(forChat.contains(jid))
               Toast.makeText( SelectUserForChat.this," This user is added!" ,Toast.LENGTH_LONG).show();
                else{
                    forChat.add(jid);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_for_chat, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_for_chat);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(sett) {
                    if(!done){
                        //contactList = (ListView) mViewPager.findViewById(R.id.Contact_contact_list);
                        for (int i = 0; i<contactList.getAdapter().getCount();i++) {
                            map.put(i,((ContactListItem)contactList.getAdapter().getItem(i)));
                        }
                        done = true;
                    }
                    ArrayList listA = new ArrayList();
                    for(int i = 0; i<map.size();i++){
                        Toast.makeText(getBaseContext(),""+map.get(i).getName(),Toast.LENGTH_SHORT).show();
                        if(map.get(i).getName().toLowerCase().startsWith(newText)){

                            listA.add(map.get(i));
                        }
                    }
                    contactList.setAdapter(new ContactListAdapter(getBaseContext(),listA));
                }
            return true;
            }
        });
        return true;
    }
    private ArrayList<ContactListItem> getContactsList(){
        ArrayList<ContactListItem> contactsList = new ArrayList<ContactListItem>();
        Roster roster = Roster.getInstanceFor(XMPP.connection);
        Presence availabilyty;
        Presence.Mode userMode;

        for(RosterEntry entry:roster.getEntries()) {
            ContactListItem listItem = new ContactListItem();
            if (roster.getGroup("Friends").contains(entry.getUser())) {
                VCard contactData = new VCard();
                try {
                    contactData.load(XMPP.connection, entry.getUser());
                    byte[] avatar = contactData.getAvatar();
                    listItem.setNAme(contactData.getFirstName());
                    if (avatar != null)
                        listItem.setAvatarr(avatar);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                availabilyty = roster.getPresence(entry.getUser());
                userMode = availabilyty.getMode();
                listItem.setStatus(retrieveState_mode(userMode, availabilyty.isAvailable()));
                listItem.setJid(entry.getUser());
                contactsList.add(listItem);
            }
        }
        return contactsList;
    }
    public static String retrieveState_mode(Presence.Mode userMode, boolean isOnline) {
        String userState= "Offline";
        /** 0 for offline, 1 for online, 2 for away,3 for busy*/
        if(userMode == Presence.Mode.dnd) {
            userState = "Busy";
        } else if (userMode == Presence.Mode.away || userMode == Presence.Mode.xa) {
            userState = "Away";
        } else if (isOnline) {
            userState = "online";
        }else userState="offline";
        return userState;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.for_chat_ok) {
            Bundle b = new Bundle();
            b.putSerializable("users", forChat);
            Intent intent = new Intent(this,GroupChats.class);
            intent.putExtras(b);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
