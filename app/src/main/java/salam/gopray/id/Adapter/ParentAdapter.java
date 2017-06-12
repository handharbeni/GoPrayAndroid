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

import salam.gopray.id.Adapter.model.ParentModel;
import salam.gopray.id.R;

/**
 * Created by root on 09/06/17.
 */

public class ParentAdapter extends ArrayAdapter<ParentModel> {
    private int lastPosition = -1;
    private ArrayList<ParentModel> dataSet;
    Context mContext;
    private static class ViewHolder {
        ImageView photo_thumbnail;
        TextView txtName, txtGrade;
    }
    ParentAdapter.ViewHolder viewHolder;
    public ParentAdapter(ArrayList<ParentModel> data, Context context) {
        super(context, R.layout.item_parent, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParentModel dataModel = getItem(position);
        final View result;
        if (convertView == null) {
            viewHolder = new ParentAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_parent, parent, false);
            viewHolder.photo_thumbnail = (ImageView) convertView.findViewById(R.id.photo_thumbnail);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.txtGrade = (TextView) convertView.findViewById(R.id.txtGrade);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ParentAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;
        viewHolder.txtName.setText(dataModel.getNama());
        viewHolder.txtGrade.setText(dataModel.getGrade());
        Glide.with(mContext)
                .load(dataModel.getPhoto())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .into(viewHolder.photo_thumbnail);

        return convertView;
    }

}