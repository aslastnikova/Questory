package com.example.questory3.ui.fullQuest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.questory3.EmailPasswordActivity;
import com.example.questory3.MainActivity;
import com.example.questory3.R;
import com.example.questory3.TaskActivity;
import com.example.questory3.tables.Quest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.example.questory3.EmailPasswordActivity.databaseFD;
import static com.example.questory3.EmailPasswordActivity.databaseReference;
import static com.example.questory3.MainActivity.decodeBase64;
import static com.example.questory3.MainActivity.fab;

public class fullQuestFragment extends Fragment {

    public static String currentQuestkey;
    Quest currentQuest;

    TextView qname;
    TextView qdesc;
    TextView qduratation;
    TextView qdistance;
    TextView qowner;
    ImageView timer, map;
    ImageView qpic;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.quest_preview, container, false);
        qname = root.findViewById(R.id.qname);
        qdesc = root.findViewById(R.id.qdecription);
        qduratation = root.findViewById(R.id.qduratation);
        qdistance = root.findViewById(R.id.qdistation);
        qpic = root.findViewById(R.id.qimage);
        qowner=root.findViewById(R.id.qowner);
        timer=root.findViewById(R.id.timer);
        map=root.findViewById(R.id.mapsign);
        fab.setImageResource(R.drawable.baseline_play_arrow_24);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TaskActivity.class);
                startActivity(intent);
            }
        });
        databaseReference=databaseFD.getReference("Quest");

        Query query= databaseReference.orderByKey().equalTo(currentQuestkey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Log.d("MyTag", "Value is: " + dataSnapshot);
                if (dataSnapshot.exists()) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        currentQuest=issue.getValue(Quest.class);

                        if(currentQuest!=null){
                            fillText(currentQuest);
                            break;
                        }

                    }
                }
                //Player playerServer = dataSnapshot.getValue(Player.class);
                // Log.d(Tag, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        return root;
    }

    void fillText(Quest quest){
        setTextifnotnull(quest.qname, qname);
        setTextifnotnull(quest.description, qdesc);
        setTextifnotnull(quest.ownerName, qowner);
        if(quest.kilomenters!=0){
        qdistance.setText(""+quest.kilomenters+" км");}else{
            qdistance.setVisibility(View.GONE);
            map.setVisibility(View.GONE);
        }
        if(quest.duration!=0){
        int m = quest.duration%60;
        int h = (quest.duration-m)/60;
        qduratation.setText(""+h+" ч "+m+" мин");}else{
            timer.setVisibility(View.GONE);
            qduratation.setVisibility(View.GONE);
        }
        Bitmap pic=null;
        if(!(quest.qpicture.equals(null)||quest.qpicture.equals(""))){
        pic=decodeBase64(quest.qpicture);}
        if(pic!=null){
            qpic.setImageBitmap(pic);
        }
    }
    public void setTextifnotnull(String s, TextView t){
        if(!(s.equals(null)||s.equals(""))){
            t.setText(s);
        }
    }
}