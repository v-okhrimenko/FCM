package com.example.fcm.models;

public class TempaleJob {
    String template_name, valuta, tempalte_type;
    Integer price_hour, price_fixed, price_smena, smena_duration, overtime_pocent, rounded_minutes, half_shift_hours ;
    private Boolean half_shift;

    public TempaleJob() {
    }

    public TempaleJob(String template_name, String valuta, String tempalte_type, Integer price_hour, Integer price_fixed, Integer price_smena, Integer smena_duration, Integer overtime_pocent, Integer rounded_minutes, Integer half_shift_hours, Boolean half_shift) {
        this.template_name = template_name;
        this.valuta = valuta;
        this.tempalte_type = tempalte_type;
        this.price_hour = price_hour;
        this.price_fixed = price_fixed;
        this.price_smena = price_smena;
        this.smena_duration = smena_duration;
        this.overtime_pocent = overtime_pocent;
        this.rounded_minutes = rounded_minutes;
        this.half_shift_hours = half_shift_hours;
        this.half_shift = half_shift;
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

    public Integer getRounded_minutes() {
        return rounded_minutes;
    }

    public void setRounded_minutes(Integer rounded_minutes) {
        this.rounded_minutes = rounded_minutes;
    }

    public String getTempalte_type() {
        return tempalte_type;
    }

    public void setTempalte_type(String tempalte_type) {
        this.tempalte_type = tempalte_type;
    }

    public String getTemplate_name() {
        return template_name;
    }

    public void setTemplate_name(String template_name) {
        this.template_name = template_name;
    }

    public String getValuta() {
        return valuta;
    }

    public void setValuta(String valuta) {
        this.valuta = valuta;
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
}
