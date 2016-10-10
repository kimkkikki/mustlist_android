package com.questcompany.mustlist;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.questcompany.mustlist.entity.Notice;
import com.questcompany.mustlist.util.NetworkManager;

import java.util.List;

/**
 * Created by kimkkikki on 2016. 10. 10..
 * 공지사항 Activity
 */

public class NoticeActivity extends AppCompatActivity {
    ExpandableListView expandableListView;
    List<Notice> noticeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_activity);

        noticeList = NetworkManager.getNotice();

        expandableListView = (ExpandableListView) findViewById(R.id.notice_expandable_list_view);
        expandableListView.setAdapter(new NoticeExpandableListViewAdapter());
    }

    private class NoticeExpandableListViewAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return noticeList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return 1;
        }

        @Override
        public Object getGroup(int i) {
            return null;
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.notice_list_title, viewGroup, false);
            }

            TextView titleTextView = (TextView) view.findViewById(R.id.notice_title_text_view);

            Notice notice = noticeList.get(i);
            titleTextView.setText(notice.getTitle());

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.notice_list_contents, viewGroup, false);
            }

            TextView titleTextView = (TextView) view.findViewById(R.id.notice_contents_text_view);

            Notice notice = noticeList.get(i);
            titleTextView.setText(notice.getContents());

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }
}
