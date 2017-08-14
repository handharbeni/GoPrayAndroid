package salam.gopray.id.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import salam.gopray.id.Adapter.model.TimelineModel;
import salam.gopray.id.R;

/**
 * Created by root on 15/05/17.
 */

public class TimelineAdapter extends ArrayAdapter<TimelineModel> implements View.OnClickListener{
    private int lastPosition = -1;
    private ArrayList<TimelineModel> dataSet;
    Context mContext;
    private static class ViewHolder {
        /*Line BOTTOM*/
        ImageView lineBottom;
        /*Sholat*/
        RelativeLayout itemSholat;
        TextView txtSholat;
        TextView txtLabelBersama;
        TextView txtBersama;
        TextView txtLabelTempat;
        TextView txtTempat;
        TextView txtTanggalSholat;
        /*Sholat*/
        /*Mengaji*/
        RelativeLayout itemMengaji;
        TextView txtLabelMengaji;
        TextView txtMengaji;
        TextView txtLabelTempatMengaji;
        TextView txtTempatMengaji;
        TextView txtTanggalMengaji;
        /*Mengaji*/
        /*Sedekah*/
        RelativeLayout itemSedekah;
        TextView txtLabelSedekah;
        TextView txtLabelTempatSedekah;
        TextView txtTempatSedekah;
        TextView txtTanggalSedekah;
        /*Sedekah*/
        /*Berdoa*/
        RelativeLayout itemBerdoa;
        TextView txtDoa;
        TextView txtTanggalDoa;
        /*Berdoa*/
        /*FreeText*/
        RelativeLayout itemFreeText;
        TextView txtFreeText;
        TextView txtTanggalFreeText;
        /*FreeText*/
        /*Stiker*/
        RelativeLayout itemStiker;
        ImageView txtStiker;
        TextView txtFreeTextStiker;
        TextView txtTanggalStiker;
        /*Stiker*/
        ImageView icon;
        /*Puasa*/
        RelativeLayout itemPuasa;
        TextView txtPuasa;
        TextView txtTanggalPuasa;
    }
    TimelineAdapter.ViewHolder viewHolder;
    public TimelineAdapter(ArrayList<TimelineModel> data, Context context) {
        super(context, R.layout.item_timeline, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        TimelineModel dataModel=(TimelineModel)object;
    }
    public static SpannableStringBuilder setToBold(String text, String textToBold){

        SpannableStringBuilder builder=new SpannableStringBuilder();

        if(textToBold.length() > 0 && !textToBold.trim().equals("")){

            //for counting start/end indexes
            String testText = text.toLowerCase(Locale.US);
            String testTextToBold = textToBold.toLowerCase(Locale.US);
            int startingIndex = testText.indexOf(testTextToBold);
            int endingIndex = startingIndex + testTextToBold.length();
            //for counting start/end indexes

            if(startingIndex < 0 || endingIndex <0){
                return builder.append(text);
            }
            else if(startingIndex >= 0 && endingIndex >=0){

                builder.append(text);
                builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
                builder.setSpan(new ForegroundColorSpan(Color.BLACK), startingIndex, endingIndex, 0);
            }
        }else{
            return builder.append(text);
        }

        return builder;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimelineModel dataModel = getItem(position);
        final View result;
        if (convertView == null) {
            viewHolder = new TimelineAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_timeline, parent, false);
            viewHolder.lineBottom = (ImageView) convertView.findViewById(R.id.lineBottom);
            viewHolder.itemSholat = (RelativeLayout) convertView.findViewById(R.id.itemSholat);
            viewHolder.itemMengaji = (RelativeLayout) convertView.findViewById(R.id.itemMengaji);
            viewHolder.itemSedekah = (RelativeLayout) convertView.findViewById(R.id.itemSedekah);
            viewHolder.itemBerdoa = (RelativeLayout) convertView.findViewById(R.id.itemBerdoa);
            viewHolder.itemFreeText = (RelativeLayout) convertView.findViewById(R.id.itemFreeText);
            viewHolder.itemStiker = (RelativeLayout) convertView.findViewById(R.id.itemStiker);
            viewHolder.itemPuasa = (RelativeLayout) convertView.findViewById(R.id.itemPuasa);

            viewHolder.txtPuasa = (TextView) convertView.findViewById(R.id.txtPuasa);
            viewHolder.txtTanggalPuasa = (TextView) convertView.findViewById(R.id.txtTanggalPuasa);
            viewHolder.txtSholat = (TextView) convertView.findViewById(R.id.txtSholat);
            viewHolder.txtLabelBersama = (TextView) convertView.findViewById(R.id.txtLabelBersama);
            viewHolder.txtBersama = (TextView) convertView.findViewById(R.id.txtBersama);
            viewHolder.txtLabelTempat = (TextView) convertView.findViewById(R.id.txtLabelTempat);
            viewHolder.txtTempat = (TextView) convertView.findViewById(R.id.txtTempat);
            viewHolder.txtTanggalSholat = (TextView) convertView.findViewById(R.id.txtTanggalSholat);
            viewHolder.txtLabelMengaji = (TextView) convertView.findViewById(R.id.txtLabelMengaji);
            viewHolder.txtMengaji = (TextView) convertView.findViewById(R.id.txtMengaji);
            viewHolder.txtTanggalMengaji = (TextView) convertView.findViewById(R.id.txtTanggalMengaji);
            viewHolder.txtLabelTempatMengaji = (TextView) convertView.findViewById(R.id.txtLabelTempatMengaji);
            viewHolder.txtTempatMengaji = (TextView) convertView.findViewById(R.id.txtTempatMengaji);
            viewHolder.txtLabelSedekah = (TextView) convertView.findViewById(R.id.txtLabelSedekah);
            viewHolder.txtLabelTempatSedekah = (TextView) convertView.findViewById(R.id.txtLabelTempatSedekah);
            viewHolder.txtTempatSedekah = (TextView) convertView.findViewById(R.id.txtTempatSedekah);
            viewHolder.txtTanggalSedekah = (TextView) convertView.findViewById(R.id.txtTanggalSedekah);
            viewHolder.txtDoa = (TextView) convertView.findViewById(R.id.txtDoa);
            viewHolder.txtTanggalDoa = (TextView) convertView.findViewById(R.id.txtTanggalDoa);
            viewHolder.txtFreeText = (TextView) convertView.findViewById(R.id.txtFreeText);
            viewHolder.txtTanggalFreeText = (TextView) convertView.findViewById(R.id.txtTanggalFreeText);

            viewHolder.txtStiker = (ImageView) convertView.findViewById(R.id.txtStiker);
            viewHolder.txtFreeTextStiker = (TextView) convertView.findViewById(R.id.txtFreeTextStiker);

            viewHolder.txtTanggalStiker = (TextView) convertView.findViewById(R.id.txtTanggalStiker);

            viewHolder.icon = (ImageView) convertView.findViewById(R.id.imgIcon);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TimelineAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        if(dataModel.getTl().equalsIgnoreCase("start")){
            viewHolder.lineBottom.setVisibility(View.GONE);
        }else if(dataModel.getTl().equalsIgnoreCase("content")){
            viewHolder.lineBottom.setVisibility(View.VISIBLE);
        }

        Glide.with(mContext).load(dataModel.getIcon()).into(viewHolder.icon);
        viewHolder.itemStiker.setVisibility(View.GONE);
        viewHolder.itemFreeText.setVisibility(View.GONE);
        viewHolder.itemBerdoa.setVisibility(View.GONE);
        viewHolder.itemSedekah.setVisibility(View.GONE);
        viewHolder.itemMengaji.setVisibility(View.GONE);
        viewHolder.itemSholat.setVisibility(View.GONE);
        viewHolder.itemPuasa.setVisibility(View.GONE);
        if(dataModel.getType() == 3){
            SpannableStringBuilder bersamaBold = null, diBold = null, keteranganBold;
            SpannableStringBuilder sbLengkap;
            /*sholat*/
            viewHolder.itemSholat.setVisibility(View.VISIBLE);
            viewHolder.txtLabelBersama.setVisibility(View.GONE);
            viewHolder.txtTempat.setVisibility(View.GONE);
            viewHolder.txtBersama.setVisibility(View.GONE);
            viewHolder.txtLabelTempat.setVisibility(View.GONE);

            keteranganBold = setToBold(dataModel.getKeterangan(), dataModel.getKeterangan());
            sbLengkap = new SpannableStringBuilder();
            sbLengkap.append(keteranganBold);
            if(dataModel.getBersama().equalsIgnoreCase("nothing") == false){
                bersamaBold = setToBold(dataModel.getBersama(), dataModel.getBersama());
                sbLengkap.append(" Bersama ");
                sbLengkap.append(bersamaBold);
            }
            if (dataModel.getDi().equalsIgnoreCase("nothing") == false){
                diBold = setToBold(dataModel.getDi(), dataModel.getDi());
                sbLengkap.append(" di ");
                sbLengkap.append(diBold);
            }
            viewHolder.txtSholat.setText(sbLengkap);
            viewHolder.txtTanggalSholat.setText(dataModel.getTanggal());
        }else if(dataModel.getType() == 7){
            SpannableStringBuilder bersamaBold = null, diBold = null, keteranganBold;
            SpannableStringBuilder sbLengkap;
            /*mengaji*/
            viewHolder.itemMengaji.setVisibility(View.VISIBLE);
            viewHolder.txtLabelTempatMengaji.setVisibility(View.GONE);
            viewHolder.txtTempatMengaji.setVisibility(View.GONE);
            keteranganBold = setToBold(dataModel.getBersama(), dataModel.getBersama());
            sbLengkap = new SpannableStringBuilder();
            sbLengkap.append(keteranganBold);
            if (dataModel.getDi().equalsIgnoreCase("nothing") == false){
                diBold = setToBold(dataModel.getDi(), dataModel.getDi());
                sbLengkap.append(" di ");
                sbLengkap.append(diBold);
            }
            viewHolder.txtMengaji.setText(sbLengkap);
            viewHolder.txtTanggalMengaji.setText(dataModel.getTanggal());
        }else if(dataModel.getType() == 4){
            /*sedekah*/
            Double value = Double.valueOf(dataModel.getNominal()) ;
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setCurrencySymbol("Rp.");
            ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
            String gainString = formatter.format(value);
            SpannableStringBuilder bersamaBold = null, diBold = null, keteranganBold;
            keteranganBold = setToBold("Sedekah "+gainString, "Sedekah "+gainString);
            viewHolder.itemSedekah.setVisibility(View.VISIBLE);
            viewHolder.txtLabelTempatSedekah.setVisibility(View.GONE);
            viewHolder.txtLabelSedekah.setVisibility(View.VISIBLE);
            viewHolder.txtLabelSedekah.setText(keteranganBold);
            viewHolder.txtTanggalSedekah.setText(dataModel.getTanggal());
        }else if(dataModel.getType() == 1){
            /*berdoa*/

            viewHolder.itemBerdoa.setVisibility(View.VISIBLE);
            viewHolder.txtDoa.setText(dataModel.getKeterangan());
            viewHolder.txtTanggalDoa.setText(dataModel.getTanggal());
        }else if(dataModel.getType() == 5){
            /*freetext*/
            SpannableStringBuilder bersamaBold = null, diBold = null, keteranganBold;
            SpannableStringBuilder sbLengkap;
            keteranganBold = setToBold(dataModel.getKeterangan(), dataModel.getStiker());
            sbLengkap = new SpannableStringBuilder();
            sbLengkap.append(keteranganBold);
            if(dataModel.getBersama().equalsIgnoreCase("nothing") == false){
                bersamaBold = setToBold(dataModel.getBersama(), dataModel.getBersama());
                sbLengkap.append(" Bersama ");
                sbLengkap.append(bersamaBold);
            }
            if (dataModel.getDi().equalsIgnoreCase("nothing") == false){
                diBold = setToBold(dataModel.getDi(), dataModel.getDi());
                sbLengkap.append(" di ");
                sbLengkap.append(diBold);
            }

            viewHolder.itemFreeText.setVisibility(View.VISIBLE);
            viewHolder.txtFreeText.setText(sbLengkap);
            viewHolder.txtTanggalFreeText.setText(dataModel.getTanggal());

        }else if(dataModel.getType() == 6){
            viewHolder.itemStiker.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(dataModel.getStiker()).into(viewHolder.txtStiker);
            viewHolder.txtTanggalStiker.setText(dataModel.getTanggal());
        }else if(dataModel.getType() == 2){
            /*Puasa*/
            SpannableStringBuilder bersamaBold = null, diBold = null, keteranganBold;
            keteranganBold = setToBold(dataModel.getKeterangan(), dataModel.getKeterangan());
            viewHolder.itemPuasa.setVisibility(View.VISIBLE);
            viewHolder.txtPuasa.setText(keteranganBold);
            viewHolder.txtTanggalPuasa.setText(dataModel.getTanggal());
        }
        return convertView;
    }

}
