package io.questcompany.mustlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

import io.questcompany.mustlist.entity.Must;
import io.questcompany.mustlist.util.DateUtil;
import io.questcompany.mustlist.util.IabBroadcastReceiver;
import io.questcompany.mustlist.util.IabHelper;
import io.questcompany.mustlist.util.IabResult;
import io.questcompany.mustlist.util.Inventory;
import io.questcompany.mustlist.util.NetworkManager;
import io.questcompany.mustlist.util.Purchase;

/**
 * Created by kimkkikki on 2016. 9. 28..
 * Add Activity
 */

public class AddActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {

    private static final String TAG = "AddActivity";
    private int[] layouts;
    private InputMethodManager inputMethodManager;
    private ViewPager viewPager;

    private int startDaySelected = 0;
    private int periodSelected = 0;
    private int amountSelected = 0;
    private int timeRangeSelected = 0;
    private int pageOffset = 0;

    private String[] startDayArray;
    private String[] periodArray;
    private String[] amountArray;
    private String[] timeRangeArray;
    private String[] inAppPurchaseItemArray;

    private Must must;

    private IabHelper iabHelper;
    private IabBroadcastReceiver iabBroadcastReceiver;
    private static final String iabPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt8qugRocMakutggvaHsDgbP+77uWlRx2L2oWRbQx7o3PfcGLA0vnMaUaSTIyCUWODl0dgk3t2lCc4ksPygGIh+liLbEUaHC39KJ9uwjc1WsepY2XSP98W71x9yNMTTQ5EGMephVOgIDISE0GIwcVlPwe06AZ9aHF+sP9TCtj9OojpvgdlZ/8LTUsZvhQA1doKhOaW+poX4neTUpOSzSS/AemRyig+nUm/fSYdtRTfCrlMNQkCJmmh7Tq5e1nq2UqcDVATH/h+YeNtjFuOTQJ6BAegnktKu+VPJEqfdRnkBmiKzlVQO/NuetNMH43FAgxZLdRbe7c/bjj+bad4TlwkQIDAQAB";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

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
        timeRangeArray = getResources().getStringArray(R.array.time_range);
        inAppPurchaseItemArray = getResources().getStringArray(R.array.in_app_purchase_items);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (iabBroadcastReceiver != null) {
            unregisterReceiver(iabBroadcastReceiver);
        }

        Log.d(TAG, "Destroying helper.");
        if (iabHelper != null) {
            iabHelper.disposeWhenFinished();
            iabHelper = null;
        }
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "receivedBroadcast: !!");
    }

    // 결제 진행
    private void billing() {
        Log.d(TAG, "Creating IAB helper.");
        iabHelper = new IabHelper(AddActivity.this, iabPublicKey);
        iabHelper.enableDebugLogging(true);

        Log.d(TAG, "Starting setup.");
        iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (iabHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                iabBroadcastReceiver = new IabBroadcastReceiver(AddActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(iabBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    iabHelper.queryInventoryAsync(iabGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e(TAG, "Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    // 보유 상품 목록 조회 (기 결제 상품)
    IabHelper.QueryInventoryFinishedListener iabGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (iabHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");
            Log.d(TAG, "inventory : " + inventory.getSkuDetails("mustlist_amount_0"));

            /** TODO: for security, generate your payload here for verification. See the comments on
             *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
             *        an empty string, but on a production app you should carefully generate this. */
            String payload = "123412345123";

            try {
                iabHelper.launchPurchaseFlow(AddActivity.this, inAppPurchaseItemArray[amountSelected], RC_REQUEST, iabPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.d(TAG, "Error launching purchase flow. Another async operation in progress.");
            }
        }
    };

    // 구매 완료 리스너
    IabHelper.OnIabPurchaseFinishedListener iabPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (iabHelper == null) return;

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                Log.d(TAG, "Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            try {
                iabHelper.consumeAsync(purchase, iabConsumeFinishedListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.d(TAG, "Error consuming gas. Another async operation in progress.");
            }
        }
    };

    // 구매한 제품을 소진 시킴
    IabHelper.OnConsumeFinishedListener iabConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (iabHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");

                // 소진이 완료되면 서버에 전송
                addMust();
            }
            else {
                Log.d(TAG, "Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        /**
         *  TODO: 위의 그림에서 설명하였듯이 로컬 저장소 또는 원격지로부터 미리 저장해둔 developerPayload값을 꺼내 변조되지 않았는지 여부를 확인합니다.
         *
         * 이 payload의 값은 구매가 시작될 때 랜덤한 문자열을 생성하는것은 충분히 좋은 접근입니다.
         * 하지만 두개의 디바이스를 가진 유저가 하나의 디바이스에서 결제를 하고 다른 디바이스에서 검증을 하는 경우가 발생할 수 있습니다.
         * 이 경우 검증을 실패하게 될것입니다. 그러므로 개발시에 다음의 상황을 고려하여야 합니다.
         *
         * 1. 두명의 유저가 같은 아이템을 구매할 때, payload는 같은 아이템일지라도 달라야 합니다.
         *    두명의 유저간 구매가 이어져서는 안됩니다.
         *
         * 2. payload는 앱을 두대를 사용하는 유저의 경우에도 정상적으로 동작할 수 있어야 합니다.
         *    이 payload값을 저장하고 검증할 수 있는 자체적인 서버를 구축하는것을 권장합니다.
         */

        return true;
    }

    // 서버에 Mustlist 추가
    private void addMust() {
        NetworkManager.addMust(AddActivity.this ,must);

        AlertDialog.Builder alert = new AlertDialog.Builder(AddActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        alert.setMessage("등록되었습니다");
        alert.show();
    }

    private class AddViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private EditText addTitleEditText;
        private Spinner startDaySpinner;
        private Spinner periodSpinner;
        private Spinner amountSpinner;
        private Spinner timeRangeSpinner;
        private Button nextButton;
        private Button okButton;

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

        private void goPreview() {
            // 서버에 프리뷰 전달
            if (must == null) {
                must = new Must();
            }

            must.setName(addTitleEditText.getText().toString());
            must.setStartDate(DateUtil.getDateStringWithDay(startDaySelected));
            must.setEndDate(DateUtil.getDateStringWithWeek(periodSelected));
            must.setCheckTimeRange(timeRangeArray[timeRangeSelected]);
            must.setAmount(amountSelected);

            Must serverReceivedData = NetworkManager.previewAddMust(AddActivity.this, must);
            if (serverReceivedData != null) {
                must = serverReceivedData;
                pageOffset += 1;
                viewPager.setCurrentItem(pageOffset, true);

                TextView nameTextView = (TextView) findViewById(R.id.preview_name);
                TextView startDayTextView = (TextView) findViewById(R.id.preview_start_day);
                TextView periodTextView = (TextView) findViewById(R.id.preview_period);
                TextView amountTextView = (TextView) findViewById(R.id.preview_amount);
                TextView timeRangeTextView = (TextView) findViewById(R.id.preview_time_range);
                TextView defaultPointTextView = (TextView) findViewById(R.id.preview_default_point);
                TextView successPointTextView = (TextView) findViewById(R.id.preview_success_point);

                nameTextView.setText(addTitleEditText.getText().toString());
                startDayTextView.setText(startDayArray[startDaySelected]);
                periodTextView.setText(periodArray[periodSelected] + "(" + must.getStartDate() + "~" + must.getEndDate() + ")");
                amountTextView.setText(amountArray[amountSelected]);
                timeRangeTextView.setText(timeRangeArray[timeRangeSelected]);

                defaultPointTextView.setText(String.format(Locale.KOREA, "%d", must.getDefaultPoint()));
                successPointTextView.setText(String.format(Locale.KOREA, "%d", must.getSuccessPoint()));
            }
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
                            goPreview();
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

            if (timeRangeSpinner == null) {
                timeRangeSpinner = (Spinner) findViewById(R.id.add_time_range_spinner);
                if (timeRangeSpinner != null) {
                    timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            timeRangeSelected = i;
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
                            billing();
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
