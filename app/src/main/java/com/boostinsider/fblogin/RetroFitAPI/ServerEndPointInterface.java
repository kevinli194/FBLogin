package com.boostinsider.fblogin.RetroFitAPI;

import com.boostinsider.fblogin.RetroFitAPI.models.fBModel;
import com.boostinsider.fblogin.RetroFitAPI.models.serverReturn;
import com.boostinsider.fblogin.RetroFitAPI.models.twitterModel;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Kevin on 6/23/2015.
 */
public interface ServerEndPointInterface {
    @POST("/network/facebook/post_to_timeline")
    public void postMessage(@Body fBModel fb, Callback<serverReturn> cb);
    @POST("/oauth/twitter/post_to_twitter")
    public void postMessage(@Body twitterModel tw, Callback<serverReturn> cb);
}
