package com.questcompany.mustlist;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * MainActivity
 */

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = "MainActivity";

    private boolean doubleBackToExitPressOne = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressOne) {
            super.onBackPressed();
        } else {
            this.doubleBackToExitPressOne = true;
            Toast.makeText(this, R.string.double_back_exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressOne = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Tool Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        } else {
            Log.e(TAG, "onCreate: toolbar is null");
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
        } else {
            Log.e(TAG, "onCreate: drawerLayout is null");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            Log.e(TAG, "onCreate: getSupportActionBar is null");
        }

        // List View
        ListViewCompat listViewCompat = (ListViewCompat) findViewById(R.id.main_listview);
        ListViewAdapter listViewAdapter = new ListViewAdapter();
        listViewCompat.setAdapter(listViewAdapter);

        listViewCompat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: ListView " + i);
            }
        });
    }

    // Tool Bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    public class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            Context context = viewGroup.getContext();
            LayoutInflater inflaterCompat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflaterCompat.inflate(R.layout.main_list_view_empty, viewGroup, false);

//            if (i == 3) {
//                if (view == null) {
//                }
//            } else {
//                if (view == null) {
//                    LayoutInflater inflaterCompat = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//                    view = inflaterCompat.inflate(R.layout.listview_item_2, viewGroup, false);
//                }
//            }


            return view;
        }
    }
}
