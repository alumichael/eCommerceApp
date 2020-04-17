package com.laundry.smartwash.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class fetchByStatus implements Serializable
{

    @SerializedName("userID")
    @Expose
    public String userID;
    @SerializedName("status")
    @Expose
    public String status;

    public fetchByStatus(String userID, String status) {

        this.userID = userID;
        this.status = status;
    }



}