package io.questcompany.mustlist;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import io.questcompany.mustlist.manager.NetworkManager;
import io.questcompany.mustlist.util.AlertUtil;
import io.questcompany.mustlist.util.PrefUtil;

/**
 * Created by kimkkikki on 2017. 1. 16..
 * Setting Activity
 */

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    public static final int SETTING_ACTIVITY_CODE = 7333;
    public static final int SETTING_LOGOUT_RESULT = 1004;

    private void closeActivitiesAndGoHome() {
        startActivity(new Intent(SettingActivity.this, WelcomeActivity.class));
        setResult(SETTING_LOGOUT_RESULT);
        finish();
    }

    private ProgressDialog loadingDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        loadingDialog = AlertUtil.getLoadingDialog(this);

        Button logout = (Button) findViewById(R.id.setting_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertUtil.alertWithCancel(SettingActivity.this, R.string.alert_logout_question, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        PrefUtil.clear(SettingActivity.this);
                        closeActivitiesAndGoHome();
                    }
                });
            }
        });

        Button leave = (Button) findViewById(R.id.setting_leave);
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertUtil.alertWithCancel(SettingActivity.this, R.string.alert_leave_question, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            Log.d(TAG, "onClick: user : " + user.getProviderId());
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        PrefUtil.clear(SettingActivity.this);
                                        closeActivitiesAndGoHome();
                                    } else {
                                        Toast.makeText(SettingActivity.this, "Leave Failed", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onComplete: error : " + task.getException());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        Button feedback = (Button) findViewById(R.id.setting_feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "donggeun.kim@questcompany.io", null));
                startActivity(Intent.createChooser(email, "Send email..."));
            }
        });

        initializeAccountButtons();
    }

    // Firebase
    FirebaseUser user;

    // Google +
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private static final int GOOGLE_SIGN_IN = 8765;

    // Facebook
    CallbackManager callbackManager;
    Button googleButton;
    LoginButton facebookButton;

    private void linkCredential(AuthCredential credential) {
        user.linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "linkWithCredential:onComplete:" + task.isSuccessful());

                if (task.isSuccessful()) {
                    Log.d(TAG, "linkWithCredential: success");
                    AlertUtil.alert(SettingActivity.this, R.string.alert_account_link_success);
                    googleButton.setEnabled(false);
                    facebookButton.setEnabled(false);

                } else {
                    Log.d(TAG, "linkWithCredential: failure");
                    AlertUtil.alert(SettingActivity.this, R.string.alert_account_link_failure);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google Callback
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                linkCredential(credential);
            } else {
                Log.d(TAG, "onActivityResult: Fail " + result.getStatus());
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
            }

        } else {
            // Facebook Callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startWithGoogle() {
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_oauth2_client_id))
                    .requestEmail()
                    .build();
        }

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        linkCredential(credential);
    }

    private void initializeAccountButtons() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        user = FirebaseAuth.getInstance().getCurrentUser();
        googleButton = (Button) findViewById(R.id.setting_sign_in_with_google_button);
        facebookButton = (LoginButton) findViewById(R.id.setting_sign_in_with_facebook);
        if (user != null) {
            if (user.isAnonymous()) {
                googleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkManager.checkNetworkStatus(SettingActivity.this)) {
                            startWithGoogle();
                            if (loadingDialog.isShowing())
                                loadingDialog.dismiss();
                        } else {
                            AlertUtil.alert(SettingActivity.this, R.string.alert_not_connected_network);
                        }
                    }
                });

                facebookButton.setReadPermissions("email", "public_profile");
                facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Facebook Callback : Login Success");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        if (loadingDialog.isShowing())
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Facebook Callback : Login Cancel");
                        Toast.makeText(SettingActivity.this, R.string.alert_sign_in_fail, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "Facebook Callback : Login Error");
                        Toast.makeText(SettingActivity.this, R.string.alert_sign_in_fail, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                googleButton.setEnabled(false);
                facebookButton.setEnabled(false);
            }
        }
    }
}
