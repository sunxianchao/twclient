package com.phonegame.facebook;


import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.LoginActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;

public class FBLoginActivity extends Activity {

    private static final List<String> PERMISSIONS=Arrays.asList("publish_stream");

    /*private Session.StatusCallback statusCallback=new SessionStatusCallback();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session=Session.getActiveSession();
        if(session == null) {
            if(savedInstanceState != null) {
                session=Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if(session == null) {
                session=new Session(this);
            }
            Session.setActiveSession(session);
            if(session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }

        if(!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(FBLoginActivity.this, true, statusCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        Session session=Session.getActiveSession();
        if(session != null && !session.isOpened()) {
            Toast.makeText(this, "取消登录", Toast.LENGTH_LONG).show();
            Log.i("efunLog", "用户取消登入");
            Intent intent=new Intent(this, LoginActivity.class);
            this.startActivity(intent);
            this.finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Session session=Session.getActiveSession();
        if(!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    private void executeMeRequestAsync(Session session) {
        Session.NewPermissionsRequest newPermissionsRequest=new Session.NewPermissionsRequest(this, PERMISSIONS);
        session.requestNewPublishPermissions(newPermissionsRequest);
        session.addCallback(statusCallback);
        
        Request.newMeRequest(session, new Request.GraphUserCallback(){
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if(user != null) {
                    Toast.makeText(FBLoginActivity.this, user.getId()+"\t"+user.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FBLoginActivity.this, "獲取用戶資料失敗，請重新登錄", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(FBLoginActivity.this, LoginActivity.class);
                    FBLoginActivity.this.startActivity(intent);
                    finish();
                }

            }
        }).executeAsync();
    }

    private void updateView() {
        Session session=Session.getActiveSession();
        if(session.isOpened()) {
            executeMeRequestAsync(session);
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {

        @Override
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent=new Intent(FBLoginActivity.this, LoginActivity.class);
            FBLoginActivity.this.startActivity(intent);
            finish();
            return true;
        }
        return false;
    }*/
}
