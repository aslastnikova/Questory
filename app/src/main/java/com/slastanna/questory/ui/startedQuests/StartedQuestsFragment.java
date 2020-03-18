package com.slastanna.questory.ui.startedQuests;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.slastanna.questory.R;
import com.slastanna.questory.recycleQuest.Content;
import com.slastanna.questory.recycleQuest.MyAdapter;
import com.slastanna.questory.tables.Quest;
import com.slastanna.questory.ui.fullQuest.fullQuestActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.userCurrent;

public class StartedQuestsFragment extends Fragment {

    RecyclerView rv;
    MyAdapter adapter;
    TextView previewText;
    public static int layout;
    static ArrayList<Content> contentsStart = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        //findQuestsViewModel = ViewModelProviders.of(this).get(FindQuestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_findquests, container, false);
        layout = R.layout.content_quests;
        rv = root.findViewById(R.id.recyclerQuest);
        rv.setHasFixedSize(false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        contentsStart.clear();
        adapter = new MyAdapter(contentsStart, getContext());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        previewText=root.findViewById(R.id.previewText);
        if(contentsStart.size()==0){
        previewText.setText("У вас нет начатых квестов");}

        databaseReference = databaseFD.getReference("Quest");





        // Отлавливает добавление новых анкет, изменение или удаление старых
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Content content=null;
                if(dataSnapshot.exists()){
                    for (int i = 0; i < userCurrent.activeQuests.size(); i++) {
                        if(dataSnapshot.getKey().equals(userCurrent.activeQuests.get(i))){
                            Quest quest = dataSnapshot.getValue(Quest.class);
                            content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());
                            contentsStart.add(content);
                            adapter.notifyDataSetChanged();
                            previewText.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Content content = null;
                int index;
                if(dataSnapshot.exists()){
                    for (int i = 0; i < userCurrent.activeQuests.size(); i++) {
                        if(dataSnapshot.getKey().equals(userCurrent.activeQuests.get(i))){
                            Quest quest = dataSnapshot.getValue(Quest.class);
                            content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());

                            index = getItemIndex(content);
                            contentsStart.set(index, content);
                            adapter.notifyItemChanged(index);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Content content = null;
                for (int i = 0; i < contentsStart.size(); i++) {
                    if(contentsStart.get(i).key.equals(dataSnapshot.getKey())){
                        content= contentsStart.get(i);
                    }
                }
                int index=getItemIndex(content);
                if(index!=-1){
                    contentsStart.remove(index);
                    adapter.notifyItemRemoved(index);}
                if(contentsStart.size()==0){
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
                fullQuestActivity.currentQuestkey = contentsStart.get(position).key;
                Intent intent = new Intent(getContext(), fullQuestActivity.class);
                startActivity(intent);

            }
        });


        return root;
    }

    // Получить позицию измененного contentsStart
    private int getItemIndex(Content content){
        int index=-1;
        for(int i = 0; i< contentsStart.size(); i++){
            if(contentsStart.get(i).key.equals(content.key))
            {index=i;break;}
        }
        return index;
    }

}