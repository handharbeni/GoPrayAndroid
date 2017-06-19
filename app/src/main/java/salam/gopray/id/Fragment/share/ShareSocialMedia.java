package salam.gopray.id.Fragment.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import java.io.File;

//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.share.Sharer;
//import com.facebook.share.model.ShareLinkContent;
//import com.facebook.share.widget.ShareDialog;

/**
 * Created by root on 22/05/17.
 */

public class ShareSocialMedia {
//    ShareDialog shareDialog;
//    CallbackManager callbackManager;
    Activity mActivity;
    Fragment fragment;
    Context mContext;

    public ShareSocialMedia(Activity mActivity, Context mContext) {
        this.mActivity = mActivity;
        this.mContext = mContext;
//        callbackManager = CallbackManager.Factory.create();
//        shareDialog = new ShareDialog(this.mActivity);
//        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });
    }

    public ShareSocialMedia(Context mContext) {
        this.mContext = mContext;
    }

    public ShareSocialMedia(Fragment fragment) {
        this.fragment = fragment;
//        callbackManager = CallbackManager.Factory.create();
    }

    public ShareSocialMedia(Activity mActivity) {
        this.mActivity = mActivity;
//        callbackManager = CallbackManager.Factory.create();
//        shareDialog = new ShareDialog(this.mActivity);
//        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });
    }
    public void shareTwitter(String message, String url){
//        String tweetUrl = "https://twitter.com/intent/tweet?text="+message+"&url="
//                + url;
//        Uri uri = Uri.parse(tweetUrl);
//        Intent iShare = new Intent(Intent.ACTION_VIEW, uri);
////        iShare.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//        mActivity.startActivity(iShare);
    }
    public void shareInstagram(String type/* 'image/*' */, String mediaPath){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType(type);
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        mContext.startActivity(Intent.createChooser(share, "Share to"));
    }
    public void shareFacebook(String title, String description, String url){
//        ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                .setContentTitle(title)
//                .setContentDescription(description)
//                .setContentUrl(Uri.parse(url))
//                .build();
//        shareDialog.show(linkContent);
    }
}
