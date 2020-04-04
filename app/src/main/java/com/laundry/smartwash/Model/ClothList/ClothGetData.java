package com.laundry.smartwash.Model.ClothList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ClothGetData implements Serializable
{

    @SerializedName("cloth_id")
    @Expose
    private String clothId;
    @SerializedName("cloth_name")
    @Expose
    private String clothName;

    public String getClothId() {
        return clothId;
    }

    public String getClothName() {
        return clothName;
    }
}