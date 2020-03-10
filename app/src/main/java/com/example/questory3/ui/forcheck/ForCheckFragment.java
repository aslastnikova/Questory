package com.example.questory3.ui.forcheck;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questory3.R;
import com.example.questory3.recycleAnswer.AdapterAnswer;
import com.example.questory3.recycleAnswer.ContentAnswer;
import com.example.questory3.tables.Answer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.questory3.EmailPasswordActivity.databaseFD;
import static com.example.questory3.EmailPasswordActivity.databaseReference;
import static com.example.questory3.EmailPasswordActivity.userCurrent;

public class ForCheckFragment extends Fragment {


    RecyclerView rv;
    AdapterAnswer adapter;
    public static int layout;
    public static TextView previewText;
    static ArrayList<ContentAnswer> contents = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_forcheck, container, false);
        layout = R.layout.answer;
        rv = root.findViewById(R.id.rvAns);
        rv.setHasFixedSize(false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        //contents.clear();
        adapter = new AdapterAnswer(contents, getContext());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        previewText=root.findViewById(R.id.text_forcheck);
        for (int i = 0; i < userCurrent.forCheking.size(); i++) {
            fillContent(userCurrent.forCheking.get(i));
        }

        return root;
    }

    void fillContent(String key){
        // TODO убрать
        databaseReference = databaseFD.getReference("Answer").child(key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Answer answer= dataSnapshot.getValue(Answer.class);
                if(answer!=null){
                if(!answer.ischeked){
                    try{
                ContentAnswer content = new ContentAnswer();
                content.nameQuest=answer.questName;
                content.nameTask=answer.taskName;
                content.answer=answer.answer;
                content.task=answer.taskText;
                content.key=key;
                content.name=answer.userName;
                content.keyQuest=answer.questKey;
                content.keyUser=answer.userKey;
                content.points=answer.points;
                contents.add(content);
                previewText.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();}catch (Exception e) {
                        Log.d("MyTag", "Exception:" + e);
                    }
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        contents.clear();

    }

    // Получить позицию измененного contents
    private int getItemIndex(ContentAnswer content){
        int index=-1;
        for(int i = 0; i< contents.size(); i++){
            if(contents.get(i).key.equals(content.key))
            {index=i;break;}
        }
        return index;
    }


}