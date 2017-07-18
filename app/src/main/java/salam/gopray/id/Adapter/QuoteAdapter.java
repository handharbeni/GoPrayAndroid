package salam.gopray.id.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.golovin.fluentstackbar.FluentSnackbar;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import salam.gopray.id.Adapter.model.QuoteModel;
import salam.gopray.id.R;
import salam.gopray.id.database.helper.QuoteHelper;
import salam.gopray.id.util.ShareListener;

/**
 * Created by root on 26/04/17.
 */

public class QuoteAdapter extends ArrayAdapter<QuoteModel> implements View.OnClickListener{
    public String STAT = "stat", KEY = "key", NAMA="nama", EMAIL= "email", PICTURE = "gambar";
    private int lastPosition = -1;
    private ArrayList<QuoteModel> dataSet;
    private ShareListener shareListener;

    String endUriDelete;

    Context mContext;
    OkHttpClient client;
    EncryptedPreferences encryptedPreferences;
    QuoteHelper qHelper;
    private FluentSnackbar mFluentSnackbar;

    private static class ViewHolder {
        ImageView txtImage, imgMenu;
        TextView txtTanggal;

    }
    ViewHolder viewHolder;
    public QuoteAdapter(ArrayList<QuoteModel> data, Context context, ShareListener shareListener) {
        super(context, R.layout.item_timeline, data);
        this.dataSet = data;
        this.mContext=context;
        encryptedPreferences = new EncryptedPreferences.Builder(this.mContext).withEncryptionPassword(this.mContext.getString(R.string.KeyPassword)).build();
        client = new OkHttpClient();
        qHelper = new QuoteHelper(this.mContext);
        endUriDelete = this.mContext.getResources().getString(R.string.server)+"/"+this.mContext.getResources().getString(R.string.vServer)+"/"+"users/self/deletememe";
        this.shareListener = shareListener;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        QuoteModel dataModel=(QuoteModel)object;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final QuoteModel dataModel = getItem(position);
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_quote, parent, false);
            viewHolder.txtImage = (ImageView) convertView.findViewById(R.id.txtImage);
            viewHolder.txtTanggal = (TextView) convertView.findViewById(R.id.txtTanggalImage);
            viewHolder.imgMenu = (ImageView) convertView.findViewById(R.id.imgMenu);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                shareListener.openContext(v);
/*                PopupMenu popup = new PopupMenu(mContext, viewHolder.imgMenu, Gravity.RIGHT);
                popup.getMenuInflater()
                        .inflate(R.menu.menu_quote, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getTitle().toString()){
                            case "Share WhatsApp":
                                shareListener.shareWa(dataModel.getId());
                                break;
                            case "Share Facebook":
                                shareListener.shareFb(dataModel.getId());
                                break;
                            case "Delete":
                                shareListener.delete(dataModel.getId());
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popup.show();*/
            }
        });
        Glide.with(mContext)
                .load(dataModel.getPath_meme())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .skipMemoryCache(false)
                .into(viewHolder.txtImage);
        viewHolder.txtTanggal.setText(dataModel.getTanggal());

        return convertView;
    }
}
