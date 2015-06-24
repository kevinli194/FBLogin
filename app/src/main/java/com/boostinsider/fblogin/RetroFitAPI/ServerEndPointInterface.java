package com.boostinsider.fblogin.RetroFitAPI;

import com.boostinsider.fblogin.RetroFitAPI.models.FBPost;
import com.boostinsider.fblogin.RetroFitAPI.models.FBReturn;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Kevin on 6/23/2015.
 */
public interface ServerEndPointInterface {
    @POST("/network/facebook/post_to_timeline")
    public void postMessage(@Body FBPost fb, Callback<FBReturn> cb);

}
