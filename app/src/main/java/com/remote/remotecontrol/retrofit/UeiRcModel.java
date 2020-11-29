package com.remote.remotecontrol.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UeiRcModel {
    @SerializedName("AuthenticateUserResult")
    @Expose
    private Integer authenticateUserResult;
    @SerializedName("ueiRc")
    @Expose
    private String ueiRc;

    public Integer getAuthenticateUserResult() {
        return authenticateUserResult;
    }

    public void setAuthenticateUserResult(Integer authenticateUserResult) {
        this.authenticateUserResult = authenticateUserResult;
    }

    public String getUeiRc() {
        return ueiRc;
    }

    public void setUeiRc(String ueiRc) {
        this.ueiRc = ueiRc;
    }
}
