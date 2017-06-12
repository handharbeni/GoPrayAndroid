package salam.gopray.id.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import salam.gopray.id.Adapter.model.MessageModel;
import salam.gopray.id.R;

/**
 * Created by root on 26/04/17.
 */

public class MessageAdapter extends ArrayAdapter<MessageModel> {
    private int lastPosition = -1;
    private ArrayList<MessageModel> dataSet;
    Context mContext;
    private static class ViewHolder {

        RelativeLayout itemChatText, itemChatImage;
        /*Line BOTTOM*/
        ImageView photo_thumbnail, txtImage;
        TextView txtMessage, txtTanggal, txtMessageImage, txtTanggalImage;

    }
    ViewHolder viewHolder;
    public MessageAdapter(ArrayList<MessageModel> data, Context context) {
        super(context, R.layout.item_timeline, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageModel dataModel = getItem(position);
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_chat, parent, false);
            viewHolder.photo_thumbnail = (ImageView) convertView.findViewById(R.id.photo_thumbnail);


            /*chat image*/
            viewHolder.itemChatImage= (RelativeLayout) convertView.findViewById(R.id.itemChatImage);
            viewHolder.txtImage = (ImageView) convertView.findViewById(R.id.txtImage);
            viewHolder.txtMessageImage = (TextView) convertView.findViewById(R.id.txtMessageImage);
            viewHolder.txtTanggalImage = (TextView) convertView.findViewById(R.id.txtTanggalImage);
            /*chat image*/

            /*chat text*/
            viewHolder.itemChatText = (RelativeLayout) convertView.findViewById(R.id.itemChatText);
            viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
            viewHolder.txtTanggal = (TextView) convertView.findViewById(R.id.txtTanggal);
            /*chat text*/
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        int type = dataModel.getType();

        viewHolder.itemChatImage.setVisibility(View.GONE);
        viewHolder.itemChatText.setVisibility(View.GONE);
        if (type == 1){
                /*chat text*/
            viewHolder.itemChatText.setVisibility(View.VISIBLE);
            viewHolder.txtMessage.setText(dataModel.getMessage());
            viewHolder.txtTanggal.setText(dataModel.getDate());
        }else{
                /*chat image*/
            viewHolder.itemChatImage.setVisibility(View.VISIBLE);
            viewHolder.txtMessageImage.setText(dataModel.getMessage());
            Glide.with(mContext)
                    .load(dataModel.getPhoto())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .skipMemoryCache(false)
                    .into(viewHolder.txtImage);
            viewHolder.txtTanggalImage.setText(dataModel.getDate());
        }

        Glide.with(mContext)
                .load(dataModel.getProfpict())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .into(viewHolder.photo_thumbnail);

        return convertView;
    }

}