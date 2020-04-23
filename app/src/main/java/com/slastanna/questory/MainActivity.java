package com.slastanna.questory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.firebase.database.Query;
import com.slastanna.questory.tables.Rating;
import com.slastanna.questory.tables.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkmmte.view.CircularImageView;
import com.slastanna.questory.ui.fullQuest.fullQuestActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.myPref;
import static com.slastanna.questory.EmailPasswordActivity.userCurrent;
import static com.slastanna.questory.EmailPasswordActivity.userCurrentKey;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static FloatingActionButton fab;
    public static boolean backitem=false;
    public static Context context_main_activity;
    TextView usernametv;
    CircularImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context_main_activity=MainActivity.this;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);

        fab.hide();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        updateUserInfo();
        getUserData();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_message, R.id.nav_forcheck, R.id.nav_findquests,
                //R.id.nav_myquests,
                R.id.nav_startedquests, R.id.nav_endedquests
                //, R.id.nav_share, R.id.nav_send
        )
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    @Override
    protected void onResume() {
        super.onResume();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        usernametv=navHeader.findViewById(R.id.username);
        if(usernametv.getText().toString().equals("Ошибка")){
            getUserData();
        }
    }

    void getUserData(){
        databaseFD = FirebaseDatabase.getInstance();
        databaseReference=databaseFD.getReference("User");
        if(userCurrentKey!=null){
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
            });}else{
            SharedPreferences.Editor editor = myPref.edit();
            editor.putString("email", null);
            editor.putString("password", null);
            editor.commit();
            Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
            startActivity(intent);finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

   public void updateUserInfo(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);

        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Выйти из аккаунта?");

                builder.setNegativeButton("Нет",(a, b) -> fab.setEnabled(true));
                builder.setPositiveButton("Да",(a, b) -> {

                    SharedPreferences.Editor editor = myPref.edit();
                    editor.putString("email", null);
                    editor.putString("password", null);
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, EmailPasswordActivity.class);
                    startActivity(intent);finish();

                });
                builder.show();
            }
        });
        CircularImageView avatar=navHeader.findViewById(R.id.avatar);
        photo=avatar;

        //TODO раскомменть меня
        if(userCurrent!=null){
        if(userCurrent.avatar!=null){
            //TODO получить картинку см ниже
            Bitmap ava =BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(userCurrent.avatar, "drawable", getPackageName()));
            avatar.setImageBitmap(ava);}

//        avatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                avatar.setEnabled(false);
//                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(intent);
//                avatar.setEnabled(true);
//            }
//        });
        if(userCurrent.username!=null){
            usernametv=navHeader.findViewById(R.id.username);

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
