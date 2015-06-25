package com.boostinsider.fblogin.RetroFitAPI.models;

import com.google.gson.annotations.Expose;

/**
 * Created by Kevin on 6/24/2015.
 */
public class twitterModel {
    @Expose
    private String token;
    @Expose
    private String secret;
    @Expose
    private String message;

    /**
     *
     * @return
     * The token
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @param token
     * The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     *
     * @return
     * The secret
     */
    public String getSecret() {
        return secret;
    }

    /**
     *
     * @param secret
     * The secret
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}

