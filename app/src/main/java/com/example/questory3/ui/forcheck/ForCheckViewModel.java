package com.example.questory3.ui.forcheck;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ForCheckViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ForCheckViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is for check fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}