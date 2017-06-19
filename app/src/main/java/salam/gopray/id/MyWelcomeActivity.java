package salam.gopray.id;

import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

/**
 * Created by root on 11/06/17.
 */

public class MyWelcomeActivity extends WelcomeActivity {
    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorPrimary)
                .page(new TitlePage(R.drawable.tab_timeline,
                        "Pray Timeline")
                        .background(R.color.colorAccent)
                )
                .page(new TitlePage(R.drawable.tab_ortu,
                        "Pray Circle")
                        .background(R.color.colorPrimary)
                )
                .page(new TitlePage(R.drawable.tab_meme,
                        "Pray Quote")
                        .background(R.color.colorPrimaryDark)
                )
                .swipeToDismiss(true)
                .build();
    }
}
