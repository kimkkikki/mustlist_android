package io.questcompany.mustlist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.questcompany.mustlist.entity.Must;
import io.questcompany.mustlist.util.AlertUtil;
import io.questcompany.mustlist.manager.NetworkManager;
import io.questcompany.mustlist.util.PrefUtil;
import io.questcompany.mustlist.util.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * MainActivity
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = "MainActivity";

    private boolean doubleBackToExitPressOne = false;

    private List<Must> mustList;

    private ListViewAdapter listViewAdapter;

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
    protected void onResume() {
        super.onResume();
        List<Must> serverReceivedData = NetworkManager.getMustList(this);
        if (serverReceivedData != null) {
            mustList = serverReceivedData;
        }
        listViewAdapter.notifyDataSetChanged();
        Log.d(TAG, "onResume: MistList : " + mustList.toString());
    }

    @Override
    public void onRefresh() {
        this.onResume();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        // Tool Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        } else {
            Log.e(TAG, "onCreate: toolbar is null");
        }

        // Side Menu Selected Listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
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

        mustList = new ArrayList<>();

        // List View
        ListViewCompat listViewCompat = (ListViewCompat) findViewById(R.id.main_listview);
        listViewAdapter = new ListViewAdapter();
        listViewCompat.setAdapter(listViewAdapter);

        listViewCompat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    startActivity(new Intent(MainActivity.this, AddActivity.class));
                } else {
                    Log.d(TAG, "onItemClick: ListView " + mustList.get(i - 1));
                    Must must = mustList.get(i - 1);
                    if (must.check) {
                        AlertUtil.alert(MainActivity.this, "오늘은 이미 체크했습니다");
                    } else {
                        NetworkManager.checkMust(MainActivity.this, mustList.get(i - 1));
                        MainActivity.this.onResume();
                    }
                }
            }
        });

        // Side Headers
        TextView sideIdTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.side_id_text_view);
        TextView sidePointTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.side_point_text_view);
        if (sideIdTextView == null) {
            Log.d(TAG, "onCreate: side textView is null");
        } else {
            sideIdTextView.setText(PrefUtil.getId(this));
        }

        if (sidePointTextView != null) {
            Singleton singleton = Singleton.getInstance();
            String pointString = "" + singleton.getUser().point;
            sidePointTextView.setText(pointString);
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Log.d(TAG, "onNavigationItemSelected: " + id);

        switch (id) {
            case R.id.side_notice:
                startActivity(new Intent(MainActivity.this, NoticeActivity.class));
                break;

            case R.id.side_point_change:
                startActivity(new Intent(MainActivity.this, PointActivity.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
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
            Log.d(TAG, "getView: index  " + i);

            if (i == 0) {
                // Add Must 버튼 처리 부분
                Context context = viewGroup.getContext();
                LayoutInflater inflaterCompat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterCompat.inflate(R.layout.main_list_view_empty, viewGroup, false);
            } else {
                // Must List Data 처리 부분
                Context context = viewGroup.getContext();
                LayoutInflater inflaterCompat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterCompat.inflate(R.layout.main_list_view, viewGroup, false);

                TextView nameTextView = (TextView) view.findViewById(R.id.main_list_name);
                TextView depositTextView = (TextView) view.findViewById(R.id.main_list_deposit);
                TextView periodTextView = (TextView) view.findViewById(R.id.main_list_period);
                TextView remainCountTextView = (TextView) view.findViewById(R.id.main_list_remain_count);
                TextView checkCountTextView = (TextView) view.findViewById(R.id.main_list_check_count);

                Must must = mustList.get(i - 1);
                nameTextView.setText(must.title);

                String[] depositArray = getResources().getStringArray(R.array.deposit);
                depositTextView.setText(depositArray[must.deposit]);
                periodTextView.setText(must.start_date.split("T")[0] + " ~ " + must.end_date.split("T")[0]);

                // 남은 일수 계산 & 수행 일자 문자 생성
                String remainString = "" + (must.total_count - must.check_count);
                remainCountTextView.setText(remainString);
                String checkString = "" + must.check_count;
                checkCountTextView.setText(checkString);

//                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                TextView statusTag = (TextView) view.findViewById(R.id.main_list_status);
                // In Progress Or Ended Must
                if (must.end) {
                    String defaultEndString = getString(R.string.main_list_status_end);
                    if (must.success) {
                        defaultEndString += " : " + getString(R.string.main_list_status_success);
                        setTagBackgroundColor(view, R.drawable.round_corner_blue);
                    } else {
                        defaultEndString += " : " + getString(R.string.main_list_status_failure);
                        setTagBackgroundColor(view, R.drawable.round_corner_red);
                    }
                    statusTag.setText(defaultEndString);

                } else {
                    statusTag.setText(R.string.main_list_status_progress);

                    // Checkbox Text & Checkbox Status
                    TextView checkboxTag = (TextView) view.findViewById(R.id.main_list_checkbox_tag);
                    ImageView checkbox = (ImageView) view.findViewById(R.id.main_list_checkbox);
                    if (must.check) {
                        checkboxTag.setText(R.string.main_list_checked);
                        checkbox.setImageResource(R.mipmap.checkbox_green);

                        // Tag Color Green
                        setTagBackgroundColor(view, R.drawable.round_corner_green);
                    } else {
                        checkboxTag.setText(R.string.main_list_not_checked);
                        checkbox.setImageResource(R.mipmap.checkbox_gray);
                    }
                }
            }

            return view;
        }

        private void setTagBackgroundColor(View view, int resourceId) {
            TextView nameTag = (TextView) view.findViewById(R.id.main_list_name_tag);
            nameTag.setBackgroundResource(resourceId);
            TextView periodTag = (TextView) view.findViewById(R.id.main_list_period_tag);
            periodTag.setBackgroundResource(resourceId);
            TextView depositTag = (TextView) view.findViewById(R.id.main_list_deposit_tag);
            depositTag.setBackgroundResource(resourceId);
            TextView statusTag = (TextView) view.findViewById(R.id.main_list_status);
            statusTag.setBackgroundResource(resourceId);
            TextView remainCountTag = (TextView) view.findViewById(R.id.main_list_remain_count_tag);
            remainCountTag.setBackgroundResource(resourceId);
            TextView checkCountTag = (TextView) view.findViewById(R.id.main_list_check_count_tag);
            checkCountTag.setBackgroundResource(resourceId);
        }
    }
}
