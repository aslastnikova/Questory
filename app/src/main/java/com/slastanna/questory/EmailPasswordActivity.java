package com.slastanna.questory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Parcelable;
import android.renderscript.ScriptGroup;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.slastanna.questory.Services.NotificationService;
import com.slastanna.questory.tables.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static DatabaseReference databaseReference;
    public static FirebaseDatabase databaseFD;
    public static SharedPreferences myPref;

    private EditText ETemail;
    ActionProcessButton btnSignIn;
    public  static User userCurrent;
    public static String userCurrentKey;
    private EditText ETpassword;
    private EditText ETnickname, ETname, ETsurname;
    private ImageView avatar;
    ImageView previousImageView;
    ProgressBar progressBar;
    boolean registration=true;
    View mDecorView;
    boolean wasAlreadySigned=false;
    boolean waiting_for_verify;
    SharedPreferences.Editor editor;
    int tries;
    TextView choose_date;
    TextView privacy;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.loading_layout);
        context=this;
        FirebaseApp.initializeApp(this);

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }



        //mDecorView = getWindow().getDecorView();
        //onWindowFocusChanged(true);
        mAuth = FirebaseAuth.getInstance();
        databaseFD = FirebaseDatabase.getInstance();
        databaseReference = databaseFD.getInstance().getReference();
        myPref =  getSharedPreferences("Questory_user_base_1542", Context.MODE_PRIVATE);

        editor = myPref.edit();


        String email= myPref.getString("email","");
        String password= myPref.getString("password", "");




        //вход в аккаунт/регистрация пользователя
        if(!(email.length()>0 && password.length()>0)){
            enteranceClick();
        }
        //Вход переход на навигационное меню
        else{
            wasAlreadySigned=true;
            setContentView(R.layout.loading_layout);
            ((TextView)findViewById(R.id.versionView)).setText("v. "+BuildConfig.VERSION_NAME);
            signin(email, password);
        }


    }
    private void openEmailApp() {
        List<Intent> emailAppLauncherIntents = new ArrayList<>();

        //Intent that only email apps can handle:
        Intent emailAppIntent = new Intent(Intent.ACTION_SENDTO);
        emailAppIntent.setData(Uri.parse("mailto:"));
        emailAppIntent.putExtra(Intent.EXTRA_EMAIL, "");
        emailAppIntent.putExtra(Intent.EXTRA_SUBJECT, "");

        PackageManager packageManager = getPackageManager();

        //All installed apps that can handle email intent:
        List<ResolveInfo> emailApps = packageManager.queryIntentActivities(emailAppIntent, PackageManager.MATCH_ALL);

        for (ResolveInfo resolveInfo : emailApps) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            emailAppLauncherIntents.add(launchIntent);
        }

        //Create chooser
        Intent chooserIntent = Intent.createChooser(new Intent(), "Select email app:");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, emailAppLauncherIntents.toArray(new Parcelable[emailAppLauncherIntents.size()]));
        startActivity(chooserIntent);
    }

    @Override
    public void onClick(View view) {
        if(!registration)
        {
            btnSignIn.setProgress(1);
            signin(ETemail.getText().toString(),ETpassword.getText().toString());
        }else
        {
            btnSignIn.setProgress(1);
            registration(ETnickname.getText().toString().trim(),ETemail.getText().toString().trim(),ETpassword.getText().toString().trim(),
                    ETname.getText().toString().trim(), ETsurname.getText().toString().trim(), choose_date.getText().toString().trim());
        }

    }

    public void registrationClick(){
        registration=true;
        setContentView(R.layout.activity_firebase_ui_registration2);
        userCurrent=new User();
        ETemail = (EditText) findViewById(R.id.editEmail);
        ETpassword = (EditText) findViewById(R.id.editPassword);
        ETpassword.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC);
        ETnickname = (EditText) findViewById(R.id.editName);
        ETsurname= (EditText) findViewById(R.id.editSurname);
        ETname= (EditText) findViewById(R.id.editNameReal);
        avatar=findViewById(R.id.avatarchoose);
        ETpassword.setCameraDistance(100);
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if(isOpen){
                            findViewById(R.id.linhidden).setVisibility(View.VISIBLE);
                        }else{findViewById(R.id.linhidden).setVisibility(View.GONE);}
                    }
                });

        ETpassword.setOnKeyListener(new View.OnKeyListener() {
               public boolean onKey(View v, int keyCode, KeyEvent event)
               {
                   Log.d("MyTag", "keycode: "+keyCode);
                   Log.d("MyTag", "keyevent: "+event);
                   if (event.getAction() == KeyEvent.ACTION_DOWN)
                       if ((keyCode == KeyEvent.KEYCODE_ENTER))
                       {    // Это событие не наступает

                       }else{}
                   return true;
               }
           }
        );

        privacy=findViewById(R.id.privacypolicy1);
        privacy.setText(Html.fromHtml("<a href=https://drive.google.com/file/d/1Z1r2_SqR2YRdbQfbeWbYYMW4Xs-kXmAT/view?usp=sharing><font color=#FFFFFF>Вы даете свое согласие на обработку персональных данных</font></a>"));
        privacy.setLinksClickable(true);
        privacy.setLinkTextColor(Color.WHITE);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri adress= Uri.parse("https://questory-site.herokuapp.com/privacyPolicy.php");
                Intent browser= new Intent(Intent.ACTION_VIEW, adress);
                //browser.setDataAndType(adress, "application/pdf");
                try{
                context.startActivity(browser);}catch (Exception e){
                    Log.d("MyTag", "Browser: "+e);
                }
            }
        });
       // privacy.setMovementMethod(LinkMovementMethod.getInstance());

        //privacy.setOnLongClickListener();
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(EmailPasswordActivity.this);

                View view =(LinearLayout) getLayoutInflater().inflate(R.layout.choose_avatar, null);
                ArrayList<ImageView> avatars= new ArrayList<>();
                avatars.add((ImageView)view.findViewById(R.id.imageView1));
                avatars.add((ImageView)view.findViewById(R.id.imageView2));
                avatars.add((ImageView)view.findViewById(R.id.imageView3));
                avatars.add((ImageView)view.findViewById(R.id.imageView4));
                avatars.add((ImageView)view.findViewById(R.id.imageView5));
                avatars.add((ImageView)view.findViewById(R.id.imageView6));
                avatars.add((ImageView)view.findViewById(R.id.imageView7));
                avatars.add((ImageView)view.findViewById(R.id.imageView8));
                avatars.add((ImageView)view.findViewById(R.id.imageView9));
                avatars.add((ImageView)view.findViewById(R.id.imageView10));
                avatars.add((ImageView)view.findViewById(R.id.imageView11));
                avatars.add((ImageView)view.findViewById(R.id.imageView12));
                for(int i=1; i<13; i++){
                    avatars.get(i-1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageView ava = (ImageView) v;

                            String tag=v.getTag().toString();
                            userCurrent.avatar=tag;
                            avatar.setImageDrawable(ava.getDrawable());
                            Drawable bck=null;

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                if(previousImageView!=null){
                                    previousImageView.setBackground(null);
                                }
                                bck = getDrawable(R.drawable.background_shape_dark);
                                ava.setBackground(bck);
                                previousImageView=ava;
                            }

                    }
                    });
                }
                builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface a, int b) {
                    }
                });
                builder.setView(view);
                builder.show();
            }
        });
        TextView alreadyhasaccount = findViewById(R.id.alreadyregistered);
        alreadyhasaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteranceClick();
            }
        });

        choose_date=findViewById(R.id.date_choose);
        choose_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Выберите дату рождения");

                MaterialDatePicker<Long> picker = builder.build();
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        //picker.getSelection().intValue()
                        Log.d("MyTag", ""+selection.intValue());
                        //Date date = Date.from(selection);
                        Date date = new Date(selection);

                        choose_date.setText(date_to_String(date));
                        choose_date.setTextColor(Color.BLACK);
                        Log.d("MyTag", ""+date);
                    }
                });

                picker.show(getSupportFragmentManager(), picker.toString());
            }
        });

        //URL privacyPolicy=new URL("https://drive.google.com/file/d/1Z1r2_SqR2YRdbQfbeWbYYMW4Xs-kXmAT/view?usp=sharing");
        //<a href=https://drive.google.com/file/d/1Z1r2_SqR2YRdbQfbeWbYYMW4Xs-kXmAT/view?usp=sharing><font color=#AAA>Your Text</font></a>
        btnSignIn = (ActionProcessButton) findViewById(R.id.btn_register);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSignIn.setOnClickListener(this);

    }
    public String date_to_String(Date date){
        String str="";
        int[] date_int=new int[3];
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);
        date_int[2]= cal.get(Calendar.YEAR);
        date_int[1]= cal.get(Calendar.MONTH)+1;
        date_int[0]= cal.get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < 2; i++) {
            if(date_int[i]<10){
                str+="0"+date_int[i]+".";
            }else {str+=date_int[i]+".";}
        }
        str+=date_int[2];
        return str;
    }

    public void enteranceClick(){
        registration=false;
        setContentView(R.layout.activity_firebase_enterance);
        userCurrent=new User();
        ETemail = (EditText) findViewById(R.id.editEmail);
        ETpassword = (EditText) findViewById(R.id.editPassword);
        TextView alreadyhasaccount = findViewById(R.id.alreadyregistered);
        alreadyhasaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationClick();
            }
        });
        btnSignIn = (ActionProcessButton) findViewById(R.id.btn_enterance);
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
        btnSignIn.setOnClickListener(this);
    }
    public void verifyClick(String email){
        setContentView(R.layout.verify_email);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteranceClick();
            }
        });
        TextView txt =findViewById(R.id.verify_text);
        txt.setText(getResources().getString(R.string.verify_text)+" "+email);
        findViewById(R.id.verify_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waiting_for_verify=true;
                openEmailApp();
            }
        });
        findViewById(R.id.verify_text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waiting_for_verify=true;
                verify_email(mAuth.getCurrentUser());
            }
        });
    }
    public void verify_email(FirebaseUser firebaseUser){
        if (!firebaseUser.isEmailVerified()) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        verifyClick(firebaseUser.getEmail());

                    } else {
                        btnSignIn.setProgress(-1);
                        btnSignIn.setText("Попробовать еще раз");
                        Log.e("MyTag", "sendEmailVerification: "+task.getException());
                        Toast.makeText(getApplicationContext(), "Проблемы с отправкой верификационного письма. Попробуйте позже.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    //вход в аккаунт
    public void signin(final String email , final String pass)
    {

        if (email.equals(""))
        {Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, введите имя пользователя", Toast.LENGTH_SHORT).show();}
        else if(pass.equals("")){
            Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, введите пароль", Toast.LENGTH_SHORT).show();
        }else if(wasAlreadySigned&&(email.equals("")||pass.equals(""))){
            enteranceClick();
        }
        else{
            if(isOnline()){
//                progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    databaseReference = databaseFD.getReference("User");
                    //Log.d("MyTag", "Key_Char_Serv:"+keyMonster);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user.isEmailVerified()){
                    Query query= databaseReference.orderByChild("email").equalTo(email);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("MyTag", "Value is: " + dataSnapshot);
                            if (dataSnapshot.exists()) {

                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    userCurrent=issue.getValue(User.class);

                                    if(userCurrent!=null){
                                        if(wasAlreadySigned){
                                            userCurrentKey=issue.getKey();
                                            editor.putString("userKey", userCurrentKey);
                                            editor.commit();
                                            //startService(new Intent(EmailPasswordActivity.this, NotificationService.class));
                                            Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
                                            startActivity(intent); finish();
                                        }else{
                                            btnSignIn.setProgress(100);
                                            btnSignIn.setText("Успешно");
                                        Toast.makeText(EmailPasswordActivity.this, "Вы авторизированы", Toast.LENGTH_SHORT).show();
                                        CheckBox checkBox = findViewById(R.id.remember_me);
                                        if(checkBox.isChecked()){

                                            editor.putString("email", userCurrent.email);
                                            editor.putString("password", userCurrent.password);

                                            editor.commit();
                                        }
                                        userCurrentKey=issue.getKey();
                                        editor.putString("userKey", userCurrentKey);
                                        editor.commit();
                                        //startService(new Intent(EmailPasswordActivity.this, NotificationService.class));

                                        Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
                                        startActivity(intent); finish();}
                                        break;
                                    }

                                }
                            }else{
                                SharedPreferences.Editor editor = myPref.edit();
                                editor.putString("email", null);
                                editor.putString("password", null);
                                editor.putString("userKey", null);
                                editor.commit();
                                enteranceClick();
                                btnSignIn.setProgress(-1);
                                btnSignIn.setText("Попробовать еще раз");
                                Toast.makeText(EmailPasswordActivity.this, "Неправильный email или пароль. Вы не авториованы", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {


                                SharedPreferences.Editor editor = myPref.edit();
                                editor.putString("email", null);
                                editor.putString("password", null);
                                editor.putString("userKey", null);
                                editor.commit();

                            enteranceClick();
                            btnSignIn.setProgress(-1);
                            btnSignIn.setText("Попробовать еще раз");
                            Toast.makeText(EmailPasswordActivity.this, "Неправильный email или пароль. Вы не авториованы", Toast.LENGTH_SHORT).show();
                        }
                    });}else{
                        verify_email(user);
                    }



                }else{
                    SharedPreferences.Editor editor = myPref.edit();
                    editor.putString("email", null);
                    editor.putString("password", null);
                    editor.putString("userKey", null);
                    editor.commit();
                    enteranceClick();
                }

            }
        });}
            // offline
            else{
                setContentView(R.layout.activity_firebase_enterance);
                Toast.makeText(EmailPasswordActivity.this, "Нет интернет-соединения", Toast.LENGTH_SHORT).show();
                if(btnSignIn!=null) {

                    btnSignIn.setProgress(-1);
                    btnSignIn.setText("Попробовать еще раз");

                }else if(tries<5){
                    tries++;
                    signin(email, pass);}else{enteranceClick();}
            }
        }
    }
    //регистрация аккаунта
    public void registration (final String username, final String email , final String password, String firstname, String surname, String date){
        Date curdate = new Date();
        //Calendar calendar = Calendar.getInstance().get(Calendar.YEAR);;

        Log.d("Mytag", "calendar= "+Calendar.getInstance().get(Calendar.YEAR));
        CheckBox pp=findViewById(R.id.privacypolicy);
        if(email.equals("guest"))
        {   btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, выберите другой никнейм", Toast.LENGTH_SHORT).show();
        }
        else if (password.equals(""))
        {   btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, введите пароль", Toast.LENGTH_SHORT).show();
        }else if (password.length()<6)
        {   btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Пароль должен быть 6 и более символов", Toast.LENGTH_SHORT).show();
        }else if(userCurrent.avatar.equals(null)||userCurrent.avatar.equals("")){
            btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, выберите аватар", Toast.LENGTH_SHORT).show();
        }else if(username.length()<6){
            btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробывать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Никнейм должен быть длиннее 6 символов", Toast.LENGTH_SHORT).show();
        }else if(firstname.equals("")){
            btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, введите имя", Toast.LENGTH_SHORT).show();
        }else if(surname.equals("")){
            btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, введите фамилию", Toast.LENGTH_SHORT).show();
        }else if(email.equals("")){
            btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, введите почту", Toast.LENGTH_SHORT).show();
        }else if(!date.equals("Дата рождения")&&(Calendar.getInstance().get(Calendar.YEAR)-Integer.parseInt(date.substring(6)))<13){
            btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Log.d("Mytag", "curdate= "+Calendar.getInstance().get(Calendar.YEAR));
            Log.d("Mytag", "datesubstring= "+Integer.parseInt(date.substring(6)));
            Log.d("Mytag", "result= "+(curdate.getYear()-Integer.parseInt(date.substring(6))));
            Toast.makeText(EmailPasswordActivity.this, "Извините, но вы слишком молоды", Toast.LENGTH_SHORT).show();
        }
        else if(!pp.isChecked()){
            btnSignIn.setProgress(-1);
            btnSignIn.setText("Попробовать еще раз");
            Toast.makeText(EmailPasswordActivity.this, "Пожалуйста, дайте согласие на обработку персональнх данных", Toast.LENGTH_SHORT).show();
        }
        else{
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    btnSignIn.setProgress(100);
                    btnSignIn.setText("Успешно");
                    userCurrent.username=username;
                    userCurrent.email=email;
                    userCurrent.password=password;
                    userCurrent.firstname=firstname;
                    userCurrent.surname=surname;
                    userCurrent.birth_date=date;
                    // отправляем данные в Realtime database FB
                    databaseFD.getReference("User").push().setValue(userCurrent, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            CheckBox checkBox = findViewById(R.id.remember_me);
                            if(checkBox.isChecked()){
                                SharedPreferences.Editor editor = myPref.edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putString("userKey", databaseReference.getKey());
                                editor.commit();
                            }
                            verify_email(firebaseUser);
                            userCurrentKey= databaseReference.getKey();

                        }
                    });

                }
                else{
                    btnSignIn.setProgress(-1);
                    btnSignIn.setText("Попробовать еще раз");
                    Toast.makeText(EmailPasswordActivity.this, "Ошибка соединения. Регистрация провалена", Toast.LENGTH_SHORT).show();
                Log.d("MyTag", "Error: "+ task.getException());}
            }
        });

        }
    }

    //проверка на наличие интернет соединения
    public static Boolean isOnline() {
        try {
            Process p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }




    //скрыть ленту навигации
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(waiting_for_verify){
            mAuth = FirebaseAuth.getInstance();
            final Task<Void> userTask = mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseUser firebaseUser =mAuth.getCurrentUser();
                    boolean ver =firebaseUser.isEmailVerified();
                    String email  = firebaseUser.getEmail();
                    Log.d("MyTag", "verified:"+email+ver);
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(EmailPasswordActivity.this, "Вы авторизированы", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
                        startActivity(intent); finish();}
                }
            });




        }
    }
}

