package com.vitaliypetruk.project;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
        Bitmap bitmap;
        if(contactList.get(position).getAvatar()!=null) {
            bitmap = BitmapFactory.decodeByteArray(contactList.get(position).getAvatar(), 0, contactList.get(position).getAvatar().length);
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
}
