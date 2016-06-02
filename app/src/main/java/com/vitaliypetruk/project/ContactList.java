package com.vitaliypetruk.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactList extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static ListView contacts;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactList.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactList newInstance(String param1, String param2) {
        ContactList fragment = new ContactList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    View view ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      if(XMPP.connection!=null) {
          view = inflater.inflate(R.layout.fragment_contact_list, container, false);
          contacts = (ListView) view.findViewById(R.id.Contact_contact_list);

          contacts.setPadding(5, 5, 5, 5);
          contacts.setAdapter(new ContactListAdapter(getActivity(), getContactsList()));
          contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  view.setBackgroundColor(189);
                  Intent data = new Intent(getActivity(),ContactInfo.class);
                String  jid=((TextView) view.findViewById(R.id.contact_item_jid)).getText().toString();
                  Bundle b = new Bundle();
                  b.putString("userjid",jid);
                  data.putExtras(b);
                  startActivity(data);
              }
          });
      }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
           // throw new ClassCastException(activity.toString()
          //          + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    private ArrayList<ContactListItem> getContactsList(){
        ArrayList<ContactListItem> contactsList = new ArrayList<ContactListItem>();

        Roster roster = Roster.getInstanceFor(XMPP.connection);
        Presence availabilyty;
        Presence.Mode userMode;

        for(RosterEntry entry:roster.getEntries()){
            ContactListItem listItem = new ContactListItem();
            VCard contactData = new VCard();
            try{

                contactData.load(XMPP.connection, entry.getUser());
                byte [] avatar = contactData.getAvatar();
                listItem.setNAme(contactData.getFirstName());
                if(avatar!=null)
                listItem.setAvatarr(avatar);

            }catch (Exception e){
                e.printStackTrace();
               // Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

            availabilyty = roster.getPresence(entry.getUser());
            userMode =availabilyty.getMode();
            listItem.setStatus(retrieveState_mode(userMode,availabilyty.isAvailable()));
            listItem.setJid(entry.getUser());
            contactsList.add(listItem);
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


}
