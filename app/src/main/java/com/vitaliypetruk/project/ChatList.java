package com.vitaliypetruk.project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatList extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageButton add;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatList.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatList newInstance(String param1, String param2) {
        ChatList fragment = new ChatList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ChatList() {
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

    View view;
    ListView chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //if (XMPP.connection != null) {
            view = inflater.inflate(R.layout.fragment_chat_list, container, false);
            add = (ImageButton)view.findViewById(R.id.chat_list_addChat);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),SelectUserForChat.class));
                    Toast.makeText(getActivity(),"YYep",Toast.LENGTH_LONG).show();
                }
            });

         /*   chatList = (ListView) view.findViewById(R.id.chats_list);
            chatList.setAdapter(new ChatListAdapter(getActivity(), getChatsList()));*/
      //  }
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
            //    throw new ClassCastException(activity.toString()
            //             + " must implement OnFragmentInteractionListener");
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

    Connection connection;

    private ArrayList<ChatListItem> getChatsList() {
        final ArrayList<ChatListItem> chatListItem = new ArrayList<ChatListItem>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatListItem listItem = new ChatListItem();
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager
                            .getConnection("jdbc:postgresql://192.168.178.242:5432/DiplomDB",
                                    "postgres", "vital");
                    System.out.println("Ok");
                    Statement stmt = connection.createStatement();
                    String sql = "select  * from ofmessagearchive where (sentdate)=(select max(sentdate) from ofmessagearchive where " +
                            "((fromjid ='testuser1@vital' and tojid = 'testuser2@vital') " +
                            " or (fromjid ='testuser2@vital' and tojid = 'testuser1@vital')))";
                    ResultSet rsto = stmt.executeQuery(sql);
                    // rsto.next();
                    VCard vCard = new VCard();
                    vCard.load(XMPP.connection, "testuser2@vital");
                    listItem.setJid("testuser2@vital");
                    listItem.setName(vCard.getFirstName());
                    listItem.setAvatar(vCard.getAvatar());
                    listItem.setLastMessage(rsto.getString("body"));
                    Log.d("LIST", rsto.getString("body"));
                    Date d = new Date(Long.parseLong(rsto.getString("sentdate")));
                    listItem.setDateOfLastMessage(" 45" + d.getDay() + " days " +
                            d.getHours() + " h " +
                            d.getMinutes() + " min");

                    chatListItem.add(listItem);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("LIST", "------ exception message ---- " + e.getMessage());
                }
            }
        }).start();

        return chatListItem;
    }


}
