package com.boostinsider.fblogin.HTTP_Clients;
import android.os.AsyncTask;

import com.loopj.android.http.*;

import org.apache.http.entity.StringEntity;

/**
 * Created by Kevin on 6/22/2015.
 *
 * Async HTTP Client used to post to server in order to enable facebook posting.
 */

public class FBPostClient {
    private static final String TEST_URL = "http://posttestserver.com/post.php?dir=boostinsider";
    private static final String SERVER_URL = "http://52.11.39.63:3008/network/facebook/post_to_timeline";
    //private static final String SECOND_URL = "http://52.11.39.63:3008/oauth/test_post";
    private static final String loginURL = "http://199.217.119.199:3009/api/v1/users/login";
    private static final String signupURL = "http://199.217.119.199:3009/api/v1/users/create";
    private static final String googleURL = "http://google.com";


    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(RequestParams params, AsyncHttpResponseHandler responseHandler) {

        client.post(TEST_URL, responseHandler);
    }

}