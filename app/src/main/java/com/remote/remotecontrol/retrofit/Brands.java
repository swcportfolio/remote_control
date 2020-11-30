package com.remote.remotecontrol.retrofit;

public class Brands {
    private String ueiRc;
    private String region;
    private String deviceTypes;
    private String devGroupId;
    private String brandsStartWith;
    private int resultPageIndex;
    private int resultPageSize;

    public String getUeiRc() {
        return ueiRc;
    }

    public void setUeiRc(String ueiRc) {
        this.ueiRc = ueiRc;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(String deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public String getDevGroupId() {
        return devGroupId;
    }

    public void setDevGroupId(String devGroupId) {
        this.devGroupId = devGroupId;
    }

    public String getBrandsStartWith() {
        return brandsStartWith;
    }

    public void setBrandsStartWith(String brandsStartWith) {
        this.brandsStartWith = brandsStartWith;
    }

    public int getResultPageIndex() {
        return resultPageIndex;
    }

    public void setResultPageIndex(int resultPageIndex) {
        this.resultPageIndex = resultPageIndex;
    }

    public int getResultPageSize() {
        return resultPageSize;
    }

    public void setResultPageSize(int resultPageSize) {
        this.resultPageSize = resultPageSize;
    }
}
