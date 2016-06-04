package com.vitaliypetruk.project;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vital on 25.05.16.
 */
public class ContactListAdapter extends BaseAdapter {
    private ArrayList<ContactListItem> contactList;
    private LayoutInflater layoutInflater;

    public ContactListAdapter(Context context, ArrayList<ContactListItem> contactList ){
        this.contactList = contactList;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    Bitmap bitmap = null ;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder;
        if(convertView==null){
            convertView= layoutInflater.inflate(R.layout.contact_list_item,null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.contact_item_name);
            holder.status = (TextView)convertView.findViewById(R.id.contact_item_status);
            holder.jid = (TextView)convertView.findViewById(R.id.contact_item_jid);
            holder.avatar =(ImageView)convertView.findViewById(R.id.contact_item_avatar);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        if(contactList.get(position).getAvatar()!=null) {

            bitmap = decodeSampledBitmapFromResource(contactList.get(position).getAvatar(),60,60);
            holder.avatar.setImageBitmap(bitmap);

        }

        holder.name.setText(contactList.get(position).getName());
        holder.status.setText(contactList.get(position).getStatus());
        holder.jid.setText(contactList.get(position).getJid());


        return convertView;
    }
    static  class ViewHolder{
        TextView name;
        TextView status;
        TextView jid;
        ImageView avatar;
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromResource(byte[] data, int   reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //options.inPurgeable = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }
}
