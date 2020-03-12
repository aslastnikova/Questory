package com.slastanna.questory.ui.message;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.slastanna.questory.R;

import com.slastanna.questory.recyclerMessage.AdapterMessage;
import com.slastanna.questory.tables.MyMessage;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.userCurrentKey;

public class MessageFragment extends Fragment {

    ArrayList<MyMessage> messages = new ArrayList<>();
    RecyclerView rv;
    AdapterMessage adapter;
    public static TextView previewText;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_message, container, false);
        final TextView textView = root.findViewById(R.id.text_message);

        rv = root.findViewById(R.id.recycle_message);
        rv.setHasFixedSize(false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        messages.clear();
        adapter = new AdapterMessage(messages, getContext());
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);
        previewText= root.findViewById(R.id.text_message);

        databaseReference = databaseFD.getReference("Message");
        // Отлавливает добавление новых анкет, изменение или удаление старых
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {

                    Log.d("MyTag", "Data:"+dataSnapshot);
                    MyMessage message = dataSnapshot.getValue(MyMessage.class);
                    //content =new Content(quest.qname, quest.description, quest.qpicture, dataSnapshot.getKey());
                    message.messageKey=dataSnapshot.getKey();
                    if(message.requestedKey.equals(userCurrentKey)){
                        previewText.setVisibility(View.GONE);
                        messages.add(message);
                        adapter.notifyDataSetChanged();}
                }catch(Exception e){Log.d("MyTag", "Data:"+e);}

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        return root;
    }

}