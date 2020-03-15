package com.slastanna.questory.ui.forcheck;

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

import com.slastanna.questory.R;
import com.slastanna.questory.recycleAnswer.AdapterAnswer;
import com.slastanna.questory.recycleAnswer.ContentAnswer;
import com.slastanna.questory.tables.Answer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.userCurrent;
import static com.slastanna.questory.EmailPasswordActivity.userCurrentKey;

public class ForCheckFragment extends Fragment {


    RecyclerView rv;
    RecyclerView.LayoutManager layoutManager;
    AdapterAnswer adapter;
    boolean isLoading;
    public static int layout;
    public static TextView previewText;
    static ArrayList<ContentAnswer> contents = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_forcheck, container, false);
        layout = R.layout.answer;
        rv = root.findViewById(R.id.rvAns);
        //rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        //contents.clear();
        adapter = new AdapterAnswer(contents, getContext());
        adapter.setHasStableIds(true);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        //rv.setOnScrollListener(scrollListener);

        previewText=root.findViewById(R.id.text_forcheck);
        for (int i = 0; i < userCurrent.forCheking.size(); i++) {
            fillContent(userCurrent.forCheking.get(i));
        }
        //isLoading = true;
        //loadMore(userCurrent.forCheking.size(), contents.size());
        return root;
    }

//    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//            int visibleItemCount = layoutManager.getChildCount();//смотрим сколько элементов на экране
//            int totalItemCount = layoutManager.getItemCount();//сколько всего элементов
//            int firstVisibleItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();//какая позиция первого элемента
//
//            if (!isLoading) {//проверяем, грузим мы что-то или нет, эта переменная должна быть вне класса  OnScrollListener
//                if (((visibleItemCount+firstVisibleItems) >= totalItemCount)) {
//                    isLoading = true;//ставим флаг что мы попросили еще элемены
//                    loadMore(5, contents.size());
//
//                }
//            }
//        }
//    };

//    void loadMore(int quantity, int last){
//        for(int i = 0; i<quantity; i++){
//            fillContent(userCurrent.forCheking.get(i+last), quantity, last);
//        }
//        //adapter.notifyDataSetChanged();
//
//    }

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
                //if(contents.size()==quantity+last){
                //            isLoading=false;}
                adapter.notifyDataSetChanged();}catch (Exception e) {
                        Log.d("MyTag", "Exception:" + e);
                    }
                }}else{userCurrent.forCheking.remove(dataSnapshot.getKey());
                //if(contents.size()==quantity+last){ isLoading=false;}
                databaseFD.getReference("User").child(userCurrentKey).child("forCheking").setValue(userCurrent.forCheking);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userCurrent.forCheking.remove(key);
                databaseFD.getReference("User").child(userCurrentKey).child("forCheking").setValue(userCurrent.forCheking);
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