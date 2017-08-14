package salam.gopray.id;

import android.os.Bundle;
import android.view.KeyEvent;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.pddstudio.preferences.encrypted.EncryptedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/06/17.
 */

public class MyWelcomeActivity extends AhoyOnboarderActivity {
    EncryptedPreferences encryptedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        encryptedPreferences = new EncryptedPreferences.Builder(this).withEncryptionPassword(getString(R.string.KeyPassword)).build();
        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("", "", R.drawable.ic_text1);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("", "", R.drawable.ic_text2);

        ahoyOnboarderCard1.setBackgroundColor(android.R.color.transparent);
        ahoyOnboarderCard2.setBackgroundColor(android.R.color.transparent);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.white);
            page.setDescriptionColor(R.color.grey_200);
        }

        setFinishButtonTitle("Get Started");
        showNavigationControls(true);
        //setGradientBackground();
        setImageBackground(R.drawable.ic_bg_welcome);

//        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        //setFont(face);

        setInactiveIndicatorColor(R.color.grey_600);
        setActiveIndicatorColor(R.color.white);

        setOnboardPages(pages);
    }
    @Override
    public void onFinishButtonPressed() {
        encryptedPreferences.edit().putString("WELCOME", "1").apply();
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //do whatever you need for the hardware 'back' button
            encryptedPreferences.edit().putString("WELCOME", "1").apply();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}