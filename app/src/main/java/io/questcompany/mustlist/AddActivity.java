package io.questcompany.mustlist;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.Calendar;
import java.util.Date;

import io.questcompany.mustlist.entity.Pay;
import io.questcompany.mustlist.entity.Must;
import io.questcompany.mustlist.util.AlertUtil;
import io.questcompany.mustlist.util.DateUtil;
import io.questcompany.mustlist.manager.NetworkManager;
import io.questcompany.mustlist.util.PrefUtil;

/**
 * Created by kimkkikki on 2016. 9. 28..
 * Add Activity
 */

public class AddActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final String TAG = "AddActivity";
    private int[] layouts;
    private InputMethodManager inputMethodManager;
    private ViewPager viewPager;

    private int depositSelected = 1;
    private int pageOffset = 0;

    private Must must;

    // Pay
    BillingProcessor billingProcessor;

    private String[] depositArray;
    private String[] inAppPurchaseItemArray;
    private boolean isInitializeBilling = false;

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

        // Pay Initialize
        inAppPurchaseItemArray = getResources().getStringArray(R.array.in_app_purchase_items);
        billingProcessor = new BillingProcessor(this, getString(R.string.in_app_public_key), this);
    }

    @Override
    protected void onDestroy() {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.onDestroy();
    }

    // 서버에 Must 추가
    private void addMustAndFinish() {
        final ProgressDialog progressDialog = AlertUtil.showProgress(AddActivity.this, R.string.progress_loading);
        new Thread() {
            @Override
            public void run() {
                super.run();
                NetworkManager.addMust(AddActivity.this, must);
                progressDialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertUtil.alert(AddActivity.this, R.string.alert_add_success, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
//                                AlertUtil.shareSNSAlert(AddActivity.this, shareMessage, true);
                                finish();
                            }
                        });
                    }
                });
            }
        }.start();
    }

    private void billing() {
        if (isInitializeBilling) {
            billingProcessor.purchase(this, inAppPurchaseItemArray[depositSelected]);
        } else {
            AlertUtil.alert(this, R.string.alert_not_initial_billing);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onProductPurchased(final String productId, TransactionDetails details) {
        Log.d(TAG, "onProductPurchased: productId : " + productId + ", details : " + details);
        final Pay pay = new Pay();
        pay.product_id = productId;
        pay.order_id = details.purchaseInfo.purchaseData.orderId;
        pay.token = details.purchaseInfo.purchaseData.purchaseToken;
        pay.date = DateUtil.convertDateToString(AddActivity.this, details.purchaseInfo.purchaseData.purchaseTime);

        PrefUtil.savePayData(this, pay);
        PrefUtil.saveMustData(this, must);
        final Must payMust = must;

        // TODO : 단계별로 저장해서 처리하도록 해야함
        final ProgressDialog progressDialog = AlertUtil.showProgress(AddActivity.this, R.string.progress_pay);
        new Thread() {
            @Override
            public void run() {
                super.run();
                int code = NetworkManager.pay(AddActivity.this, pay, payMust);
                if (code == 200) {
                    PrefUtil.deletePayData(AddActivity.this);
                    PrefUtil.deleteMustData(AddActivity.this);

                    billingProcessor.consumePurchase(productId);
                }
                progressDialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertUtil.alert(AddActivity.this, R.string.alert_add_success, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
//                                AlertUtil.shareSNSAlert(AddActivity.this, shareMessage, true);
                                finish();
                            }
                        });
                    }
                });
            }
        }.start();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d(TAG, "onPurchaseHistoryRestored: ");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.d(TAG, "onBillingError: errorcode : " + errorCode + ", error : " + error);
        AlertUtil.alert(this, R.string.alert_billing_failed);
    }

    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "onBillingInitialized: ");
        isInitializeBilling = true;
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

            final ProgressDialog progressDialog = AlertUtil.showProgress(AddActivity.this, R.string.progress_loading);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    final Must serverReceivedData = NetworkManager.previewMust(AddActivity.this, must);
                    if (serverReceivedData != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                TextView nameTextView = (TextView) findViewById(R.id.preview_name);
                                TextView startDayTextView = (TextView) findViewById(R.id.preview_start_day);
                                TextView endDayTextView = (TextView) findViewById(R.id.preview_end_day);
                                TextView daysTextView = (TextView) findViewById(R.id.preview_days);
                                TextView depositTextView = (TextView) findViewById(R.id.preview_deposit);
                                TextView defaultPointTextView = (TextView) findViewById(R.id.preview_default_point);
                                must = serverReceivedData;
                                pageOffset = 1;
                                viewPager.setCurrentItem(pageOffset, true);

                                nameTextView.setText(addTitleEditText.getText().toString());
                                startDayTextView.setText(DateUtil.convertDateToUTCDate(AddActivity.this, startDay));
                                endDayTextView.setText(DateUtil.convertDateToUTCDate(AddActivity.this, endDay));
                                String daysString = must.days + " " + getString(R.string.preview_days_unit);
                                daysTextView.setText(daysString);
                                depositTextView.setText(depositArray[depositSelected]);

                                String pointString = "" + must.default_point;
                                defaultPointTextView.setText(pointString);

                            }
                        });
                    }
                }
            }.start();
        }

        Date startDate = new Date();

        View.OnClickListener dayClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (view.getId() == R.id.add_start_day_picker_button) {
                    startDate = new Date();
                    endDay = null;
                    endDayButton.setText(R.string.add_datepicker);
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);

                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        inputMethodManager.hideSoftInputFromWindow(addTitleEditText.getWindowToken(), 0);
                        switch (view.getId()) {
                            case R.id.add_start_day_picker_button:
                                startDayButton.setText("" + year + "-" + (month + 1) + "-" + day);
                                startDay = DateUtil.getStartDateStringWithYearAndMonthAndDay(AddActivity.this, year, month, day);

                                // Set min Date to start Date
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(startDate);
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);
                                startDate.setTime(calendar.getTimeInMillis());

                                break;

                            case R.id.add_end_day_picker_button:
                                endDayButton.setText("" + year + "-" + (month + 1) + "-" + day);
                                endDay = DateUtil.getEndDateStringWithYearAndMonthAndDay(AddActivity.this, year, month, day);
                                break;

                            default:
                                Log.d(TAG, "onDateSet: Error!!");
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(startDate.getTime());
                calendar.add(Calendar.DAY_OF_MONTH, 29);
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
                                AlertUtil.alertWithCancel(AddActivity.this, R.string.alert_not_deposit, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addMustAndFinish();
                                    }
                                });
                            } else {
                                billing();
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
