package com.vitaliypetruk.project;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayOutputStream;


public class Extends_info extends ActionBarActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extends_info);
        first_last_names = (EditText) findViewById(R.id.extend_info_names);
        age = (EditText) findViewById(R.id.extend_info_age);
        city = (EditText) findViewById(R.id.extend_info_city);
        work = (EditText) findViewById(R.id.extend_info_work);
        telephone = (EditText) findViewById(R.id.extend_info_telephon);
        avatar = (ImageView) findViewById(R.id.extend_info_avatar);
        save_finish = (Button) findViewById(R.id.extend_info_save);
        loadAvatar = (Button) findViewById(R.id.extend_info_loadAvatar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_extends_info, menu);
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

    /*initialise file chooser. Needed to getting image*/
    public void loadAvatar(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(
                Intent.createChooser(intent, "Complete action using"),
                1);
    }

    @Override/*Return the chousen image from SD card */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        mImageCaptureUri = data.getData();
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
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void finishModifyInfo(View view) {
        this.saveData();
        startActivity(new Intent(this, Home.class));
    }

    private void saveData() {
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
            Toast.makeText(this, personalInfo.getFirstName(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, "All done", Toast.LENGTH_LONG).show();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d("Extend", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
