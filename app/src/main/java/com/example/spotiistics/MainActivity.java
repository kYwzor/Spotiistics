
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
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;


public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String CLIENT_ID = "31ba52256ea04bad96190373ecbfdfb1";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    SpotifyApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        api = new SpotifyApi();
        spotify = api.getService();
    }

    public void onGetUserProfileClicked(View view) {
        Uri uri = new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
        final AuthenticationRequest requestToken = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, uri.toString())
                .setShowDialog(false)
                .setScopes(new String[]{"playlist-read-collaborative", "user-read-private", "playlist-read-private", "user-follow-read", "user-library-read"})
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
                    api.setAccessToken(response.getAccessToken());
                    getUser();
                }
                else {
                    Toast.makeText(MainActivity.this, "Couldn't login (Wrong request code)", Toast.LENGTH_LONG).show();
                }

                break;
            case ERROR:
                Log.e(TAG, response.getError());
                Toast.makeText(MainActivity.this, "Couldn't login (Error)", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e(TAG, response.getState());
                Toast.makeText(MainActivity.this, "Couldn't login (Default)", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void getUser(){
        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                user = userPrivate;
                Intent mIntent = new Intent(MainActivity.this, UserPlaylistsActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
            }

            @Override
            public void failure(SpotifyError error) {
                //TODO should we retry?
                if(error.hasErrorDetails()){
                    Log.e(TAG, error.getErrorDetails().message);
                }
                Toast.makeText(getApplicationContext(), "Failure getting user data", Toast.LENGTH_LONG).show();
            }
        });
    }
}
