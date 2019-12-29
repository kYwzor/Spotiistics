
package com.example.spotiistics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.room.Room;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.spotiistics.Database.AppDatabase;
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
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        api = new SpotifyApi();
        spotify = api.getService();
        loginButton = findViewById(R.id.login);
    }

    public void onGetUserProfileClicked(View view) {
        loginButton.setEnabled(false);
        loginButton.setBackground(getResources().getDrawable(R.drawable.button_clicked_background));
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
                    database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "appdb")
                            .allowMainThreadQueries()
                            .build();
                    getUser();
                }
                else {
                    Toast.makeText(MainActivity.this, "Couldn't login (Wrong request code)", Toast.LENGTH_LONG).show();
                }

                break;
            case ERROR:

                break;
            default:
                String error = response.getError();
                if(error!=null) Log.e(TAG, error);
                Toast.makeText(MainActivity.this, "Couldn't login, please retry", Toast.LENGTH_LONG).show();
                loginButton.setEnabled(true);
                loginButton.setBackground(getResources().getDrawable(R.drawable.button_background));
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
                if(error.hasErrorDetails()){
                    Log.e(TAG, error.getErrorDetails().message);
                }
                Toast.makeText(getApplicationContext(), "Failure getting user data", Toast.LENGTH_LONG).show();
            }
        });
    }
}
