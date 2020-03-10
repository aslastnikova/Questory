package com.example.questory3.ui.findQuests;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questory3.R;
import com.example.questory3.recycleQuest.Content;
import com.example.questory3.recycleQuest.MyAdapter;
import com.example.questory3.tables.Quest;
import com.example.questory3.ui.forcheck.ForCheckFragment;
import com.example.questory3.ui.fullQuest.fullQuestActivity;
import com.example.questory3.ui.fullQuest.fullQuestFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import static com.example.questory3.EmailPasswordActivity.databaseFD;
import static com.example.questory3.EmailPasswordActivity.databaseReference;

public class FindQuestsFragment extends Fragment {

    private FindQuestsViewModel findQuestsViewModel;
    RecyclerView rv;
    MyAdapter adapter;
    public static int layout;
    static ArrayList<Content> contents = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        findQuestsViewModel = ViewModelProviders.of(this).get(FindQuestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_findquests, container, false);
        layout = R.layout.content_quests;
        root.findViewById(R.id.progress_wheel).setVisibility(View.VISIBLE);
        rv = root.findViewById(R.id.recyclerQuest);
        rv.setHasFixedSize(false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        contents.clear();
        adapter = new MyAdapter(contents, getContext());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        TextView previewText= root.findViewById(R.id.previewText);
        previewText.setVisibility(View.GONE);
        // TODO убрать
        databaseReference = databaseFD.getReference("Quest");





        // Отлавливает добавление новых анкет, изменение или удаление старых
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Content content=null;
                try {
                    Log.d("MyTag", "Data:"+dataSnapshot);
                    Quest quest = dataSnapshot.getValue(Quest.class);
                    content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());
                    contents.add(content);
                    root.findViewById(R.id.progress_wheel).setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }catch(Exception e){Log.d("MyTag", "Data:"+e);}

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Content content = null;
                int index;
                Quest quest = dataSnapshot.getValue(Quest.class);
                content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());

                index = getItemIndex(content);
                if(index!=-1){
                contents.set(index, content);
                adapter.notifyItemChanged(index);}
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
                    adapter.notifyItemRemoved(index);}
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
                if(contents.size()>position){
                fullQuestActivity.currentQuestkey = contents.get(position).key;
                Intent intent = new Intent(getContext(), fullQuestActivity.class);
                startActivity(intent);}



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