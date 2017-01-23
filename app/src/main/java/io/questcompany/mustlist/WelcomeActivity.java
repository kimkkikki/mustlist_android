package io.questcompany.mustlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.iid.FirebaseInstanceId;

import io.questcompany.mustlist.entity.User;
import io.questcompany.mustlist.manager.NetworkManager;
import io.questcompany.mustlist.util.AlertUtil;
import io.questcompany.mustlist.util.PrefUtil;
import io.questcompany.mustlist.util.Singleton;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private int[] layouts;

    private static final int GOOGLE_SIGN_IN = 8765;

    private ProgressDialog loadingDialog;
    private boolean firstTime = true;

    private void getUserInformation(final User requestUser) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                User user;
                if(!PrefUtil.isUser(WelcomeActivity.this)) {
                    String FCMToken = FirebaseInstanceId.getInstance().getToken();
                    Log.d(TAG, "onCreate: FCM Token : " + FCMToken);

                    requestUser.device_id = FCMToken;
                    user = NetworkManager.postUser(WelcomeActivity.this, requestUser);
                    if (user != null) {
                        PrefUtil.setUser(WelcomeActivity.this, user);
                        Log.d(TAG, "user: " + user);
                    }
                } else {
                    user = PrefUtil.getUser(WelcomeActivity.this);
                    Log.d(TAG, "is registered user : " + user);
                }

                if (!NetworkManager.checkNetworkStatus(WelcomeActivity.this)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertUtil.alert(WelcomeActivity.this, R.string.alert_not_connected_network);
                        }
                    });
                } else {
                    if (user != null) {
                        Singleton singleton = Singleton.getInstance();
                        singleton.setIdAndKey(user.id, user.key);

                        User getUser = NetworkManager.getUser(WelcomeActivity.this);
                        if (getUser != null) {
                            singleton.setUser(getUser);
                        } else {
                            PrefUtil.clear(WelcomeActivity.this);
                            singleton.setIdAndKey(null, null);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goMain();
                        }
                    });
                }

                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();

            }
        }.start();
    }

    private void goMain() {
        Log.i(TAG, "goMain: start");
        Singleton singleton = Singleton.getInstance();
        if (singleton.getUser() != null) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        } else {
            AlertUtil.alert(this, R.string.alert_login_fail);
            Log.i(TAG, "goMain: Failed : " + singleton.getUser());
        }
    }

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + ", " + user.getEmail());

                User mustlist_user = new User(user.getUid(), user.getEmail());
                getUserInformation(mustlist_user);
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                if (loadingDialog.isShowing())
                    loadingDialog.dismiss();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog.isShowing())
            loadingDialog.dismiss();
    }

    private void initializeUser() {
        if (PrefUtil.getUser(WelcomeActivity.this) != null) {
            if (!loadingDialog.isShowing())
                loadingDialog.show();
        }

        firebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        loadingDialog = AlertUtil.getLoadingDialog(this);

        loadingDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                final int code = NetworkManager.checkVersion(WelcomeActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (code) {
                                case 200:
                                    initializeUser();
                                    break;
                                case 205:
                                case 206:
                                    AlertUtil.alertWithCancel(WelcomeActivity.this, R.string.update_select,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // OK
                                                    final String appPackageName = getPackageName();
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException e) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                    firstTime = false;
                                                }
                                            },
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Cancel
                                                    initializeUser();
                                                }
                                            });
                                    loadingDialog.dismiss();
                                    break;
                                default:
                                    AlertUtil.alert(WelcomeActivity.this, R.string.alert_not_connect_server);
                                    loadingDialog.dismiss();
                            }
                        }
                    });
            }
        }.start();

        ViewPager viewPager = (ViewPager) findViewById(R.id.welcome_view_pager);
        layouts = new int[]{
                R.layout.welcome_slide_1,
                R.layout.welcome_slide_2,
                R.layout.welcome_slide_3,
                R.layout.welcome_slide_4};


        WelcomeViewPagerAdapter welcomeViewPagerAdapter = new WelcomeViewPagerAdapter();

        if (viewPager != null) {
            viewPager.setAdapter(welcomeViewPagerAdapter);
        } else {
            Log.e(TAG, "welcomeViewPager is null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstTime) {
            initializeUser();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            if (firebaseAuth != null) {
                firebaseAuth.removeAuthStateListener(authStateListener);
            }
        }
    }

    private void authFailureCheck(Task<AuthResult> task) {
        Log.d(TAG, "authFailureCheck : " + task.isSuccessful());
        if (!task.isSuccessful()) {
            Log.w(TAG, "authFailureCheck, exception : ", task.getException());
            Toast.makeText(WelcomeActivity.this, R.string.alert_sign_in_fail, Toast.LENGTH_SHORT).show();

            if (loadingDialog.isShowing())
                loadingDialog.dismiss();
        }
    }

    // With out Sign in
    private void startWithoutSignIn() {
        firebaseAuth.signInAnonymously().addOnCompleteListener(WelcomeActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    authFailureCheck(task);
                }
            }
        });
    }

    // Google +
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    // Facebook
    CallbackManager callbackManager;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google Callback
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        authFailureCheck(task);
                    }
                });
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        authFailureCheck(task);
                    }
                });
    }


    private class WelcomeViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Button startGuestButton;
        private Button signWithGoogleButton;
        private LoginButton signWithFacebookButton;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            if (startGuestButton == null) {
                startGuestButton = (Button) findViewById(R.id.welcome_start_button);
                if (startGuestButton != null) {
                    startGuestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (NetworkManager.checkNetworkStatus(WelcomeActivity.this)) {
                                loadingDialog.show();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        startWithoutSignIn();
                                    }
                                }.start();
                            } else {
                                AlertUtil.alert(WelcomeActivity.this, R.string.alert_not_connected_network);
                            }
                        }
                    });
                }
            }

            if (signWithGoogleButton == null) {
                signWithGoogleButton = (Button) findViewById(R.id.sign_in_with_google_button);
                if (signWithGoogleButton != null) {
                    signWithGoogleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetworkManager.checkNetworkStatus(WelcomeActivity.this)) {
                                loadingDialog.show();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                            startWithGoogle();
                                    }
                                }.start();
                            } else {
                                AlertUtil.alert(WelcomeActivity.this, R.string.alert_not_connected_network);
                            }
                        }
                    });
                }
            }

            if (signWithFacebookButton == null) {
                signWithFacebookButton = (LoginButton) findViewById(R.id.sign_in_with_facebook);
                if (signWithFacebookButton != null) {
                    signWithFacebookButton.setReadPermissions("email", "public_profile");
                    signWithFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d(TAG, "Facebook Callback : Login Success");
                            loadingDialog.show();
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            Log.d(TAG, "Facebook Callback : Login Cancel");
                            Toast.makeText(WelcomeActivity.this, R.string.alert_sign_in_fail, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d(TAG, "Facebook Callback : Login Error");
                            Toast.makeText(WelcomeActivity.this, R.string.alert_sign_in_fail, Toast.LENGTH_SHORT).show();
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
