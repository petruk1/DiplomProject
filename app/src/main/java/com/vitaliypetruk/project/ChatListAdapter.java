package com.vitaliypetruk.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vital on 01.06.16.
 */
public class ChatListAdapter extends BaseAdapter {
    private ArrayList<ChatListItem> listItems;
    private LayoutInflater layoutInflater;

    public ChatListAdapter(Context context, ArrayList<ChatListItem> listItems) {
        this.listItems = listItems;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    Bitmap bitmap = null;
    byte[] av;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.chat_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.chat_list_item_name);
            holder.date = (TextView) convertView.findViewById(R.id.chat_list_item_date);
            holder.lastMessage = (TextView) convertView.findViewById(R.id.chat_list_item_message);
            holder.avatar = (ImageView) convertView.findViewById(R.id.chat_list_item_avatar);
            holder.jid = (TextView) convertView.findViewById(R.id.chat_list_item_jid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(listItems.get(position).getName());
        holder.lastMessage.setText(listItems.get(position).getLastMessage());
        holder.date.setText(listItems.get(position).getDateOfLastMessage());
        holder.jid.setText(listItems.get(position).getJid());
        // holder.jid.setVisibility();
        av = listItems.get(position).getAvatar();
        if (av != null) {
            bitmap = decodeSampledBitmapFromResource(av, 60, 60);
            holder.avatar.setImageBitmap(bitmap);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView date;
        TextView lastMessage;
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

    public static Bitmap decodeSampledBitmapFromResource(byte[] data, int reqWidth, int reqHeight) {

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
