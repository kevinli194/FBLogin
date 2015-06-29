package com.boostinsider.fblogin;

import com.boostinsider.fblogin.OAuthAPI.Constants.TumblrConstants;
import com.boostinsider.fblogin.OAuthAPI.OAuth;
import com.boostinsider.fblogin.RetroFitAPI.ServerEndPointInterface;
import com.boostinsider.fblogin.RetroFitAPI.models.fBModel;
import com.boostinsider.fblogin.RetroFitAPI.models.serverReturn;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.boostinsider.fblogin.RetroFitAPI.models.twitterModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.wuman.android.auth.OAuthManager;

import java.io.IOException;


/**
 * Fragment that contains gives users the capability to login from different social media websites. Currently supports: Twitter and Facebook.
 */
public class LoginFragment extends Fragment {
    private CallbackManager mCallbackManager;
    //private TextView mTextDisplayed;

    // List of endpoints to test
    private static final String TEST_URL = "http://52.11.39.63:3008";
    // Test messages
    private static final String TEST_MESSAGE = "Second Test Message";

    //Secret and Consumer Keys
    private static final String TWITTER_KEY = "2JGw8IRjGhlRCfVloFuiuFfCe";
    private static final String TWITTER_SECRET = "EPm5nbmIo4vKXMGyQLOFtK0wv9Aeu5QVxuLpNCkILLGh6XaZEq";

    // Internal Variables for Facebook Integration
    private LoginButton fBLogin;
    private FacebookCallback<LoginResult> fBCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            makeToast("Login Successful");
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if (profile != null) {
                postFB(accessToken, TEST_MESSAGE);

            }
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    //Internal variables for Twitter Integration
    private TwitterLoginButton twitterLogin;
    private Callback<TwitterSession> twitterCallback = new Callback<TwitterSession>() {
        @Override
        public void success(Result<TwitterSession> result) {
            makeToast("Login Successful");
            TwitterSession session =
                    Twitter.getSessionManager().getActiveSession();
            TwitterAuthToken authToken = session.getAuthToken();
            String token = authToken.token;
            String secret = authToken.secret;
            makeToast(token + secret);
            postTwitter(token, secret, TEST_MESSAGE);
        }

        @Override
        public void failure(TwitterException e) {
            makeToast("Login Failed");
        }
    };

    //Internal variables for Tumblr Integration
    private Button tumblrLogin;
    private View.OnClickListener tumblrClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            makeToast("Button works");
            getTumblrCredentials();
        }
    };

    private OAuthManager.OAuthCallback<Credential> tumblrCallback = new OAuthManager.OAuthCallback<Credential>() {
        @Override
        public void run(OAuthManager.OAuthFuture<Credential> future) {
            try {
                Credential credential = future.getResult();
                makeToast("Login Successful");
            } catch (IOException e) {
                makeToast("Login Failed");
                e.printStackTrace();
            }


        }
    };
    OAuth tumblrOAuth;

    private void getTumblrCredentials() {
        tumblrOAuth = OAuth.newInstance(getActivity().getApplicationContext(),
                getActivity().getSupportFragmentManager(),
                new ClientParametersAuthentication(TumblrConstants.CLIENT_ID,
                        TumblrConstants.CLIENT_SECRET),
                TumblrConstants.AUTHORIZATION_CODE_URL,
                TumblrConstants.ACCESS_TOKEN_SERVER_URL,
                TumblrConstants.REDIRECT_URL, null,
                TumblrConstants.REQUEST_TOKEN_URL);
        tumblrOAuth.authorize10a("Tumblr", tumblrCallback, null);
    }

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the SDK of FB
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        //Initialize configs for Twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));

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
        initializeButtons(view);
    }

    /**
     * Initialize login button onto the view provided.
     *
     * @param view
     */
    private void initializeButtons(View view) {
        fBLogin = (LoginButton) view.findViewById(R.id.login_button);
        fBLogin.setPublishPermissions("publish_actions");
        fBLogin.setFragment(this);
        fBLogin.registerCallback(mCallbackManager, fBCallback);
        //Instantiate twitterLogin and set callbacks.
        twitterLogin = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        twitterLogin.setCallback(twitterCallback);
        tumblrLogin = (Button) view.findViewById(R.id.tumblr_login_button);
        tumblrLogin.setOnClickListener(tumblrClick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLogin.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Sends a fb message to the backend using the RetroFit library.
     *
     * @param token access token generated on login by FB
     * @param post  message to be posted on FB
     */
    private void postFB(AccessToken token, String post) {
        fBModel fBPost = new fBModel();
        fBPost.setToken(token.getToken());
        fBPost.setMessage(post);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TEST_URL)
                .build();
        ServerEndPointInterface apiService =
                restAdapter.create(ServerEndPointInterface.class);
        apiService.postMessage(fBPost, new retrofit.Callback<serverReturn>() {
            @Override
            public void success(serverReturn result, Response response) {
                makeToast("Posted to Facebook!");
            }

            @Override
            public void failure(RetrofitError error) {
                makeToast("Failed to post to Facebook");
            }
        });
    }

    /**
     * Posts a twitter message through the backend using Retrofit library.
     * @param token
     * @param secret
     * @param message
     */

    private void postTwitter(String token, String secret, String message) {
        twitterModel tw = new twitterModel();
        tw.setToken(token);
        tw.setSecret(secret);
        tw.setMessage(message);
        Log.d("Secret :", secret);
        Log.d("Token :", token);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(TEST_URL)
                .build();
        ServerEndPointInterface apiService =
                restAdapter.create(ServerEndPointInterface.class);

        apiService.postMessage(tw, new retrofit.Callback<serverReturn>() {
            @Override
            public void success(serverReturn result, Response response) {
                makeToast("Posted to Twitter!");
            }

            @Override
            public void failure(RetrofitError error) {
                String url = error.getUrl();
                System.out.println(url);
                System.out.println(error.getMessage());
                makeToast("Failed to post to Twitter");
            }
        });
    }

    /**
     * Makes a toast based on input message.
     *
     * @param message
     */
    private void makeToast(String message) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();

    }


}
