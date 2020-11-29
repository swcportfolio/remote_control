package com.remote.remotecontrol.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BrandModel {
    @SerializedName("GetBrandsResult")
    @Expose
    private Integer getBrandsResult;
    @SerializedName("brands")
    @Expose
    private List<String> brands = null;
    @SerializedName("resultTotal")
    @Expose
    private Integer resultTotal;

    public Integer getGetBrandsResult() {
        return getBrandsResult;
    }

    public void setGetBrandsResult(Integer getBrandsResult) {
        this.getBrandsResult = getBrandsResult;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public Integer getResultTotal() {
        return resultTotal;
    }

    public void setResultTotal(Integer resultTotal) {
        this.resultTotal = resultTotal;
    }
}
