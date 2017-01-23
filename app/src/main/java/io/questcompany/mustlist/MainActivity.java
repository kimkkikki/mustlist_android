package io.questcompany.mustlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import io.questcompany.mustlist.entity.Must;
import io.questcompany.mustlist.entity.Pay;
import io.questcompany.mustlist.util.AlertUtil;
import io.questcompany.mustlist.manager.NetworkManager;
import io.questcompany.mustlist.util.DateUtil;
import io.questcompany.mustlist.util.PrefUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimkkikki on 2016. 9. 26..
 * MainActivity
 */

//TODO: 1000 원부터 하는건 어떨까? 아니면 100원
//TODO: 30일 넘어가면 카드수수료 내야함?? 알아보자 내야하면 30일 단위
//TODO: 30일 단위로 하게 될 경우에 동일하게 재등록이 있어야 하지 않을까
//TODO: SNS Share 기능 필요함 (등록 / 완료시) - Message 만들기가 힘듬. 언어별로
//TODO: 전문 번역이 필요한가?
//TODO: 푸시 발송 개발 해야댐 - NOK

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = "MainActivity";

    private boolean doubleBackToExitPressOne = false;

    private List<Must> mustList;

    private ListViewAdapter listViewAdapter;

    ProgressDialog loadingDialog;
    String[] depositArray;
    private int page = 1;

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
        if (!loadingDialog.isShowing())
            loadingDialog.show();

        new Thread() {
            @Override
            public void run() {
                super.run();

                List<Must> serverReceivedData = NetworkManager.getMustList(MainActivity.this);
                if (serverReceivedData != null) {
                    mustList = serverReceivedData;
                    page = 1;

                    Pay pay = PrefUtil.getPayData(MainActivity.this);
                    Must must = PrefUtil.getMustData(MainActivity.this);

                    if (pay != null) {
                        Log.d(TAG, "onResume, find un uploaded pay data");
                        int code = NetworkManager.pay(MainActivity.this, pay, must);
                        if (code == 200) {
                            PrefUtil.deletePayData(MainActivity.this);
                            PrefUtil.deleteMustData(MainActivity.this);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listViewAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertUtil.alert(MainActivity.this, R.string.alert_not_connect_server);
                        }
                    });
                }

                loadingDialog.dismiss();
                Log.d(TAG, "onResume: MistList : " + mustList.toString());
            }
        }.start();
    }

    @Override
    public void onRefresh() {
        this.onResume();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    private void moreDataLoad() {
        loadingDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<Must> moreList = NetworkManager.getMustList(MainActivity.this, page);
                if (moreList != null && moreList.size() != 0) {
                    page += 1;
                    mustList.addAll(moreList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
                loadingDialog.dismiss();
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        loadingDialog = AlertUtil.getLoadingDialog(this);
        depositArray = getResources().getStringArray(R.array.deposit);

        // Tool Bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        } else {
            Log.e(TAG, "onCreate: toolbar is null");
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
        final ListViewCompat listViewCompat = (ListViewCompat) findViewById(R.id.main_list_view);
        listViewAdapter = new ListViewAdapter();
        listViewCompat.setAdapter(listViewAdapter);

        listViewCompat.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    int count = listViewCompat.getCount() - 1;
                    if (listViewCompat.getLastVisiblePosition() >= count && count % 10 == 0) {
                        Log.d(TAG, "onScrollStateChanged: last " + count);
                        moreDataLoad();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        listViewCompat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (i == 0) {
                    startActivity(new Intent(MainActivity.this, AddActivity.class));
                } else {
                    Log.d(TAG, "onItemClick: ListView " + mustList.get(i - 1));
                    Must must = mustList.get(i - 1);
                    if (must.end) {
                        if (must.success) {
                            AlertUtil.alert(MainActivity.this, R.string.alert_end_success);
                        } else {
                            AlertUtil.alert(MainActivity.this, R.string.alert_end_failure);
                        }
                    } else if (DateUtil.compareStartDateAndToday(MainActivity.this, must.start_date)) {
                        AlertUtil.alert(MainActivity.this, R.string.alert_not_today);
                    } else {
                        int message;
                        if (must.check) {
                            message = R.string.alert_check_cancel_question;
                        } else {
                            message = R.string.alert_check_question;
                        }
                        AlertUtil.alert(MainActivity.this, message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if (!NetworkManager.checkNetworkStatus(MainActivity.this)) {
                                    AlertUtil.alert(MainActivity.this, R.string.alert_not_connected_network);
                                    return;
                                }

                                if (!loadingDialog.isShowing())
                                    loadingDialog.show();

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        final int code = NetworkManager.checkMust(MainActivity.this, mustList.get(i - 1));

                                        dialog.dismiss();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                switch (code) {
                                                    case 201:
                                                        // Success
                                                        AlertUtil.alert(MainActivity.this, R.string.alert_check_success);
                                                        break;
                                                    case 200:
                                                        // Un Check
                                                        break;
                                                    default:
                                                        // Failure
                                                        AlertUtil.alert(MainActivity.this, R.string.alert_not_today);
                                                }
                                                MainActivity.this.onResume();
                                            }
                                        });
                                    }
                                }.start();
                            }
                        });
                    }
                }
            }
        });

        initializeSideHeaderView();
    }

    private void initializeSideHeaderView() {
        // Side Menu Selected Listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            View headerView = navigationView.getHeaderView(0);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.getDisplayName() != null && user.getPhotoUrl() != null) {
                    CircleImageView profile = (CircleImageView) headerView.findViewById(R.id.side_profile_image);
                    Glide.with(this).load(user.getPhotoUrl().toString()).into(profile);
                    TextView name = (TextView) headerView.findViewById(R.id.side_profile_name);
                    name.setText(user.getDisplayName());
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: code: " + requestCode + " result : " + resultCode);
        if (requestCode == SettingActivity.SETTING_ACTIVITY_CODE) {
            if (resultCode == SettingActivity.SETTING_LOGOUT_RESULT) {
                finish();
            }
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
        switch (item.getItemId()) {
            case R.id.side_setting:
                startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), SettingActivity.SETTING_ACTIVITY_CODE);
                break;

            case R.id.side_score:
                startActivity(new Intent(MainActivity.this, ScoreActivity.class));
                break;

            case R.id.side_use:
                AlertUtil.alert(this, R.string.use_not_yet);
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

        private Object getViewFromId(View view, int id) {
            Object object;
            if (view.getTag(id) != null) {
                object = view.getTag(id);
            } else {
                object = view.findViewById(id);
                view.setTag(id, object);
            }

            return object;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Context context = viewGroup.getContext();
            LayoutInflater inflaterCompat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (view == null) {
                if (i == 0) {
                    view = inflaterCompat.inflate(R.layout.main_list_view_empty, viewGroup, false);
                } else {
                    view = inflaterCompat.inflate(R.layout.main_list_view, viewGroup, false);
                }
            } else {
                if (i == 0 && view.getId() != R.id.main_list_empty_layout) {
                    view = inflaterCompat.inflate(R.layout.main_list_view_empty, viewGroup, false);
                } else if (i != 0 && view.getId() != R.id.main_list_layout) {
                    view = inflaterCompat.inflate(R.layout.main_list_view, viewGroup, false);
                }
            }

            if (i != 0)  {
                // Must List Data 처리 부분
                TextView nameTextView = (TextView) getViewFromId(view, R.id.main_list_name);
                TextView depositTextView = (TextView) getViewFromId(view, R.id.main_list_deposit);
                TextView periodTextView = (TextView) getViewFromId(view, R.id.main_list_period);
                ProgressBar progressBar = (ProgressBar) getViewFromId(view, R.id.main_list_progress);
                TextView progressPercent = (TextView) getViewFromId(view, R.id.main_list_progress_percent);
                TextView statusTag = (TextView) getViewFromId(view, R.id.main_list_status);
                TextView checkboxTag = (TextView) getViewFromId(view, R.id.main_list_checkbox_tag);
                ImageView checkbox = (ImageView) getViewFromId(view, R.id.main_list_checkbox);

                Must must = mustList.get(i - 1);
                nameTextView.setText(must.title);

                depositTextView.setText(depositArray[must.deposit]);

                String startDate = DateUtil.convertDateToUTCDate(MainActivity.this, must.start_date);
                String endDate = DateUtil.convertDateToUTCDate(MainActivity.this, must.end_date);
                periodTextView.setText(startDate + " ~ " + endDate);

                // 남은 일수 계산 & 수행 일자 문자 생성
                progressBar.setMax(must.total_count);
                progressBar.setProgress(must.check_count);

                int percent = (int)(((float) must.check_count / (float) must.total_count) * 100);
                String percentString = percent + " %";
                progressPercent.setText(percentString);

                if (percent >= 80) {
                    progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                } else if (percent >= 40) {
                    progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorGreen), PorterDuff.Mode.MULTIPLY);
                } else {
                    progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorRed), PorterDuff.Mode.MULTIPLY);
                }

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
                    checkboxTag.setVisibility(View.INVISIBLE);
                    checkbox.setVisibility(View.INVISIBLE);

                } else {
                    // 끝나지 않았고, 시작일이 오늘보다 뒤일때
                    if (!DateUtil.compareStartDateAndToday(MainActivity.this, must.start_date)) {
                        statusTag.setText(R.string.main_list_status_progress);
                        checkboxTag.setVisibility(View.VISIBLE);
                        checkbox.setVisibility(View.VISIBLE);

                        // Checkbox Text & Checkbox Status
                        if (must.check) {
                            checkboxTag.setText(R.string.main_list_checked);
                            checkbox.setImageResource(R.mipmap.checkbox_green);
                            setTagBackgroundColor(view, R.drawable.round_corner_green);
                        } else {
                            checkboxTag.setText(R.string.main_list_not_checked);
                            checkbox.setImageResource(R.mipmap.checkbox_gray);
                            setTagBackgroundColor(view, R.drawable.round_corner_gray);
                        }
                    } else {
                        checkboxTag.setVisibility(View.INVISIBLE);
                        checkbox.setVisibility(View.INVISIBLE);
                        statusTag.setText(R.string.main_list_status_scheduled);
                        setTagBackgroundColor(view, R.drawable.round_corner_gray);
                    }
                }
            }

            return view;
        }

        private void setTagBackgroundColor(View view, int resourceId) {
            TextView nameTag = (TextView) getViewFromId(view, R.id.main_list_name_tag);
            nameTag.setBackgroundResource(resourceId);
            TextView periodTag = (TextView) getViewFromId(view, R.id.main_list_period_tag);
            periodTag.setBackgroundResource(resourceId);
            TextView depositTag = (TextView) getViewFromId(view, R.id.main_list_deposit_tag);
            depositTag.setBackgroundResource(resourceId);
            TextView statusTag = (TextView) getViewFromId(view, R.id.main_list_status);
            statusTag.setBackgroundResource(resourceId);
            TextView progressTag = (TextView) getViewFromId(view, R.id.main_list_progress_tag);
            progressTag.setBackgroundResource(resourceId);
        }
    }
}
