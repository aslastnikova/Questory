package com.example.questory3.ui.findQuests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FindQuestsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FindQuestsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is find quests fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}