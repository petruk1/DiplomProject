package com.vitaliypetruk.project;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExtendsInfoSettings.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExtendsInfoSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExtendsInfoSettings extends android.support.v4.app.Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText first_last_names;
    private EditText age;
    private EditText city;
    private EditText work;
    private EditText telephone;
    private ImageView avatar;
    private Button save_finish;
    private Button loadAvatar;

    private Uri mImageCaptureUri;
    private String path;
    private Bitmap image;

    VCard personalInfo;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExtendsInfoSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static ExtendsInfoSettings newInstance(String param1, String param2) {
        ExtendsInfoSettings fragment = new ExtendsInfoSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ExtendsInfoSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_extends_info_settings, container, false);
        first_last_names = (EditText) view.findViewById(R.id.settings_extend_info_names);
        age = (EditText) view.findViewById(R.id.settings_extend_info_age);
        city = (EditText)  view.findViewById(R.id.settings_extend_info_citsettingsy);
        work = (EditText)  view.findViewById(R.id.settings_extend_info_work);
        telephone = (EditText)  view.findViewById(R.id.settings_extend_info_telephon);
        avatar = (ImageView)  view.findViewById(R.id.settings_extend_info_avatar);
        save_finish = (Button)  view.findViewById(R.id.settings_extend_info_save);
        loadAvatar = (Button)  view.findViewById(R.id.settings_extend_info_loadAvatar);
            /*Loacal connect. Need remove after compile*/
          //  XMPP.getInstance("192.168.178.242","","").connect();
           // XMPP.login("testuser1", "testuser1");

        loadPersonalData();

        loadAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        1);
            }
        });

        save_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    VCard personalInfo = new VCard();
                    personalInfo.load(XMPP.connection);
                    personalInfo.setFirstName(first_last_names.getText().toString());
                    personalInfo.setOrganization(work.getText().toString());
                    personalInfo.setPhoneWork("VOICE", telephone.getText().toString());
                    personalInfo.setAddressFieldHome("LOCALITY", city.getText().toString());
                    personalInfo.setField("AGE", age.getText().toString());
                    personalInfo.setAvatar(getBytesFromBitmap(image));
                    personalInfo.save(XMPP.connection);
                    Toast.makeText(getActivity(),"All data are saved",Toast.LENGTH_LONG).show();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                    Log.d("Extend", e.getMessage());

                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();

                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public void loadAvatar(View view) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        mImageCaptureUri = data.getData();
        mImageCaptureUri.getPath();
        Log.d("Extend_info", getRealPathFromURI(mImageCaptureUri));
        path = getRealPathFromURI(mImageCaptureUri);
        image = BitmapFactory.decodeFile(path);
        avatar.setImageBitmap(image);
    }
    /*Use for getting byte array from bitmap. Needed to transfer an image to VCard*/
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 70, stream);
        return stream.toByteArray();
    }

    /*Use for getting normal avatar's path */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader( getActivity().getBaseContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
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
         //   throw new ClassCastException(activity.toString()
           //         + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void loadPersonalData(){
        personalInfo = new VCard();
        try {
            personalInfo.load(XMPP.connection);
            if(personalInfo.getFirstName()!=null)
            first_last_names.setText(personalInfo.getFirstName());
            else first_last_names.setText("No name");
            if((personalInfo.getOrganization()!=null)&&!(personalInfo.getOrganization().isEmpty()))
            work.setText(personalInfo.getOrganization());
            if((personalInfo.getPhoneWork("VOICE")!=null)&&!(personalInfo.getPhoneWork("VOICE").isEmpty()))
            telephone.setText(personalInfo.getPhoneWork("VOICE"));
            if((personalInfo.getAddressFieldHome("LOCALITY")!=null)&&!(personalInfo.getAddressFieldHome("LOCALITY").isEmpty()))
            city.setText(personalInfo.getAddressFieldHome("LOCALITY"));
            if(personalInfo.getField("AGE")!=null)
            age.setText(personalInfo.getField("AGE"));
            if(personalInfo.getAvatar()!=null) {
                image = BitmapFactory.decodeByteArray(personalInfo.getAvatar(), 0, personalInfo.getAvatar().length);

                avatar.setMaxHeight(60);
                avatar.setMaxWidth(60);
                avatar.setImageBitmap(image);
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

}
