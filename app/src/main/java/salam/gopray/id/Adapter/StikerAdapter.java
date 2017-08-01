package salam.gopray.id.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.pddstudio.preferences.encrypted.EncryptedPreferences;
import com.squareup.picasso.Picasso;

import java.util.List;

import salam.gopray.id.Adapter.model.StikerModel;
import salam.gopray.id.R;
import salam.gopray.id.util.FreeTextInterface;

/**
 * Created by root on 26/07/17.
 */

public class StikerAdapter extends RecyclerView.Adapter<StikerAdapter.MyViewHolder> {
    private String TAG = "MenuAdapter";
    private List<StikerModel> menuList;
    private Context mContext;
    private EncryptedPreferences encryptedPreferences;
    private FreeTextInterface freeTextInterface;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView card_view;
        public ImageView imageStiker;

        public MyViewHolder(View view) {
            super(view);
            card_view = (CardView) view.findViewById(R.id.card_view);
            imageStiker = (ImageView) view.findViewById(R.id.stiker);
        }
    }

    public StikerAdapter(Context mContext, List<StikerModel> menuList, FreeTextInterface freeTextInterface) {
        this.mContext = mContext;
        this.menuList = menuList;
        encryptedPreferences = new EncryptedPreferences.Builder(mContext).withEncryptionPassword(mContext.getString(R.string.KeyPassword)).build();
        this.freeTextInterface = freeTextInterface;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final StikerModel m = menuList.get(position);
        Picasso.with(mContext).load(m.getStiker()).placeholder(R.drawable.logo).into(holder.imageStiker);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encryptedPreferences.edit().putString("STIKERSELECTED", m.getStiker()).apply();
                freeTextInterface.changeStiker();
            }
        });
        holder.imageStiker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encryptedPreferences.edit().putString("STIKERSELECTED", m.getStiker()).apply();
                freeTextInterface.changeStiker();
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content_stiker, parent, false);
        return new MyViewHolder(v);
    }
}