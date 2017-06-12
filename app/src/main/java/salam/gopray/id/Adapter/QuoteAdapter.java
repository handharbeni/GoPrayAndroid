package salam.gopray.id.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import salam.gopray.id.Adapter.model.QuoteModel;
import salam.gopray.id.R;

/**
 * Created by root on 26/04/17.
 */

public class QuoteAdapter extends ArrayAdapter<QuoteModel> implements View.OnClickListener{
    private int lastPosition = -1;
    private ArrayList<QuoteModel> dataSet;
    Context mContext;
    private static class ViewHolder {
        ImageView txtImage;
        TextView txtTanggal;

    }
    ViewHolder viewHolder;
    public QuoteAdapter(ArrayList<QuoteModel> data, Context context) {
        super(context, R.layout.item_timeline, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        QuoteModel dataModel=(QuoteModel)object;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QuoteModel dataModel = getItem(position);
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_quote, parent, false);
            viewHolder.txtImage = (ImageView) convertView.findViewById(R.id.txtImage);
            viewHolder.txtTanggal = (TextView) convertView.findViewById(R.id.txtTanggalImage);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        Glide.with(mContext)
                .load(dataModel.getPath_meme())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .into(viewHolder.txtImage);
        viewHolder.txtTanggal.setText(dataModel.getTanggal());

        return convertView;
    }

}
