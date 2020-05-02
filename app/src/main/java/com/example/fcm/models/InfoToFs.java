package com.example.fcm.models;


public class InfoToFs {
    String img;
    String melody;
    String nickname;
    public InfoToFs() {
    }

    public InfoToFs(String img, String melody, String nickname) {
        this.img = img;
        this.melody = melody;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImg() {
        return img;
    }

    public String getMelody() {
        return melody;
    }

    public void setMelody(String melody) {
        this.melody = melody;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
