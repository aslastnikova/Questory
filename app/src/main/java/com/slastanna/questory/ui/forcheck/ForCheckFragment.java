package com.slastanna.questory.ui.forcheck;

import android.app.Activity;
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
import androidx.recyclerview.widget.SimpleItemAnimator;

//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.imagepipeline.core.ImagePipelineConfig;
//import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.google.firebase.database.Query;
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
    public static Activity activity;
    public static int layout;
    public static TextView previewText;
    static ArrayList<ContentAnswer> contents = new ArrayList<>();
    static ArrayList<String> contentsAdded = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_forcheck, container, false);
        layout = R.layout.answer;

        activity=getActivity();
//        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(getContext())
//                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
//                .setResizeAndRotateEnabledForNetwork(true)
//                .setDownsampleEnabled(true)
//                .build();
//        Fresco.initialize(getContext(), config);
        //new ImageViewer.Builder(context, img).show();

        rv = root.findViewById(R.id.rvAns);
//        ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);
        rv.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(getContext());
        //contents.clear();
        adapter = new AdapterAnswer(contents, getContext());
        adapter.setHasStableIds(true);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        //rv.setOnScrollListener(scrollListener);
        if(contents.size()==0){
        previewText=root.findViewById(R.id.text_forcheck);}
        for (int i = 0; i < userCurrent.forCheking.size(); i++) {
            fillContent(userCurrent.forCheking.get(i));
        }
        //isLoading = true;
        //loadMore(userCurrent.forCheking.size(), contents.size());
        return root;
    }



    void fillContent(String key){
        // TODO убрать
        databaseReference = databaseFD.getReference("Answer");
        Query query = databaseFD.getReference("Answer").child(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MyTag", "AnswerQuery: 1");
                Answer answer= dataSnapshot.getValue(Answer.class);
                if(answer!=null){
                if(!answer.ischeked){
                    try{
                        if(!contentsAdded.contains(key)){

                                contentsAdded.add(key);
                                ContentAnswer content = new ContentAnswer();
                                content.nameQuest=answer.questName;
                                content.nameTask=answer.taskName;
                                content.answer=answer.answer;
                                if (content.getAnswer().length() > 300) {
                                    content.ispicture=true;
                                }
                                content.task=answer.taskText;
                                content.key=key;
                                content.name=answer.userName;
                                content.keyQuest=answer.questKey;
                                content.keyUser=answer.userKey;
                                content.points=answer.points;
                                content.id=contents.size();
                                contents.add(content);
                                previewText.setVisibility(View.GONE);
                                if(contents.size()==userCurrent.forCheking.size()){
                                    Log.d("MyTag", "dad");
                                }
                                adapter.notifyDataSetChanged();}}catch (Exception e) {
                        Log.d("MyTag", "Exception:" + e);
                    }
                }}else{userCurrent.forCheking.remove(dataSnapshot.getKey());
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
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        contents.clear();
//
//    }


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