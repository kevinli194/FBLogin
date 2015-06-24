package com.boostinsider.fblogin;

import com.boostinsider.fblogin.HTTP_Clients.FBPostClient;
import com.boostinsider.fblogin.RetroFitAPI.ServerEndPointInterface;
import com.boostinsider.fblogin.RetroFitAPI.models.FBPost;
import com.boostinsider.fblogin.RetroFitAPI.models.FBReturn;
import com.loopj.android.http.*;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    private CallbackManager mCallbackManager;
    private TextView mTextDisplayed;
    private static final String WORKING_URL = "http://posttestserver.com";
    private static final String TEST_URL = "http://52.11.39.63:3008";
    private static final String WORKING_BOOST = "http://199.217.119.199:3009";
    private final String USER_AGENT = "Mozilla/5.0";

    // Performs an action based on login results.
    private FacebookCallback<LoginResult> myCallBack = new FacebookCallback<LoginResult>() {
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

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize the SDK of FB
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        //Use Factory to construct a callbackmanager
        mCallbackManager = CallbackManager.Factory.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextDisplayed = (TextView) view.findViewById(R.id.text_details);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        //loginButton.setReadPermissions("user_friends");
        //loginButton.setReadPermissions("read_friendlists");
        loginButton.setPublishPermissions("publish_actions");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, myCallBack);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void loopJ(AccessToken token, String post) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("token", token.getToken());
        requestParams.put("message", post);
        FBPostClient.post(requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                makeToast("Success!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //HttpResponseException hre = (HttpResponseException) error;
                //int response = hre.getStatusCode();
                makeToast("Failed!");

            }
        });
    }

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
        apiService.postMessage(fBPost, new Callback<FBReturn>() {
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


    private void makeToast(String message) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }


}
