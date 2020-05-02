package com.example.fcm.models;

import java.util.Date;

public class MainWork {

    private String name, valuta, tempalte_type, discription;
    private Integer price_hour, price_fixed, price_smena, smena_duration, overtime_pocent, rounded_minut, half_shift_hours;
    private Boolean status, needFinish, half_shift;
    private Date date, alarm1, alarm2;
    int uniqId;
    private Date start;
    private Date end;
    private Integer otrabotanoMinutOverTime;
    private Integer otrabotanoMinutHourlyTime;
    private Float zarabotanoFinal;
    private Float zarabotanoOvertimeFinal;



    public MainWork() {
    }

    public MainWork(String name, String valuta, String tempalte_type, String discription, Integer price_hour, Integer price_fixed, Integer price_smena, Integer smena_duration, Integer overtime_pocent, Integer rounded_minut, Integer half_shift_hours, Boolean status, Boolean needFinish, Boolean half_shift, Date date, Date alarm1, Date alarm2, int uniqId, Date start, Date end, Integer otrabotanoMinutOverTime, Integer otrabotanoMinutHourlyTime, Float zarabotanoFinal, Float zarabotanoOvertimeFinal) {
        this.name = name;
        this.valuta = valuta;
        this.tempalte_type = tempalte_type;
        this.discription = discription;
        this.price_hour = price_hour;
        this.price_fixed = price_fixed;
        this.price_smena = price_smena;
        this.smena_duration = smena_duration;
        this.overtime_pocent = overtime_pocent;
        this.rounded_minut = rounded_minut;
        this.half_shift_hours = half_shift_hours;
        this.status = status;
        this.needFinish = needFinish;
        this.half_shift = half_shift;
        this.date = date;
        this.alarm1 = alarm1;
        this.alarm2 = alarm2;
        this.uniqId = uniqId;
        this.start = start;
        this.end = end;
        this.otrabotanoMinutOverTime = otrabotanoMinutOverTime;
        this.otrabotanoMinutHourlyTime = otrabotanoMinutHourlyTime;
        this.zarabotanoFinal = zarabotanoFinal;
        this.zarabotanoOvertimeFinal = zarabotanoOvertimeFinal;
    }

    public Integer getHalf_shift_hours() {
        return half_shift_hours;
    }

    public void setHalf_shift_hours(Integer half_shift_hours) {
        this.half_shift_hours = half_shift_hours;
    }

    public Boolean getHalf_shift() {
        return half_shift;
    }

    public void setHalf_shift(Boolean half_shift) {
        this.half_shift = half_shift;
    }

    public Integer getRounded_minut() {
        return rounded_minut;
    }

    public void setRounded_minut(Integer rounded_minut) {
        this.rounded_minut = rounded_minut;
    }

    public Boolean getNeedFinish() {
        return needFinish;
    }

    public void setNeedFinish(Boolean needFinish) {
        this.needFinish = needFinish;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOtrabotanoMinutOverTime() {
        return otrabotanoMinutOverTime;
    }

    public void setOtrabotanoMinutOverTime(Integer otrabotanoMinutOverTime) {
        this.otrabotanoMinutOverTime = otrabotanoMinutOverTime;
    }

    public Integer getOtrabotanoMinutHourlyTime() {
        return otrabotanoMinutHourlyTime;
    }

    public void setOtrabotanoMinutHourlyTime(Integer otrabotanoMinutHourlyTime) {
        this.otrabotanoMinutHourlyTime = otrabotanoMinutHourlyTime;
    }

    public Float getZarabotanoFinal() {
        return zarabotanoFinal;
    }

    public void setZarabotanoFinal(Float zarabotanoFinal) {
        this.zarabotanoFinal = zarabotanoFinal;
    }

    public Float getZarabotanoOvertimeFinal() {
        return zarabotanoOvertimeFinal;
    }

    public void setZarabotanoOvertimeFinal(Float zarabotanoOvertimeFinal) {
        this.zarabotanoOvertimeFinal = zarabotanoOvertimeFinal;
    }

    public String getValuta() {
        return valuta;
    }

    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public String getTempalte_type() {
        return tempalte_type;
    }

    public void setTempalte_type(String tempalte_type) {
        this.tempalte_type = tempalte_type;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Integer getPrice_hour() {
        return price_hour;
    }

    public void setPrice_hour(Integer price_hour) {
        this.price_hour = price_hour;
    }

    public Integer getPrice_fixed() {
        return price_fixed;
    }

    public void setPrice_fixed(Integer price_fixed) {
        this.price_fixed = price_fixed;
    }

    public Integer getPrice_smena() {
        return price_smena;
    }

    public void setPrice_smena(Integer price_smena) {
        this.price_smena = price_smena;
    }

    public Integer getSmena_duration() {
        return smena_duration;
    }

    public void setSmena_duration(Integer smena_duration) {
        this.smena_duration = smena_duration;
    }

    public Integer getOvertime_pocent() {
        return overtime_pocent;
    }

    public void setOvertime_pocent(Integer overtime_pocent) {
        this.overtime_pocent = overtime_pocent;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getAlarm1() {
        return alarm1;
    }

    public void setAlarm1(Date alarm1) {
        this.alarm1 = alarm1;
    }

    public Date getAlarm2() {
        return alarm2;
    }

    public void setAlarm2(Date alarm2) {
        this.alarm2 = alarm2;
    }

    public int getUniqId() {
        return uniqId;
    }

    public void setUniqId(int uniqId) {
        this.uniqId = uniqId;
    }
}
