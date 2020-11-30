package com.remote.remotecontrol.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NextKeyModel {
    @SerializedName("LoadOSMAndGetNextKeyResult")
    @Expose
    private Integer loadOSMAndGetNextKeyResult;
    @SerializedName("hasOSM")
    @Expose
    private Boolean hasOSM;
    @SerializedName("testKeyId")
    @Expose
    private Integer testKeyId;
    @SerializedName("testCode")
    @Expose
    private String testCode;
    @SerializedName("testCodeIdx")
    @Expose
    private Integer testCodeIdx;
    @SerializedName("candidateCodesCount")
    @Expose
    private Integer candidateCodesCount;
    @SerializedName("testCodeData")
    @Expose
    private String testCodeData;
    @SerializedName("state")
    @Expose
    private String state;

    public Integer getLoadOSMAndGetNextKeyResult() {
        return loadOSMAndGetNextKeyResult;
    }

    public void setLoadOSMAndGetNextKeyResult(Integer loadOSMAndGetNextKeyResult) {
        this.loadOSMAndGetNextKeyResult = loadOSMAndGetNextKeyResult;
    }

    public Boolean getHasOSM() {
        return hasOSM;
    }

    public void setHasOSM(Boolean hasOSM) {
        this.hasOSM = hasOSM;
    }

    public Integer getTestKeyId() {
        return testKeyId;
    }

    public void setTestKeyId(Integer testKeyId) {
        this.testKeyId = testKeyId;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public Integer getTestCodeIdx() {
        return testCodeIdx;
    }

    public void setTestCodeIdx(Integer testCodeIdx) {
        this.testCodeIdx = testCodeIdx;
    }

    public Integer getCandidateCodesCount() {
        return candidateCodesCount;
    }

    public void setCandidateCodesCount(Integer candidateCodesCount) {
        this.candidateCodesCount = candidateCodesCount;
    }

    public String getTestCodeData() {
        return testCodeData;
    }

    public void setTestCodeData(String testCodeData) {
        this.testCodeData = testCodeData;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
