
package com.example.spotiistics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String CLIENT_ID = "31ba52256ea04bad96190373ecbfdfb1";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void onGetUserProfileClicked(View view) {
        Uri uri = new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
        final AuthenticationRequest requestToken = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, uri.toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, requestToken);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

        switch (response.getType()) {
            case TOKEN:
                if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
                    Intent mIntent = new Intent(MainActivity.this, PlaylistsActivity.class);
                    mIntent.putExtra("token", response.getAccessToken());
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Wrong request code", Toast.LENGTH_LONG).show();
                }

                break;
            case ERROR:
                Log.e(TAG, response.getError());
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(TAG, response.toString());
                Toast.makeText(MainActivity.this, "Default", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
