package com.example.questory3.ui.myQuests;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.questory3.R;
import com.example.questory3.recycleQuest.Content;
import com.example.questory3.recycleQuest.MyAdapter;
import com.example.questory3.tables.Quest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.questory3.EmailPasswordActivity.databaseFD;
import static com.example.questory3.EmailPasswordActivity.databaseReference;
import static com.example.questory3.EmailPasswordActivity.userCurrentKey;

public class MyQuestsFragment extends Fragment {

    private MyQuestsViewModel myQuestsViewModel;

    RecyclerView rv;
    TextView previewText;
    MyAdapter adapterMyQuests;
    public static int layout;
    static ArrayList<Content> contents = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_findquests, container, false);
        layout = R.layout.content_quests;
        rv = root.findViewById(R.id.recyclerQuest);
        rv.setHasFixedSize(false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        contents.clear();
        adapterMyQuests = new MyAdapter(contents, getContext());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapterMyQuests);
        previewText=root.findViewById(R.id.previewText);
        previewText.setText("У вас нет созданных квестов");
        // TODO убрать
        databaseReference = databaseFD.getReference("Quest");




        Query query = databaseReference.orderByChild("ownerKey").equalTo(userCurrentKey);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Content content=null;
                try {
                    Log.d("MyTag", "Data:"+dataSnapshot);
                    Quest quest = dataSnapshot.getValue(Quest.class);
                    content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());
                    contents.add(content);
                    adapterMyQuests.notifyDataSetChanged();
                    previewText.setVisibility(View.GONE);
                }catch(Exception e){Log.d("MyTag", "Data:"+e);}

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Content content = null;
                int index;
                Quest quest = dataSnapshot.getValue(Quest.class);
                content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());

                index = getItemIndex(content);
                contents.set(index, content);
                adapterMyQuests.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


                //Quest quest = dataSnapshot.getValue(Quest.class);
                Content content = null;
                //content.key = dataSnapshot.getKey();
                for (int i = 0; i < contents.size(); i++) {
                    if(contents.get(i).key.equals(dataSnapshot.getKey())){
                        content=contents.get(i);
                    }
                }
                int index=getItemIndex(content);
                if(index!=-1){
                    contents.remove(index);
                    adapterMyQuests.notifyItemRemoved(index);}
                if(contents.size()==0){
                    previewText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        adapterMyQuests.setOnItemClickListener(new MyAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {


            }
        });





        return root;
    }

    // Получить позицию измененного contents
    private int getItemIndex(Content content){
        int index=-1;
        for(int i = 0; i< contents.size(); i++){
            if(contents.get(i).key.equals(content.key))
            {index=i;break;}
        }
        return index;
    }


}