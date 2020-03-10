package com.example.questory3.ui.myQuests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyQuestsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyQuestsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my quests fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}