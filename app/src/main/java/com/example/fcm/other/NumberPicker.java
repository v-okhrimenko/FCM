package com.example.fcm.other;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class NumberPicker extends android.widget.NumberPicker {

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void addView(View child) {
        super.addView(child);

        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }



    private void updateView(View view) {
        if(view instanceof EditText){
            ((EditText) view).setTypeface(Typeface.DEFAULT_BOLD);
            ((EditText) view).setTextSize(18);
            ((EditText) view).setTextColor( Color.parseColor("#000000"));
        }
    }



}