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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Created by kimkkikki on 2016. 9. 28..
 * Add Activity
 */

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

    private Toolbar toolbar;
    private int[] layouts;
    private InputMethodManager inputMethodManager;
    private ViewPager viewPager;

    private int pageOffset = 0;

    private String[] startDayArray;
    private String[] periodArray;
    private String[] amountArray;

    @Override
    public void onBackPressed() {
        if (pageOffset > 0) {
            pageOffset -= 1;
            viewPager.setCurrentItem(pageOffset, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        viewPager = (ViewPager) findViewById(R.id.add_view_pager);
        layouts = new int[]{
                R.layout.add_slide_1,
                R.layout.add_slide_2};
        AddViewPagerAdapter addViewPagerAdapter = new AddViewPagerAdapter();
        viewPager.setAdapter(addViewPagerAdapter);

        final GestureDetector gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());
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

        startDayArray = getResources().getStringArray(R.array.start_day);
        periodArray = getResources().getStringArray(R.array.period);
        amountArray = getResources().getStringArray(R.array.amount);

    }

    private class AddViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private EditText addTitleEditText;
        private Spinner startDaySpinner;
        private Spinner periodSpinner;
        private Spinner amountSpinner;
        private Button nextButton;

        private int startDaySelected = 0;
        private int periodSelected = 0;
        private int amountSelected = 0;

        AddViewPagerAdapter() {
        }

        private boolean checkTitle() {
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
                    return false;
                } else {
                    inputMethodManager.hideSoftInputFromWindow(addTitleEditText.getWindowToken(), 0);
                    return true;
                }
            }

            return false;
        }

        private void goNext() {
            pageOffset += 1;
            viewPager.setCurrentItem(pageOffset, true);

            TextView nameTextView = (TextView) findViewById(R.id.preview_name);
            TextView startDayTextView = (TextView) findViewById(R.id.preview_start_day);
            TextView periodTextView = (TextView) findViewById(R.id.preview_period);
            TextView amountTextView = (TextView) findViewById(R.id.preview_amount);

            nameTextView.setText(addTitleEditText.getText().toString());
            startDayTextView.setText(startDayArray[startDaySelected]);
            periodTextView.setText(periodArray[periodSelected]);
            amountTextView.setText(amountArray[amountSelected]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            addTitleEditText = (EditText) findViewById(R.id.add_must_title);

            if (addTitleEditText != null) {
                addTitleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        if (checkTitle()) {
                            goNext();
                        }
                        return true;
                    }
                });
            }

            // 시작일 셀렉트 박스
            if (startDaySpinner == null) {
                startDaySpinner = (Spinner) findViewById(R.id.add_start_spinner);
                if (startDaySpinner != null) {
                    startDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            startDaySelected = i;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
            }

            // 기간 셀렉트 박스
            if (periodSpinner == null) {
                periodSpinner = (Spinner) findViewById(R.id.add_period_spinner);
                if (periodSpinner != null) {
                    periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            periodSelected = i;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
            }

            if (amountSpinner == null) {
                amountSpinner = (Spinner) findViewById(R.id.add_amount_spinner);
                if (amountSpinner != null) {
                    amountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            amountSelected = i;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
            }

            if (nextButton == null) {
                nextButton = (Button) findViewById(R.id.add_next_button);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkTitle()) {
                            goNext();
                        }
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
