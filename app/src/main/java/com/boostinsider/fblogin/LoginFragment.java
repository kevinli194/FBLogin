package com.boostinsider.fblogin;

import com.boostinsider.fblogin.RetroFitAPI.ServerEndPointInterface;
import com.boostinsider.fblogin.RetroFitAPI.models.FBPost;
import com.boostinsider.fblogin.RetroFitAPI.models.FBReturn;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//Facebook imports
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

//Retrofit imports
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

//Twitter imports.
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {
    //private TextView mTextDisplayed;

    // List of endpoints to test
    private static final String WORKING_URL = "http://posttestserver.com";
    private static final String TEST_URL = "http://52.11.39.63:3008";
    private static final String WORKING_BOOST = "http://199.217.119.199:3009";

    // Facebook objects.
    private LoginButton fbLoginButton;
    private CallbackManager mCallbackManager;
    // Callback that responds based on callback from Facebook Login attempt.
    private FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                //loopJ(accessToken, "Testing FB Post via Android.");
                retroFit(accessToken, "Testing FB Post.");

            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };
    //Twitter objects.
    private TwitterLoginButton twitterLoginButton;
    private Callback<TwitterSession> twitterSessionCallback = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {

        }

        @Override
        public void failure(TwitterException e) {

        }
    };

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the SDK of FB and constructing the callbackmanager
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mTextDisplayed = (TextView) view.findViewById(R.id.text_details);

        // Create facebook button and initialize permissions.
        fbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        fbLoginButton.setPublishPermissions("publish_actions");
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(mCallbackManager, facebookCallback);

        //Creating loginButton and setting the callback.
        twitterLoginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(twitterSessionCallback);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //loginButton.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Sends a fb message to the backend using the RetroFit library.
     * @param token access token generated on login by FB
     * @param post message to be posted on FB
     */
    private void retroFit(AccessToken token, String post) {
        FBPost fBPost = new FBPost();
        fBPost.setToken(token.getToken());
        fBPost.setMessage(post);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TEST_URL)
                .build();
        System.out.println("Broken: 1");
        ServerEndPointInterface apiService =
                restAdapter.create(ServerEndPointInterface.class);
        System.out.println("Broken: 2");
        apiService.postMessage(fBPost, new retrofit.Callback<FBReturn>() {
            @Override
            public void success(FBReturn result, Response response) {
                makeToast(result.getMessage() + " " + result.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                String url = error.getUrl();
                System.out.println(url);
                System.out.println(error.getMessage());
                makeToast("Failed: " + url );
            }
        });
    }

    /**
     * Makes a toast based on input message.
     * @param message
     */
    private void makeToast(String message) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }


}
