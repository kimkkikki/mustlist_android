package com.questcompany.mustlist;

import android.content.Context;
import android.content.Intent;
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

import com.questcompany.mustlist.entity.Must;
import com.questcompany.mustlist.util.NetworkManager;

import java.util.List;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * MainActivity
 */

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = "MainActivity";

    private boolean doubleBackToExitPressOne = false;

    private List<Must> mustList;

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

        mustList = NetworkManager.getMustList();

        // List View
        ListViewCompat listViewCompat = (ListViewCompat) findViewById(R.id.main_listview);
        ListViewAdapter listViewAdapter = new ListViewAdapter();
        listViewCompat.setAdapter(listViewAdapter);

        listViewCompat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mustList.size() != i) {
                    Log.d(TAG, "onItemClick: ListView " + i);
                } else {
                    startActivity(new Intent(MainActivity.this, AddActivity.class));
                }
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
            return mustList.size() + 1;
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

            if (mustList.size() + 1 >= i) {
                Context context = viewGroup.getContext();
                LayoutInflater inflaterCompat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterCompat.inflate(R.layout.main_list_view_empty, viewGroup, false);
            } else {
                //TODO: 데이터 있을때 처리 필요 아래는 임시로 추가 오브젝트
                Context context = viewGroup.getContext();
                LayoutInflater inflaterCompat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterCompat.inflate(R.layout.main_list_view_empty, viewGroup, false);
            }

            return view;
        }
    }
}
