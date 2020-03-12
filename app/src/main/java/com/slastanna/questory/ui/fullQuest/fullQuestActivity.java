package com.slastanna.questory.ui.fullQuest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.slastanna.questory.R;
import com.slastanna.questory.TaskActivity;
import com.slastanna.questory.tables.Quest;
import com.slastanna.questory.tables.Rating;
import com.slastanna.questory.tables.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.userCurrent;
import static com.slastanna.questory.EmailPasswordActivity.userCurrentKey;
import static com.slastanna.questory.MapActivity.hiddenadress;
import static com.slastanna.questory.MapActivity.markerCoordinates;
import static com.slastanna.questory.MapActivity.markerCoordinatesDone;
import static com.slastanna.questory.TaskActivity.ratingKey;
//import static com.example.questory3.TaskActivity.alltasks;

public class fullQuestActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static FloatingActionButton fab;
    public static String currentQuestkey;
    public static Quest currentQuest;


    TextView qname;
    TextView qdesc;
    TextView qduratation;
    TextView qdistance;
    TextView qowner;
    ImageView timer, map;
    ImageView qpic;
    Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_for_activities);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        qname = findViewById(R.id.qname);
        qdesc = findViewById(R.id.qdecription);
        qduratation = findViewById(R.id.qduratation);
        qdistance = findViewById(R.id.qdistation);
        qpic = findViewById(R.id.qimage);
        qowner=findViewById(R.id.qowner);
        timer=findViewById(R.id.timer);
        map=findViewById(R.id.mapsign);
        fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.baseline_play_arrow_24);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setEnabled(false);
                TaskActivity.tasks=currentQuest.tasks;

                if(userCurrent.activeQuests.contains(currentQuestkey)){

                    databaseReference=databaseFD.getReference("Rating");
                    Query query= databaseReference.orderByChild("keyUser").equalTo(userCurrentKey);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("MyTag", "Value is: " + dataSnapshot);
                            if (dataSnapshot.exists()) {
                                int k=0;

                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    k++;
                                    Rating currentRating=issue.getValue(Rating.class);

                                    if(currentRating!=null){
                                        if(currentRating.keyQuest.equals(currentQuestkey)){
                                            ratingKey=issue.getKey();
                                            TaskActivity.currentAttempts=currentRating.attemptsOnLastTask;
                                            TaskActivity.currentPoints=currentRating.points;

                                            if(currentQuest.tasks.contains(currentRating.lastTaskKey)){
                                                int j = currentQuest.tasks.indexOf(currentRating.lastTaskKey);
                                                databaseReference=databaseFD.getReference("Task");

                                                Query query= databaseReference.orderByKey().equalTo(currentRating.lastTaskKey);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        // Log.d("MyTag", "Value is: " + dataSnapshot);
                                                        if (dataSnapshot.exists()) {

                                                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                                                Log.d("MyTag", "issue: "+issue);
                                                                currentTask=issue.getValue(Task.class);

                                                                if(currentTask!=null){

                                                                    try{

                                                                        if(currentRating.coordinates!=null){

                                                                            markerCoordinatesDone = FeatureCollection.fromJson(currentRating.coordinates).features();

                                                                            if(!currentTask.hiddenAddress){
                                                                                hiddenadress=false;
                                                                                //markerCoordinates.clear();
                                                                                markerCoordinates.add(markerCoordinatesDone.get(j));}else {
                                                                                hiddenadress=true;
                                                                            }



                                                                            markerCoordinatesDone.remove(j);
                                                                        }}catch (Exception e){}

                                                                    //Unable to invoke no-args constructor for interface com.mapbox.geojson.Geometry. Registering an InstanceCreator with Gson for this type may fix this problem.
                                                                    TaskActivity.i=j;
                                                                    TaskActivity.dateStart=currentRating.dateStart;
                                                                    Intent intent = new Intent(fullQuestActivity.this, TaskActivity.class);
                                                                    startActivity(intent);
                                                                    fab.setEnabled(true);
                                                                    // answer.setVisibility(View.VISIBLE);

                                                                    break;
                                                                }

                                                            }
                                                        }  //Player playerServer = dataSnapshot.getValue(Player.class);
                                                        // Log.d(Tag, "Value is: " + value);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {
                                                        // Failed to read value
                                                        // Log.w(TAG, "Failed to read value.", error.toException());
                                                    }
                                                });

                                            }
                                            break;
                                        }else if(k==dataSnapshot.getChildrenCount()){
                                            userCurrent.activeQuests.remove(currentQuestkey);
                                                fab.setEnabled(true);
                                                databaseFD.getReference("User").child(userCurrentKey).child("activeQuests").setValue(userCurrent.activeQuests);
                                                fab.performClick();
                                        }

                                    }

                                }

                            }else{ userCurrent.activeQuests.remove(currentQuestkey);
                                fab.setEnabled(true);
                                databaseFD.getReference("User").child(userCurrentKey).child("activeQuests").setValue(userCurrent.activeQuests);
                                fab.performClick();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Intent intent = new Intent(fullQuestActivity.this, TaskActivity.class);
                            //  startActivity(intent);
                        }
                    });

                }else if(userCurrent.endedQuests.contains(currentQuestkey)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(fullQuestActivity.this);
                    builder.setTitle("Сбросить прогресс?");
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            fab.setEnabled(true);
                        }
                    });
                    builder.setNegativeButton("Нет",(a, b) -> fab.setEnabled(true));
                    builder.setPositiveButton("Да",(a, b) -> {
                        userCurrent.endedQuests.remove(currentQuestkey);
                        databaseFD.getReference("User").child(userCurrentKey).child("endedQuests").setValue(userCurrent.endedQuests);
                        databaseReference=databaseFD.getReference("Rating");
                        Query query= databaseReference.orderByChild("keyUser").equalTo(userCurrentKey);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("MyTag", "Value is: " + dataSnapshot);
                                if (dataSnapshot.exists()) {
                                    int k=0;
                                    for (DataSnapshot issue : dataSnapshot.getChildren()) {

                                        Rating currentRating=issue.getValue(Rating.class);
                                        if(currentRating!=null){
                                            if(currentRating.keyQuest.equals(currentQuestkey)){

                                                issue.getRef().removeValue();
                                                newPlayer();
                                                break;
                                            }

                                        }
                                        k++;

                                    }
                                    if(k==dataSnapshot.getChildrenCount()){
                                        userCurrent.endedQuests.remove(currentQuestkey);
                                        databaseFD.getReference("User").child(userCurrentKey).child("endedQuests").setValue(userCurrent.endedQuests);
                                        fab.setEnabled(true);
                                        fab.performClick();
                                    }

                                }else {
                                    userCurrent.endedQuests.remove(currentQuestkey);
                                    databaseFD.getReference("User").child(userCurrentKey).child("endedQuests").setValue(userCurrent.endedQuests);
                                    fab.setEnabled(true);
                                    fab.performClick();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Intent intent = new Intent(fullQuestActivity.this, TaskActivity.class);
                                //  startActivity(intent);
                            }
                        });

                        });
                    builder.show();
                }
                else{
                    newPlayer();
               }
            }
        });


        databaseFD= FirebaseDatabase.getInstance();
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


    }

    public static String calendar_to_String(Calendar cal){
        String str="";
        int[] date_int=new int[6];

        date_int[0]= cal.get(Calendar.DAY_OF_MONTH);
        date_int[1]= cal.get(Calendar.MONTH)+1;
        date_int[2]= cal.get(Calendar.YEAR);
        date_int[3]= cal.get(Calendar.HOUR_OF_DAY);
        date_int[4]= cal.get(Calendar.MINUTE);
        date_int[5]= cal.get(Calendar.SECOND);
        for (int i = 0; i < 2; i++) {
            if(date_int[i]<10){
                str+="0"+date_int[i]+".";
            }else {str+=date_int[i]+".";}
        }
        str+=date_int[2]+"T";
        for (int i = 3; i < 5; i++) {
            if(date_int[i]<10){
                str+="0"+date_int[i]+":";
            }else {str+=date_int[i]+":";}
        }
        if(date_int[5]<10){
            str+="0"+date_int[5];
        }else {str+=date_int[5];}
        return str;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void newPlayer(){
        userCurrent.activeQuests.add(currentQuestkey);
        databaseFD.getReference("User").child(userCurrentKey).child("activeQuests").setValue(userCurrent.activeQuests);
        Rating rating = new Rating();
        rating.keyUser=userCurrentKey;
        rating.keyQuest=currentQuestkey;
        rating.lastTaskKey=currentQuest.tasks.get(0);
        rating.points=0;

        databaseReference=databaseFD.getReference("Task");

        Query query= databaseReference.orderByKey().equalTo(currentQuest.tasks.get(0));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        if(issue!=null){
                            Task task = issue.getValue(Task.class);
                            rating.attemptsOnLastTask=task.attempts;
                            databaseReference = databaseFD.getReference("Rating");
                            TaskActivity.currentAttempts=rating.attemptsOnLastTask;
                            List<Feature>markerCoordinatesTask=new ArrayList<>();
                            markerCoordinatesTask.add(Feature.fromGeometry(
                                    Point.fromLngLat( task.longitude, task.latitude)));
                            rating.coordinates= FeatureCollection.fromFeatures(markerCoordinatesTask).toJson();

                            Calendar cal = Calendar.getInstance();
                            rating.dateStart=calendar_to_String(cal);
                            TaskActivity.dateStart=calendar_to_String(cal);
                            if(!task.hiddenAddress){
                            markerCoordinates.add(markerCoordinatesTask.get(0));}
                            databaseReference.push().setValue(rating, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    ratingKey=databaseReference.getKey();
                                    Intent intent = new Intent(fullQuestActivity.this, TaskActivity.class);
                                    startActivity(intent);
                                    fab.setEnabled(true);
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //перевод изображения в строку
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }


    // переводит строку в изображение
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
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
