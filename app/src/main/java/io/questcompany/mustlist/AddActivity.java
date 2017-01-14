package io.questcompany.mustlist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import io.questcompany.mustlist.entity.Must;
import io.questcompany.mustlist.util.AlertUtil;
import io.questcompany.mustlist.util.DateUtil;
import io.questcompany.mustlist.manager.NetworkManager;

/**
 * Created by kimkkikki on 2016. 9. 28..
 * Add Activity
 */

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";
    private int[] layouts;
    private InputMethodManager inputMethodManager;
    private ViewPager viewPager;

    private int depositSelected = 1;
    private int pageOffset = 0;

    private String[] depositArray;

    private Must must;

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

        depositArray = getResources().getStringArray(R.array.deposit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO : Billing OnDestroy;
    }

    // 서버에 Must 추가
    private void addMust() {
        NetworkManager.addMust(AddActivity.this, must);

        AlertUtil.alert(AddActivity.this, R.string.alert_add_success, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
    }

    private class AddViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private EditText addTitleEditText;
        private Spinner depositSpinner;
        private Button nextButton;
        private Button okButton;

        private Button startDayButton;
        private Button endDayButton;

        private String startDay;
        private String endDay;

        AddViewPagerAdapter() {
        }

        private boolean checkData() {
            if (addTitleEditText != null) {
                if (addTitleEditText.getText().toString().isEmpty()) {
                    AlertUtil.alert(AddActivity.this, R.string.add_title_hint);
                    return false;
                } else {
                    inputMethodManager.hideSoftInputFromWindow(addTitleEditText.getWindowToken(), 0);
                }
            } else {
                return false;
            }

            if (startDay == null || endDay == null) {
                AlertUtil.alert(AddActivity.this, R.string.alert_set_day);
                return false;
            }

            return true;
        }

        private void goPreview() {
            // 서버에 프리뷰 전달
            if (must == null) {
                must = new Must();
            }

            must.title = addTitleEditText.getText().toString();
            must.start_date = startDay;
            must.end_date = endDay;
            must.deposit = depositSelected;

            Log.d(TAG, "goPreview: must : " + must);

            Must serverReceivedData = NetworkManager.previewMust(AddActivity.this, must);
            if (serverReceivedData != null) {
                must = serverReceivedData;
                pageOffset += 1;
                viewPager.setCurrentItem(pageOffset, true);

                TextView nameTextView = (TextView) findViewById(R.id.preview_name);
                TextView startDayTextView = (TextView) findViewById(R.id.preview_start_day);
                TextView endDayTextView = (TextView) findViewById(R.id.preview_end_day);
                TextView daysTextView = (TextView) findViewById(R.id.preview_days);
                TextView depositTextView = (TextView) findViewById(R.id.preview_deposit);
                TextView defaultPointTextView = (TextView) findViewById(R.id.preview_default_point);

                nameTextView.setText(addTitleEditText.getText().toString());
                startDayTextView.setText(startDay.split("T")[0]);
                endDayTextView.setText(endDay.split("T")[0]);
                String daysString = must.days + " " + getString(R.string.preview_days_unit);
                daysTextView.setText(daysString);
                depositTextView.setText(depositArray[depositSelected]);

                String pointString = "" + must.default_point;
                defaultPointTextView.setText(pointString);
            }
        }

        View.OnClickListener dayClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        inputMethodManager.hideSoftInputFromWindow(addTitleEditText.getWindowToken(), 0);
                        switch (view.getId()) {
                            case R.id.add_start_day_picker_button:
                                startDayButton.setText("" + year + "-" + (month + 1) + "-" + day);
                                startDay = DateUtil.getStartDateStringWithYearAndMonthAndDay(year, month, day);
                                break;
                            case R.id.add_end_day_picker_button:
                                endDayButton.setText("" + year + "-" + (month + 1) + "-" + day);
                                endDay = DateUtil.getEndDateStringWithYearAndMonthAndDay(year, month, day);
                                break;
                            default:
                                Log.d(TAG, "onDateSet: Error!!");
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(date.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, 90);
                dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                dialog.show();
            }
        };

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
                        if (checkData()) {
                            goPreview();
                        }
                        return true;
                    }
                });
            }

            startDayButton = (Button) findViewById(R.id.add_start_day_picker_button);
            if (startDayButton != null) {
                startDayButton.setOnClickListener(dayClickListener);
            }

            endDayButton = (Button) findViewById(R.id.add_end_day_picker_button);
            if (endDayButton != null) {
                endDayButton.setOnClickListener(dayClickListener);
            }

            if (depositSpinner == null) {
                depositSpinner = (Spinner) findViewById(R.id.add_deposit_spinner);
                if (depositSpinner != null) {
                    depositSpinner.setSelection(1);
                    depositSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            depositSelected = i;
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
                        if (checkData()) {
                            goPreview();
                        }
                    }
                });
            }

            if (okButton == null) {
                okButton = (Button) findViewById(R.id.preview_ok_button);
                if (okButton != null) {
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (depositSelected == 0) {
                                addMust();
                            } else {
//                                BillingManager billingManager = new BillingManager(AddActivity.this);
//                                billing();
                            }
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
    }
}
