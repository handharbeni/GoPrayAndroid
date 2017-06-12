package salam.gopray.id.Adapter.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Collections;
import java.util.List;

import salam.gopray.id.R;

/**
 * Created by root on 09/06/17.
 */

public class HorizontalParentAdapter extends RecyclerView.Adapter<HorizontalParentAdapter.MyViewHolder> {


        List<ParentModel> horizontalList = Collections.emptyList();
        Context context;


    public HorizontalParentAdapter(List<ParentModel> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView photo_thumbnail;
        TextView txtName, txtGrade;
        public MyViewHolder(View view) {
            super(view);
            photo_thumbnail = (ImageView) view.findViewById(R.id.photo_thumbnail);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtGrade = (TextView) view.findViewById(R.id.txtGrade);
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Glide.with(context)
                .load(horizontalList.get(position).getPhoto())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .into(holder.photo_thumbnail);
        holder.txtName.setText(horizontalList.get(position).getNama());
        holder.txtGrade.setText(horizontalList.get(position).getGrade());
    }
    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}