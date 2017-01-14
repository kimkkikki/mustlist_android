package io.questcompany.mustlist.manager;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import io.questcompany.mustlist.R;
import io.questcompany.mustlist.util.IabBroadcastReceiver;
import io.questcompany.mustlist.util.IabHelper;
import io.questcompany.mustlist.util.IabResult;
import io.questcompany.mustlist.util.Inventory;
import io.questcompany.mustlist.util.Purchase;

/**
 * Created by kimkkikki on 2017. 1. 10..
 * In-App Purchase Manager
 */

public class BillingManager implements IabBroadcastReceiver.IabBroadcastListener {
    private static final String TAG = "BillingManager";

    private Context context;

    private IabHelper iabHelper;
    private IabBroadcastReceiver iabBroadcastReceiver;
    private static final String iabPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt8qugRocMakutggvaHsDgbP+77uWlRx2L2oWRbQx7o3PfcGLA0vnMaUaSTIyCUWODl0dgk3t2lCc4ksPygGIh+liLbEUaHC39KJ9uwjc1WsepY2XSP98W71x9yNMTTQ5EGMephVOgIDISE0GIwcVlPwe06AZ9aHF+sP9TCtj9OojpvgdlZ/8LTUsZvhQA1doKhOaW+poX4neTUpOSzSS/AemRyig+nUm/fSYdtRTfCrlMNQkCJmmh7Tq5e1nq2UqcDVATH/h+YeNtjFuOTQJ6BAegnktKu+VPJEqfdRnkBmiKzlVQO/NuetNMH43FAgxZLdRbe7c/bjj+bad4TlwkQIDAQAB";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    private String[] inAppPurchaseItemArray;

    public BillingManager(Context context) {
        this.context = context;
        inAppPurchaseItemArray = context.getResources().getStringArray(R.array.in_app_purchase_items);
    }

    public void destroy() {
        if (iabBroadcastReceiver != null) {
            context.unregisterReceiver(iabBroadcastReceiver);
        }

        Log.d(TAG, "Destroying helper.");
        if (iabHelper != null) {
            iabHelper.disposeWhenFinished();
            iabHelper = null;
        }
    }

    // 결제 진행
    private void billing() {
        Log.d(TAG, "Creating IAB helper.");
        iabHelper = new IabHelper(context, iabPublicKey);
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
                iabBroadcastReceiver = new IabBroadcastReceiver(BillingManager.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                context.registerReceiver(iabBroadcastReceiver, broadcastFilter);

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
            String payload = NetworkManager.getPayload(context);

//            try {
                //TODO : 임시로 서버 저장도 넣어놓음
//                must.setDeveloperPayload(payload);
//                addMust();
//                iabHelper.launchPurchaseFlow(context, inAppPurchaseItemArray[amountSelected], RC_REQUEST, iabPurchaseFinishedListener, payload);
//            } catch (IabHelper.IabAsyncInProgressException e) {
//                Log.d(TAG, "Error launching purchase flow. Another async operation in progress.");
//            }
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
//                NetworkManager.purchaseSuccess(AddActivity.this, purchase);
//                addMust();
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

//        must.setDeveloperPayload(payload);

        return true;
    }


    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "receivedBroadcast: !!");
    }
}
