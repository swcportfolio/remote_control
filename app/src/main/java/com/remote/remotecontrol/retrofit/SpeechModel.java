package com.remote.remotecontrol.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpeechModel {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("totalDataCount")
    @Expose
    private Integer totalDataCount;
    @SerializedName("data")
    @Expose
    private String data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getTotalDataCount() {
        return totalDataCount;
    }

    public void setTotalDataCount(Integer totalDataCount) {
        this.totalDataCount = totalDataCount;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
