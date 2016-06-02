package com.vitaliypetruk.project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by vital on 31.05.16.
 */
public class ChatAdapter extends BaseAdapter{
    private static LayoutInflater inflater = null;
    ArrayList<ChatMessage> chatMessageList;

    public ChatAdapter(Activity activity, ArrayList<ChatMessage> list) {
        chatMessageList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        View vi = convertView;
            if(!message.isMine){
                vi = inflater.inflate(R.layout.chatbubble, null);
                TextView msg = (TextView) vi.findViewById(R.id.message_text);
                ImageView avatar =(ImageView)vi.findViewById(R.id.avatar);
                if(message.avatar!=null){
                    Bitmap bitmap = decodeSampledBitmapFromResource(message.avatar, 50, 60);
                    avatar.setDrawingCacheQuality(ImageView.DRAWING_CACHE_QUALITY_LOW);
                    avatar.setImageBitmap(bitmap);
                }
                msg.setText(message.body);
                RelativeLayout layout = (RelativeLayout) vi
                        .findViewById(R.id.bubble_layout);
                LinearLayout parent_layout = (LinearLayout) vi
                        .findViewById(R.id.bubble_layout_parent);
                TextView date = (TextView)vi.findViewById(R.id.left_time);
                date.setText(message.Time);
                    layout.setBackgroundResource(R.drawable.bubble1);
                    parent_layout.setGravity(Gravity.LEFT);
                msg.setTextColor(Color.BLACK);
            }
            else
            {
            vi = inflater.inflate(R.layout.chatbubble2, null);
            TextView msg = (TextView) vi.findViewById(R.id.message_text);
            ImageView avatar =(ImageView)vi.findViewById(R.id.avatar);
            if(message.avatar!=null){
                Bitmap bitmap = decodeSampledBitmapFromResource(message.avatar,50,60);
                avatar.setDrawingCacheQuality(ImageView.DRAWING_CACHE_QUALITY_LOW);
                avatar.setImageBitmap(bitmap);
            }
            msg.setText(message.body);
            RelativeLayout layout = (RelativeLayout) vi
                    .findViewById(R.id.bubble_layout);
            LinearLayout parent_layout = (LinearLayout) vi
                    .findViewById(R.id.bubble_layout_parent);
            TextView date = (TextView)vi.findViewById(R.id.left_time);
            date.setText(message.Time);
                layout.setBackgroundResource(R.drawable.bubble2);
                parent_layout.setGravity(Gravity.RIGHT);
                msg.setTextColor(Color.BLACK);
            }
        return vi;

    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
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
