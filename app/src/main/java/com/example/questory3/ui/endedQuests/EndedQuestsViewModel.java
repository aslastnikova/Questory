package com.example.questory3.ui.endedQuests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EndedQuestsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EndedQuestsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ended quests fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}