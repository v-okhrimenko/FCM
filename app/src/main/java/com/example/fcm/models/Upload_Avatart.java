package com.example.fcm.models;

public class Upload_Avatart {

    private String avName;
    private String avImageUrl;



    public Upload_Avatart() {

    }

    public Upload_Avatart(String avName, String avImageUrl) {

        this.avName = avName;
        this.avImageUrl = avImageUrl;

    }

    public String getAvName() {
        return avName;
    }

    public void setAvName(String avName) {
        this.avName = avName;
    }

    public String getAvImageUrl() {
        return avImageUrl;
    }

    public void setAvImageUrl(String avImageUrl) {
        this.avImageUrl = avImageUrl;
    }


}

