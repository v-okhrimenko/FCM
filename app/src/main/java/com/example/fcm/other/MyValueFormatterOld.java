package com.example.fcm.other;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MyValueFormatterOld implements ValueFormatter {
    private DecimalFormat mFormat;

    public MyValueFormatterOld() {
        mFormat = new DecimalFormat("###"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
//        return mFormat.format(value) + " грн ";}

        return mFormat.format(value);}
}
