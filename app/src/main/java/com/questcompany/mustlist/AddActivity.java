package com.questcompany.mustlist;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by kimkkikki on 2016. 9. 28..
 * Add Activity
 */

public class AddActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int[] layouts;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.add_view_pager);
        layouts = new int[]{
                R.layout.add_slide_1};
        AddViewPagerAdapter addViewPagerAdapter = new AddViewPagerAdapter();
        viewPager.setAdapter(addViewPagerAdapter);

        final GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener());
        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        gd.onTouchEvent(event);
                        break;
                }
                return true;
            }
        });
    }
    private class AddViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private EditText addTitleEditText;

        public AddViewPagerAdapter() {
        }

        private void titleNext() {
            if (addTitleEditText != null) {
                if (addTitleEditText.getText().toString().isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AddActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.setMessage("할일의 제목을 입력해 주세요");
                    alert.show();
                } else {

                    inputMethodManager.hideSoftInputFromWindow(addTitleEditText.getWindowToken(), 0);
                }
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            addTitleEditText = (EditText) findViewById(R.id.add_title_edit_text);

            if (addTitleEditText != null) {
                addTitleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        titleNext();
                        return true;
                    }
                });
            }

            final Button titleNextButton = (Button) findViewById(R.id.add_title_next_button);
            if (titleNextButton != null) {
                titleNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        titleNext();
                    }
                });
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
    }
}
