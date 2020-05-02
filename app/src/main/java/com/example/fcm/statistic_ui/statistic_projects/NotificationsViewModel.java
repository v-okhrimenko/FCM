package com.example.fcm.statistic_ui.statistic_projects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue( "This is statistic_projects fragment" );
    }

    public LiveData<String> getText() {
        return mText;
    }
}