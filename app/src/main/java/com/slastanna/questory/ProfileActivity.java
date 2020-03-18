package com.slastanna.questory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.slastanna.questory.recyclerFriends.AdapterFriends;
import com.slastanna.questory.recyclerFriends.ContentFriends;
import com.slastanna.questory.tables.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.myPref;
import static com.slastanna.questory.EmailPasswordActivity.userCurrent;
import static com.slastanna.questory.EmailPasswordActivity.userCurrentKey;

public class ProfileActivity extends AppCompatActivity {
        FloatingActionButton fab;
        ImageView user_avater;
        TextView user_name;
        TextView exit;
        RecyclerView recyclerFriends;
        AdapterFriends adapterFriends, adapterAllUsers;
        static HashSet<ContentFriends> contentsMyFriends = new HashSet<>();
        static HashSet<ContentFriends> contentsAllusers= new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_for_activities);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        View layoutTask =  getLayoutInflater().inflate(R.layout.profile_menu, null);
        LinearLayout parent = (LinearLayout) findViewById(R.id.include);
        View layout = findViewById(R.id.active_layout);
        parent.removeView(layout);
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parent.addView(layoutTask, linLayoutParam);

        fab = findViewById(R.id.fab);
        fab.hide();

        recyclerFriends = findViewById(R.id.recycleFriends);
        recyclerFriends.setHasFixedSize(false);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        //contentsMyFriends.clear();
        adapterFriends = new AdapterFriends(contentsMyFriends, this);
        adapterAllUsers = new AdapterFriends(contentsAllusers, this);
        adapterFriends.friends=true;
        adapterAllUsers.friends=false;
        recyclerFriends.setLayoutManager(manager);
        recyclerFriends.setAdapter(adapterFriends);




        recyclerFriends=findViewById(R.id.recycleFriends);
        user_avater=findViewById(R.id.userava);
        if(userCurrent.avatar!=null){
            //TODO получить картинку см ниже
            Bitmap ava = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(userCurrent.avatar, "drawable", getPackageName()));
            user_avater.setImageBitmap(ava);}
        user_name=findViewById(R.id.username);
        user_name.setText(userCurrent.username);
        exit=findViewById(R.id.exitbutton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = myPref.edit();
                editor.putString("email", null);
                editor.putString("password", null);
                editor.commit();
                Intent intent = new Intent(ProfileActivity.this, EmailPasswordActivity.class);
                startActivity(intent);finish();
            }
        });
        for (int i = 0; i < userCurrent.friends.size(); i++) {
            fillContent(userCurrent.friends.get(i));
        }

        EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //contentsAllusers.clear();
                //adapterAllUsers.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //contentsAllusers.clear();
                //adapterAllUsers.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                Query query =databaseFD.getReference("User");
                contentsAllusers.clear();
                adapterAllUsers.notifyDataSetChanged();
                recyclerFriends.setAdapter(adapterAllUsers);
                if(s!=null&&!s.equals("")&&s.length()!=0){
                query.orderByChild("username").startAt(s.toString()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String str) {
                        Log.d("MyTag", dataSnapshot.getKey());
                        if (dataSnapshot.exists()) {
                            try {
                                User user = dataSnapshot.getValue(User.class);
                                if (user != null&&user.username.startsWith(s.toString())&&!user.username.equals(userCurrent)) {

                                    ContentFriends content = new ContentFriends();
                                    content.avatar = BitmapFactory.decodeResource(getResources(),
                                            getResources().getIdentifier(user.avatar, "drawable", getPackageName()));
                                    content.keyUser = dataSnapshot.getKey();
                                    content.name = user.username;
                                    if(userCurrent.friends.contains(dataSnapshot.getKey())||userCurrentKey==dataSnapshot.getKey()){
                                        content.friend=true;
                                    }
                                    if(!contentsAllusers.contains(content)){
                                    contentsAllusers.add(content);
                                    adapterAllUsers.notifyDataSetChanged();
                                    }

                                }
                            }catch (Exception e){}

                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }else{
                    contentsAllusers.clear();
                    adapterAllUsers.notifyDataSetChanged();
                    recyclerFriends.setAdapter(adapterFriends);
                }}
        });


    }
    void fillContent(String key){
        // TODO убрать
        databaseReference = databaseFD.getReference("User").child(key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user= dataSnapshot.getValue(User.class);
                if(user!=null){
                        try{
                            ContentFriends content = new ContentFriends();
                            content.avatar = BitmapFactory.decodeResource(getResources(),
                                    getResources().getIdentifier(user.avatar, "drawable", getPackageName()));
                            //content.avatar =user.avatar;
                            content.keyUser=dataSnapshot.getKey();
                            content.name=user.username;
                            content.friend=true;
                            contentsMyFriends.add(content);
                            adapterFriends.notifyDataSetChanged();}catch (Exception e) {
                            Log.d("MyTag", "Exception:" + e);
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        contentsMyFriends.clear();
        contentsAllusers.clear();
    }

    // Получить позицию измененного contentsMyFriends
    private int getItemIndex(ContentFriends content, HashSet set){
        int index=-1;
        ArrayList<ContentFriends> contents = new ArrayList(set);
        for(int i = 0; i< contentsMyFriends.size(); i++){
            if(contents.get(i).keyUser.equals(content.keyUser))
            {index=i;break;}
        }
        return index;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
