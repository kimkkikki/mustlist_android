package io.questcompany.mustlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.questcompany.mustlist.entity.Score;
import io.questcompany.mustlist.manager.NetworkManager;
import io.questcompany.mustlist.util.AlertUtil;

/**
 * Created by kimkkikki on 2017. 1. 20..
 * 의지점수 Activity
 */

public class ScoreActivity extends AppCompatActivity {

    ScoreListAdapter scoreListAdapter;
    List<Score> scoreList;
    ProgressDialog loadingDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_activity);

        loadingDialog = AlertUtil.getLoadingDialog(this);
        scoreList = new ArrayList<>();

        ListViewCompat listViewCompat = (ListViewCompat) findViewById(R.id.score_list_view);
        scoreListAdapter = new ScoreListAdapter();
        listViewCompat.setAdapter(scoreListAdapter);

        loadingDialog.show();
        new Thread() {
            @Override
            public void run() {
                final List<Score> result = NetworkManager.getScoreList(ScoreActivity.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            scoreList = result;
                            scoreListAdapter.notifyDataSetChanged();
                        } else {
                            AlertUtil.alert(ScoreActivity.this, R.string.alert_not_connect_server);
                        }
                        loadingDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    public class ScoreListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return scoreList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                Context context = parent.getContext();
                LayoutInflater inflaterCompat = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflaterCompat.inflate(R.layout.score_list_view, parent, false);
            }

            Score score_object = scoreList.get(position);

            TextView title = (TextView) getViewFromId(convertView, R.id.score_title);
            TextView type = (TextView) getViewFromId(convertView, R.id.score_type);
            TextView score = (TextView) getViewFromId(convertView, R.id.score_score);

            title.setText(score_object.must_title);

            String scoreString;
            if (score_object.score < 0) {
                scoreString = "" + score_object.score;
                type.setBackgroundResource(R.drawable.round_corner_red);
                score.setBackgroundResource(R.drawable.round_corner_red);
            } else {
                scoreString = "+" + score_object.score;

                if (score_object.type == 'C') {
                    type.setText(R.string.score_created);
                    type.setBackgroundResource(R.drawable.round_corner_green);
                    score.setBackgroundResource(R.drawable.round_corner_green);
                } else {
                    type.setText(R.string.score_success);
                    type.setBackgroundResource(R.drawable.round_corner_blue);
                    score.setBackgroundResource(R.drawable.round_corner_blue);
                }
            }
            score.setText(scoreString);

            return convertView;
        }
    }
}
