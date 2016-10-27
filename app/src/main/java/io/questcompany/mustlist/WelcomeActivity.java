package io.questcompany.mustlist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.questcompany.mustlist.entity.User;
import io.questcompany.mustlist.util.NetworkManager;
import io.questcompany.mustlist.util.PrefUtil;
import io.questcompany.mustlist.util.Singleton;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private int[] layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        ViewPager viewPager = (ViewPager) findViewById(R.id.welcome_view_pager);
        layouts = new int[]{
                R.layout.welcome_slide_1,
                R.layout.welcome_slide_2,
                R.layout.welcome_slide_3,
                R.layout.welcome_slide_4};


        WelcomeViewPagerAdapter welcomeViewPagerAdapter = new WelcomeViewPagerAdapter();

        if (viewPager != null) {
            viewPager.setAdapter(welcomeViewPagerAdapter);
        } else {
            Log.e(TAG, "welcomeViewPager is null");
        }

        User user;
        if(!PrefUtil.isUser(this)) {
            user = NetworkManager.postUser(this);
            PrefUtil.setUser(this, user);
            Log.d(TAG, "user: " + user);
        } else {
            user = PrefUtil.getUser(this);
            Log.d(TAG, "is registered user : " + user);
        }

        Singleton singleton = Singleton.getInstance();
        singleton.setIdAndKey(user.getId(), user.getKey());
    }


    private class WelcomeViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Button startButton;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            if (startButton == null) {
                startButton = (Button) findViewById(R.id.welcome_start_button);
                if (startButton != null) {
                    startButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            launchHomeScreen();
                        }
                    });
                }
            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        private void launchHomeScreen() {
            Log.d(TAG, "launchHomeScreen: start");
//            prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
    }
}
