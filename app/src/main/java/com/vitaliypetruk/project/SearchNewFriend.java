package com.vitaliypetruk.project;

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

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchNewFriend extends ActionBarActivity {
ListView peopleList;
    String jid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new_friend);
        peopleList = (ListView)findViewById(R.id.search_new_friend);
        peopleList.setAdapter(new ContactListAdapter(this,loadPeople()));
        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                jid =((TextView)view.findViewById(R.id.contact_item_jid)).getText().toString();
                Toast.makeText(SearchNewFriend.this,jid,Toast.LENGTH_LONG).show();
                 Roster roster = Roster.getInstanceFor(XMPP.connection);
                try {
                    VCard vc = new VCard();
                    vc.load(XMPP.connection,jid);
                    roster.createEntry(jid,vCard.getFirstName(),new String[]{"Friends"});
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

        Presence presence = new Presence(Presence.Type.subscribe);
        presence.setTo(jid);
                try {
                    XMPP.connection.sendPacket(presence);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    VCard vCard = new VCard();
    Map<Integer,ContactListItem> map = new HashMap<>();
    private ArrayList<ContactListItem> loadPeople(){
        ArrayList<ContactListItem> people = new ArrayList<>();
        UserSearchManager usm = new UserSearchManager(XMPP.connection);
        Form serchForm = null;
        Roster roster = Roster.getInstanceFor(XMPP.connection);
        Presence availabilyty;
        Presence.Mode userMode;
        try {
            serchForm = usm.getSearchForm("search." + XMPP.connection.getServiceName());
            Form answerForm = serchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", "*");
            ContactListItem man ;
            ReportedData reportedData = usm.getSearchResults(answerForm, "search." + XMPP.connection.getServiceName());

            for (ReportedData.Row row : reportedData.getRows()) {
                for (String username : row.getValues("username")) {
                    vCard.load(XMPP.connection, username + "@vital");
                    man = new ContactListItem();
                    man.setJid(username + "@vital");

                    if(vCard.getFirstName()!=null)
                        man.setNAme(vCard.getFirstName());
                    else  man.setNAme("");
                    man.setAvatarr(vCard.getAvatar());
                    availabilyty = roster.getPresence(username + "@vital");
                    userMode =availabilyty.getMode();
                    man.setStatus(retrieveState_mode(userMode,availabilyty.isAvailable()));
                    people.add(man);
                }
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return people;
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
    private static ArrayList searchUser(String find) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {

        ArrayList result = new ArrayList();

        UserSearchManager usm = new UserSearchManager(XMPP.connection);
        Form serchForm = usm.getSearchForm("search." + XMPP.connection.getServiceName());
        Form answerForm = serchForm.createAnswerForm();
        answerForm.setAnswer("Username", true);
        answerForm.setAnswer("search", find);

        ReportedData reportedData = usm.getSearchResults(answerForm, "search." + XMPP.connection.getServiceName());
        // System.out.println(reportedData.getRows());
        for (ReportedData.Row row : reportedData.getRows()) {
            for (String username : row.getValues("username")) {
                result.add(username);
            }
        }
        return result;
    }
    boolean done = false;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(!done){
                 //   contactList = (ListView) mViewPager.findViewById(R.id.Contact_contact_list);
                    for (int i = 0; i<peopleList.getAdapter().getCount();i++) {
                        map.put(i,((ContactListItem)peopleList.getAdapter().getItem(i)));
                    }
                    done = true;
                }
                System.out.println(map.size());
                ArrayList listA = new ArrayList();
                for(int i = 0; i<map.size();i++){
                  //  Toast.makeText(getBaseContext(), "" + map.get(i).getName(), Toast.LENGTH_SHORT).show();
                    if(map.get(i).getName().toLowerCase().startsWith(newText)){

                        listA.add(map.get(i));
                    }
                }
                peopleList.setAdapter(new ContactListAdapter(getBaseContext(),listA));

                return false;
            }
        });
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
