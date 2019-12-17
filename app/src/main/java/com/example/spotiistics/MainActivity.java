/*
 * Copyright (c) 2015-2018 Spotify AB
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.example.spotiistics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;

import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String CLIENT_ID = "31ba52256ea04bad96190373ecbfdfb1";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;

    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void onGetUserProfileClicked(View view) {
        final AuthenticationRequest requestToken = getAuthenticationRequest(AuthenticationResponse.Type.TOKEN);
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, requestToken);
    }

    private AuthenticationRequest getAuthenticationRequest(AuthenticationResponse.Type type) {
        return new AuthenticationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        switch (response.getType()) {
            case TOKEN:
                if (AUTH_TOKEN_REQUEST_CODE == requestCode)
                    mAccessToken = response.getAccessToken();
                break;
            case ERROR:
                Log.e(TAG, "Error: " + response.getError());
                break;
            default:
                Log.e(TAG, "Default");
                break;
        }

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        SpotifyService spotify = api.getService();

        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                Intent mIntent = new Intent(MainActivity.this, ProfileActivity.class);
                mIntent.putExtra("token", mAccessToken);
                mIntent.putExtra("user", userPrivate);
                startActivity(mIntent);
            }

            @Override
            public void failure(SpotifyError error) {
                Log.e("Me failure", error.toString());
            }
        });
    }



    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }
}
