package com.example.questory3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;

import com.example.questory3.recycleQuest.Content;
import com.example.questory3.tables.Answer;
import com.example.questory3.tables.LibraryTask;
import com.example.questory3.tables.Quest;
import com.example.questory3.tables.Task;
import com.example.questory3.tables.User;
import com.example.questory3.ui.findQuests.FindQuestsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkmmte.view.CircularImageView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.sql.Time;
import java.util.ArrayList;

import static com.example.questory3.EmailPasswordActivity.databaseFD;
import static com.example.questory3.EmailPasswordActivity.databaseReference;
import static com.example.questory3.EmailPasswordActivity.myPref;
import static com.example.questory3.EmailPasswordActivity.userCurrent;
import static com.example.questory3.EmailPasswordActivity.userCurrentKey;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static FloatingActionButton fab;
    public static boolean backitem=false;
    public static Context context_main_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context_main_activity=MainActivity.this;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        updateUserInfo();

        databaseFD = FirebaseDatabase.getInstance();
        databaseReference=databaseFD.getReference("User");
        databaseReference.child(userCurrentKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    if(dataSnapshot.getKey().equals(userCurrentKey)){
                userCurrent=dataSnapshot.getValue(User.class);
                updateUserInfo();}}catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_message, R.id.nav_forcheck, R.id.nav_findquests,
                R.id.nav_myquests, R.id.nav_startedquests, R.id.nav_endedquests
                //, R.id.nav_share, R.id.nav_send
        )
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    void updateUserInfo(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        CircularImageView avatar=navHeader.findViewById(R.id.avatar);
        //TODO раскомменть меня
        if(userCurrent!=null){
        if(userCurrent.avatar!=null){
            //TODO получить картинку см ниже
            Bitmap ava =BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(userCurrent.avatar, "drawable", getPackageName()));
            avatar.setImageBitmap(ava);}

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatar.setEnabled(false);
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                avatar.setEnabled(true);
            }
        });
        if(userCurrent.username!=null){
            TextView usernametv=navHeader.findViewById(R.id.username);
            usernametv.setText(userCurrent.username);
        }}
    }

    @Override
    public boolean onSupportNavigateUp() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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


}
