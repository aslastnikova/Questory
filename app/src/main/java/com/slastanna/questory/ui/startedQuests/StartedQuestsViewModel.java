package com.slastanna.questory.ui.startedQuests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StartedQuestsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public StartedQuestsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my started fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}