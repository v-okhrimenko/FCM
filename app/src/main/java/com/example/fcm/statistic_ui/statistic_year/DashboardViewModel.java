package com.example.fcm.statistic_ui.statistic_year;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue( "This is statistic_year fragment" );
    }

    public LiveData<String> getText() {
        return mText;
    }
}