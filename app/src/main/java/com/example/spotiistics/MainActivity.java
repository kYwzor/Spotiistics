
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
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(MainActivity.this, "Default", Toast.LENGTH_LONG).show();
                break;
        }

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(mAccessToken);
        SpotifyService spotify = api.getService();

        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                Intent mIntent = new Intent(MainActivity.this, PlaylistsActivity.class);
                mIntent.putExtra("token", mAccessToken);
                mIntent.putExtra("user", userPrivate);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
            }

            @Override
            public void failure(SpotifyError error) {
                Toast.makeText(MainActivity.this, "Me failure", Toast.LENGTH_LONG).show();
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
