/*
 * Copyright (C) 2012 The Android Open Source Project Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License.
 */

package com.phonegame.google.plus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.phonegame.google.plus.PlusClientFragment.OnSignedInListener;
import com.phonegame.util.CommonUtil;
import com.phonegame.util.Logger;

/**
 * Example of signing in a user with Google+, and how to make a call to a Google+ API endpoint.
 */
public class GoogleSignInActivity extends FragmentActivity implements OnSignedInListener {

    public static final int REQUEST_CODE_PLUS_CLIENT_FRAGMENT=0;

    private PlusClientFragment mSignInFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(CommonUtil.getResourceId(this, "layout", "sign_in_activity"));
        mSignInFragment=PlusClientFragment.getPlusClientFragment(this, MomentUtil.VISIBLE_ACTIVITIES);
        SignInButton b=(SignInButton)findViewById(CommonUtil.getResourceId(this, "id", "sign_in_button"));
        b.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mSignInFragment.signIn(REQUEST_CODE_PLUS_CLIENT_FRAGMENT);
            }
        });
        b.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        mSignInFragment.handleOnActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void onSignedIn(PlusClient plusClient) {
        Person currentPerson=plusClient.getCurrentPerson();
        if(currentPerson != null) {
            System.out.println(currentPerson);
        } 
    }
}