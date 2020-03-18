package com.slastanna.questory.ui.endedQuests;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.slastanna.questory.R;
import com.slastanna.questory.recycleQuest.Content;
import com.slastanna.questory.recycleQuest.MyAdapter;
import com.slastanna.questory.tables.Quest;
import com.slastanna.questory.ui.fullQuest.fullQuestActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.userCurrent;

public class EndedQuestsFragment extends Fragment {

    //private FindQuestsFragment findQuestsViewModel;
    RecyclerView rv;
    MyAdapter adapter;
    TextView previewText;
    public static int layout;
    static ArrayList<Content> contentsEnd = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        //findQuestsViewModel = ViewModelProviders.of(this).get(FindQuestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_findquests, container, false);
        layout = R.layout.content_quests;
        rv = root.findViewById(R.id.recyclerQuest);
        rv.setHasFixedSize(false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        contentsEnd.clear();
        adapter = new MyAdapter(contentsEnd, getContext());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        previewText=root.findViewById(R.id.previewText);
        if(contentsEnd.size()==0){
        previewText.setText("У вас нет завершенных квестов");}
        // TODO убрать
        databaseReference = databaseFD.getReference("Quest");





        // Отлавливает добавление новых анкет, изменение или удаление старых
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Content content=null;
                if(dataSnapshot.exists()){
                    for (int i = 0; i < userCurrent.endedQuests.size(); i++) {
                        if(dataSnapshot.getKey().equals(userCurrent.endedQuests.get(i))){
                            Quest quest = dataSnapshot.getValue(Quest.class);
                            content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());
                            contentsEnd.add(content);
                            previewText.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Content content = null;
                int index;
                if(dataSnapshot.exists()){
                    for (int i = 0; i < userCurrent.endedQuests.size(); i++) {
                        if(dataSnapshot.getKey().equals(userCurrent.endedQuests.get(i))){
                            Quest quest = dataSnapshot.getValue(Quest.class);
                            content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());

                            index = getItemIndex(content);
                            contentsEnd.set(index, content);
                            adapter.notifyItemChanged(index);
                            previewText.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Content content = null;
                for (int i = 0; i < contentsEnd.size(); i++) {
                    if(contentsEnd.get(i).key.equals(dataSnapshot.getKey())){
                        content= contentsEnd.get(i);
                    }
                }
                int index=getItemIndex(content);
                if(index!=-1){
                    contentsEnd.remove(index);
                    adapter.notifyItemRemoved(index);}
                if(contentsEnd.size()==0){
                    previewText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        adapter.setOnItemClickListener(new MyAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                //Переход к квесту писать здесь
                fullQuestActivity.currentQuestkey = contentsEnd.get(position).key;
                Intent intent = new Intent(getContext(), fullQuestActivity.class);
                startActivity(intent);

            }
        });




        return root;
    }

    // Получить позицию измененного contentsEnd
    private int getItemIndex(Content content){
        int index=-1;
        for(int i = 0; i< contentsEnd.size(); i++){
            if(contentsEnd.get(i).key.equals(content.key))
            {index=i;break;}
        }
        return index;
    }
}