package com.example.fcm.statistic_ui.statistic_month;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue( "This is statistic_month fragment" );
    }

    public LiveData<String> getText() {
        return mText;
    }
}