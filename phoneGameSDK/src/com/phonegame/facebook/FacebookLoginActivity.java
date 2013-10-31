package com.phonegame.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.phonegame.util.CommonUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

@Deprecated
public class FacebookLoginActivity extends Activity {

    private static final String[] PERMISSIONS=new String[]{"email", "publish_actions"};

    private static Facebook mFacebook;

    private static AsyncFacebookRunner asyncRunner;

    private Bundle bundle;

    private Intent intent;

    private static Context ctx;

    private static FacebookLoginActivity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle=new Bundle();
        intent=this.getIntent();
        mFacebook=new Facebook(this.getString(CommonUtil.getResourceId(this, "string", "applicationId")));
        asyncRunner=new AsyncFacebookRunner(mFacebook);
        login();
        SessionStore.restore(mFacebook, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    // ----------------------------------------------
    // loginButtonListener
    // ----------------------------------------------
    public void login() {
        if(!mFacebook.isSessionValid()) {
            Log.v("FB", "Authorizing");
            mFacebook.authorize(FacebookLoginActivity.this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
        } else {
            Log.v("FB", "Has valid session");
            asyncRunner.request("me", new LoginRequestListener());
        }
    }

    public static FacebookLoginActivity getInstance(Context ctx) {
        if(instance == null) {
            instance=new FacebookLoginActivity();
        }
        FacebookLoginActivity.ctx=ctx;
        return instance;
    }

    public void logout() {
        asyncRunner.logout(FacebookLoginActivity.ctx, new RequestListener() {

            @Override
            public void onMalformedURLException(MalformedURLException e, Object state) {
            }

            @Override
            public void onIOException(IOException e, Object state) {
            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state) {
            }

            @Override
            public void onFacebookError(FacebookError e, Object state) {
            }

            @Override
            public void onComplete(String response, Object state) {
                SessionStore.clear(FacebookLoginActivity.ctx);
            }
        });
    }

    // ***********************************************************************
    // ***********************************************************************
    // LoginDialogListener
    // ***********************************************************************
    // ***********************************************************************
    public final class LoginDialogListener implements DialogListener {

        public void onComplete(Bundle values) {
            asyncRunner.request("me", new LoginRequestListener());
            SessionStore.save(mFacebook, FacebookLoginActivity.this);
        }

        public void onFacebookError(FacebookError error) {
            error();
            Toast.makeText(FacebookLoginActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
        }

        public void onError(DialogError error) {
            error();
            Toast.makeText(FacebookLoginActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
        }

        public void onCancel() {
            error();
            Toast.makeText(FacebookLoginActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    public final class LoginRequestListener implements RequestListener {

        @Override
        public void onComplete(String response, Object state) {
            try {
                JSONObject json=Util.parseJson(response);
                bundle.putString("code", "1");
                bundle.putString("id", json.get("id").toString());
                if(json.has("email")) {
                    bundle.putString("email", json.get("email").toString());
                }
                if(json.has("name")) {
                    bundle.putString("name", json.get("name").toString());
                }
                if(json.has("link")) {
                    bundle.putString("link", json.get("link").toString());
                }
                intent.putExtras(bundle);
                FacebookLoginActivity.this.setResult(Activity.RESULT_OK, intent);
            } catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(FacebookLoginActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
            } finally {
                finish();
            }

        }

        @Override
        public void onIOException(IOException e, Object state) {
            error();
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e, Object state) {
            error();
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            error();
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            error();
        }
    }

    private void error() {
        bundle.putString("code", "0");
        FacebookLoginActivity.this.setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        com.facebook.Settings.publishInstallAsync(this, this.getString(CommonUtil.getResourceId(this, "string", "applicationId")));
    }

}