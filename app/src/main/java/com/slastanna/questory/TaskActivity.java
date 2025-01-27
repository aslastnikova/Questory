package com.slastanna.questory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.slastanna.questory.tables.Answer;
import com.slastanna.questory.tables.Rating;
import com.slastanna.questory.tables.Task;
import com.slastanna.questory.tables.User;
import com.slastanna.questory.ui.fullQuest.fullQuestActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.userCurrent;
import static com.slastanna.questory.EmailPasswordActivity.userCurrentKey;
//import static com.slastanna.questory.MapActivity.getGPS;
//import static com.slastanna.questory.MapActivity.gotGPS;
import static com.slastanna.questory.MapActivity.hiddenadress;
import static com.slastanna.questory.MapActivity.markerCoordinates;
import static com.slastanna.questory.MapActivity.markerCoordinatesDone;
import static com.slastanna.questory.ui.fullQuest.fullQuestActivity.currentQuest;
import static com.slastanna.questory.ui.fullQuest.fullQuestActivity.currentQuestkey;

public class TaskActivity extends AppCompatActivity {


    public static FloatingActionButton fab;
    TextView header, address, text, attempts;
    ImageView image;
    EditText answer;
    public static Task currentTask;
    Button sendAnswer;
    public static int currentAttempts, currentPoints;
    public static String ratingKey;
    public static ArrayList<String> tasks=new ArrayList<>();
    public static int i=0;
    static final int GALLERY_REQUEST = 1;
    public static String dateStart;
    private static final int CAMERA_REQUEST = 0;
    Button takephoto;
    Button gallery;
    ImageView skip;
    ImageView photopicker, hint;
    ProgressLine progressLine;
    Bitmap answerPicture;
    AlertDialog dialog;
     Answer userAnswer;
    boolean sendEnable;
    public static boolean hint_taken;
    LinearLayout parent;
    public static boolean gpsAnswer;
    String currentPhotoPath;
    DrawerLayout drawerLayout;
    Context context;
    Activity activity;
    boolean endedQuest;
    public static Rating currentRating;
    View layoutTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        activity=this;



        drawerLayout = (DrawerLayout)getLayoutInflater().inflate(R.layout.drawer_layout_for_activities, null);
        setContentView(drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        layoutTask =  getLayoutInflater().inflate(R.layout.activity_task, null);
        parent = (LinearLayout) findViewById(R.id.include);
        View layout = findViewById(R.id.active_layout);
        parent.removeView(layout);
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parent.addView(layoutTask, linLayoutParam);
        fab = findViewById(R.id.fab);
        //fab.hide();
        fab.setImageResource(R.drawable.outline_explore_24);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getGPS=false;
                fab.setEnabled(false);
                Intent intent = new Intent(TaskActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });


        findViewById(R.id.scrollview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action == MotionEvent.ACTION_DOWN){
                    fab.setVisibility(View.GONE);
                }else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
                    fab.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });
        progressLine = findViewById(R.id.progressLine);

        header = findViewById(R.id.theader);
        address = findViewById(R.id.taddress);
        text= findViewById(R.id.ttext);
        image = findViewById(R.id.timage);
        attempts= findViewById(R.id.attempts);
        answer=findViewById(R.id.editTextAnswer);
        sendAnswer = findViewById(R.id.buttonSendAnswer);
        hint=findViewById(R.id.hint);
        skip=findViewById(R.id.skip);


            try {
                if (tasks.get(i) != null) {
                    setTask(tasks.get(i));
                }
            } catch (Exception e) {
                Log.d("MyTag", "tasks=" + tasks);
            }

    }


    @Override
    public boolean onSupportNavigateUp() {
        updateRating();
        onBackPressed();
        return true;
    }


    public void setTask(String key){
        databaseReference=databaseFD.getReference("Task");


        Query query= databaseReference.orderByKey().equalTo(key);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Log.d("MyTag", "Value is: " + dataSnapshot);
                if (dataSnapshot.exists()) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        Log.d("MyTag", "issue: "+issue);
                         currentTask=issue.getValue(Task.class);

                        if(currentTask!=null){
                            if(currentAttempts==0){
                            currentAttempts = currentTask.attempts;}
                            currentRating.progress.get(i).coordinates=Feature.fromGeometry(
                                    Point.fromLngLat( currentTask.longitude, currentTask.latitude)).toJson();
                            markerCoordinates.clear();
                            if(!currentTask.hiddenAddress){
                                hiddenadress=false;
                                markerCoordinates.add(Feature.fromJson(currentRating.progress.get(i).coordinates));
                                currentRating.progress.get(i).mapstate=0;
                            }else {
                                hiddenadress=true;
                                currentRating.progress.get(i).mapstate=1;
                            }

                            updateRating();
                            fillText(currentTask);
                            sendAnswer.setVisibility(View.VISIBLE);

                            break;
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    void fillText(final Task task) {
        hideSoftKeyboard(answer);
        setTextifnotnull(task.address, address);
        setTextifnotnull(task.taskHeading, header);
        setTextifnotnull(task.taskText, text);
        if(currentAttempts==0){currentAttempts=task.attempts;}
        gpsAnswer=false;
        skip.setVisibility(View.VISIBLE);
        attempts.setText("Осталось попыток: " + currentAttempts);
        if (!(task.tpicture==null || task.tpicture.equals("")||task.tpicture.equals("picture"))) {
            image.setVisibility(View.VISIBLE);
            findViewById(R.id.divider2).setVisibility(View.VISIBLE);
            image.setImageBitmap(decodeBase64(task.tpicture));
        }else{
            image.setVisibility(View.GONE);
            findViewById(R.id.divider2).setVisibility(View.GONE);
        }
        if(task.hint!=null&&!task.hint.equals("")){
            hint.setVisibility(View.VISIBLE);
            hint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!hint_taken){
                        AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
                        builder.setTitle("Потратить один балл, чтобы получить подсказку?");
                        builder.setPositiveButton("Да", (a, b) -> {
                            hint_taken=true;
                            currentAttempts-=currentTask.hintPoints;
                            attempts.setText("Осталось попыток: " + currentAttempts);
                            showPopupWindow(parent, task.hint, false);
                        });
                        builder.setNegativeButton("Нет", (a, b) ->{});
                        builder.show();
                    }else {showPopupWindow(parent, currentTask.hint, false);}

                }
            });
        }else{hint.setVisibility(View.GONE);}


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskActivity.this);
                builder.setTitle("Хотите пропустить задание? Вы не получите за него баллы.");
                builder.setPositiveButton("Да", (a, b) -> {
                    currentRating.progress.get(i).state=1;
                    isitEnd();
                });
                builder.setNegativeButton("Нет", (a, b) ->{});
                builder.show();
            }
        });


        //TODO test new progressbar
        ArrayList<Integer> steps = new ArrayList<>();
        for (int j = 0; j < currentRating.progress.size(); j++) {
            steps.add(currentRating.progress.get(j).state);
        }

        progressLine.setLines(steps);
        progressLine.invalidate();
        progressLine.setVisibility(View.VISIBLE);


        if(task.typeTask!=null){
            userAnswer = new Answer();
            userAnswer.userKey=userCurrentKey;
            userAnswer.userName=userCurrent.username;
            userAnswer.taskText=currentTask.taskText;
            switch (task.typeTask){
                case "text":
                    answer.setVisibility(View.VISIBLE);
                    if(currentTask.forAdmin){
                    attempts.setVisibility(View.GONE);}else{attempts.setVisibility(View.VISIBLE);}
                    sendAnswer.setText("Отправить ответ");
                    sendAnswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(getApplicationContext(), "11111" ,Toast.LENGTH_SHORT).show();
                            if(EmailPasswordActivity.isOnline()){
                                if(!(String.valueOf(answer.getText()).equals("")||String.valueOf(answer.getText()).equals(null))){
                                    if(!currentTask.forAdmin){
                                        Log.d("MyTag", ""+String.valueOf(answer.getText()));
                                        if(String.valueOf(answer.getText()).equals(currentTask.rightAnswer)){

                                            currentPoints+=task.taskCost -(task.attempts-currentAttempts);
                                            if(task.attempts-currentAttempts==0){
                                                currentRating.progress.get(i).state=3;
                                            }else{
                                                currentRating.progress.get(i).state=2;}
                                            Toast.makeText(TaskActivity.this, "Правильный ответ. Набрано баллов: "+currentPoints ,Toast.LENGTH_SHORT).show();
                                            isitEnd();
                                        }else{

                                            if(currentAttempts>1){
                                                Toast.makeText(TaskActivity.this, "Попробуйте ещё раз" ,Toast.LENGTH_SHORT).show();
                                                answer.setText("");
                                                currentAttempts--;
                                                if(currentTask.hintPoints>=currentAttempts){
                                                    hint.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Toast.makeText(TaskActivity.this, "У вас недостаточно баллов, чтобы получить подсказку" ,Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                attempts.setText("Осталось попыток: " + currentAttempts); }
                                            else{
                                                Toast.makeText(TaskActivity.this, "К сожалению, попытки закончились. Вы не получите баллы за это задание." ,Toast.LENGTH_SHORT).show();
                                                currentRating.progress.get(i).state=1;
                                                isitEnd(); }
                                        }

                                    }else{
                                        userAnswer.answer=String.valueOf(answer.getText());
                                        userAnswer.questKey=currentQuestkey;
                                        userAnswer.points=currentTask.attempts;
                                        sendtoAdmins();
                                    }
                                }else{Toast.makeText(TaskActivity.this,"Введите ответ", Toast.LENGTH_SHORT).show();}

                        }else{Toast.makeText(TaskActivity.this,"Интернет-соединение отсутствует", Toast.LENGTH_SHORT).show();}
                        }
                    }); break;
                case "photo":
                    attempts.setVisibility(View.GONE);
                    answer.setVisibility(View.GONE);
                    sendAnswer.setText("Отправить ответ");
                    sendAnswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(EmailPasswordActivity.isOnline()){
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(TaskActivity.this);
                            View view =getLayoutInflater().inflate(R.layout.choose_picture, null);
                            takephoto = view.findViewById(R.id.makePhoto);
                            gallery = view.findViewById(R.id.gallery);
                            photopicker = (ImageView) view.findViewById(R.id.photopicker);
                            gallery.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

                                }
                            });

                            takephoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dispatchTakePictureIntent();
                                }
                            });

                            builder.setPositiveButton("Отправить", (a, b) -> {
                             if(answerPicture!=null){
                                 //userAnswer=new Answer();
                                 userAnswer.answer=encodeToBase64(answerPicture, Bitmap.CompressFormat.JPEG, 30);
                                 userAnswer.points=currentTask.taskCost;
                                 userAnswer.questKey=currentQuestkey;
                                 userAnswer.userKey=userCurrentKey;
                                 userAnswer.userName=userCurrent.username;
                                 userAnswer.taskText=currentTask.taskText;

                                 sendtoAdmins();

                             }

                            });
                            builder.setNegativeButton("Назад", (a, b) -> {

                            });
                            builder.setView(view);
                            dialog = builder.create();

                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(DialogInterface dialog) {
                                    if(sendEnable)
                                    {((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);}
                                    else{
                                        ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);}

                                }
                            });
                            dialog.show();
                        }else{Toast.makeText(TaskActivity.this,"Интернет-соединение отсутствует", Toast.LENGTH_SHORT).show();}
                        }
                    });

                    break;
                case "gps":
                    attempts.setVisibility(View.GONE);
                    answer.setVisibility(View.GONE);
                    sendAnswer.setText("Я на месте");
                    sendAnswer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MyLocationListener.SetUpLocationListener(context, activity);
                            double lat =MyLocationListener.getLat();
                            double lon = MyLocationListener.getLong();
                            if(lat!=92 && lon!=182){
                                int radius;
                                if(currentTask.geoRadius==0){radius=5;}else{radius=currentTask.geoRadius;}

                                if(isNear(lat, lon, radius)){
                                    Toast.makeText(context, "Правильно", Toast.LENGTH_SHORT).show();
                                    currentRating.progress.get(i).state=3;
                                    isitEnd();
                                }else{Toast.makeText(context, "Неправильно", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                    break;

            }
        }


    }
    protected void hideSoftKeyboard(EditText input) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public static boolean isNear(double lat, double lon, int radius){
        Log.d("MyTag", "LatSTD: "+mToDegreesLat(radius));
        Log.d("MyTag", "LongSTD: "+mToDegreesLong(radius, currentTask.longitude));
        if((Math.abs(lat-currentTask.latitude)<mToDegreesLat(radius))&&
                (Math.abs(lon-currentTask.longitude))<mToDegreesLong(radius, currentTask.longitude)){
            return true;
        }
        return false;
    }

    static double mToDegreesLat(int m){
        final double md=111.134861111*1000;
        return (1/md)*m;
    }

    static double mToDegreesLong(int m, double lat){
        final double eq=111321.377778;
        double ans=eq*Math.cos(lat);
        return (1/ans)*m;
    }
    public void showPopupWindow(final View view, String hint_text, boolean author_txt) {


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.pop_up_layout,null);

        PopupWindow mPopupWindow = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        // Get a reference for the custom view close button
        ImageView closeButton = customView.findViewById(R.id.ib_close);

         //Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                mPopupWindow.dismiss();
            }
        });

        TextView txt = customView.findViewById(R.id.pop_up_text);
        if(author_txt){txt.setText(Html.fromHtml(hint_text));}else{txt.setText(hint_text);}

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mPopupWindow.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    drawerLayout.setForeground(new ColorDrawable(getResources().getColor(R.color.transparent)));
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerLayout.setForeground(new ColorDrawable(getResources().getColor(R.color.gray_inactive)));
        }
        // Finally, show the popup window at the center location of root relative layout
        mPopupWindow.showAtLocation(parent, Gravity.CENTER,0,-100);
    }


    void isitEnd(){
        if(answer!=null){
        answer.setText("");}
        if(tasks.size()>i+1){
            currentRating.progress.get(i).mapstate=2;
            markerCoordinatesDone.add(Feature.fromJson(currentRating.progress.get(i).coordinates));
            updateRating();
            i++;
            if(currentTask.taskComment!=null&&!currentTask.taskComment.equals("")){
            showPopupWindow(parent, "<b>Примечание автора:</b> "+currentTask.taskComment, true);}
            hint_taken=false;
            gpsAnswer=false;
            setTask(tasks.get(i));

        }else{

            updateRating();
            endedQuest=true;
            hint_taken=false;
            gpsAnswer=false;
            if(!userCurrent.endedQuests.contains(currentQuestkey)){
                userCurrent.endedQuests.add(currentQuestkey);
                databaseFD.getReference("User").child(userCurrentKey)
                        .child("endedQuests").setValue(userCurrent.endedQuests); }
            userCurrent.activeQuests.remove(currentQuestkey);
            databaseFD.getReference("User").child(userCurrentKey)
                    .child("activeQuests").setValue(userCurrent.activeQuests);

            Toast.makeText(TaskActivity.this, "Поздравляем! Квест завершен", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(TaskActivity.this, MainActivity.class);
            startActivity(intent); this.finish();

        }
    }

    void sendtoAdmins(){
        updateRating();
        databaseReference=databaseFD.getReference("Answer");
        userAnswer.questName=currentQuest.qname;
        userAnswer.taskName=currentTask.taskHeading;
        databaseReference.push().setValue(userAnswer, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference dbReference) {
                databaseFD.getReference("User").child(currentQuest.admins.get(0))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User admin = dataSnapshot.getValue(User.class);
                                if(admin!=null) {
                                    ArrayList<String> forCheking = admin.forCheking;
                                    forCheking.add(dbReference.getKey());
                                    databaseFD.getReference("User").child(currentQuest.admins.get(0))
                                            .child("forCheking").setValue(forCheking);
                                    Toast.makeText(getBaseContext(), "Ваш ответ отправлен администратору", Toast.LENGTH_SHORT);
                                    currentRating.progress.get(i).state=4;
                                    isitEnd();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("MyTag", "DB Error"+databaseError);
                                Toast.makeText(TaskActivity.this, "Попробуйте еще раз", Toast.LENGTH_SHORT);

                            }
                        });
            }

        });


    }

    void setTextifnotnull(String s, TextView t){
        if(!(s.equals(null)||s.equals(""))){
            t.setText(s);
        }else{t.setText("");}
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MyTag", "Destroyed");
        updateRating();
        markerCoordinates.clear();
        markerCoordinatesDone.clear();
        finish();
        i=0;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d("MyTag", "Stopped");
        updateRating();
    }

    void updateRating(){
        Rating rating = new Rating();
        rating.keyUser=userCurrentKey;
        rating.keyQuest=currentQuestkey;
        rating.attemptsOnLastTask=currentAttempts;
        if(tasks.size()!=0){
        rating.lastTaskKey=tasks.get(i);}
        rating.points=currentPoints;
        rating.dateStart=dateStart;
        rating.hintTaken=hint_taken;
        if(tasks.size()==i+1){
        Calendar cal = Calendar.getInstance();
        rating.dateEnd=fullQuestActivity.calendar_to_String(cal);}

        //TODO изменить
        rating.progress= currentRating.progress;


        databaseReference=databaseFD.getReference("Rating");
        databaseReference.child(ratingKey).setValue(rating);
        if(endedQuest){
            databaseFD.getReference("Rating").child(ratingKey).child("ended").setValue(true);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();

                    try {

                        photopicker.setVisibility(View.VISIBLE);
                        gallery.setVisibility(View.GONE);
                        takephoto.setVisibility(View.GONE);
                        String path =getRealPathFromURI(TaskActivity.this, selectedImage);
                        //Bitmap imageBitmap =  BitmapFactory.decodeFile(getRightAngleImage(path));
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                        answerPicture=bitmap;
                        photopicker.setImageBitmap(answerPicture);
                        sendEnable=true;

                        dialog.cancel();
                        dialog.show();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            case CAMERA_REQUEST:
                if(resultCode == RESULT_OK){

                    Bitmap imageBitmap =  BitmapFactory.decodeFile(getRightAngleImage(currentPhotoPath));
                    gallery.setVisibility(View.GONE);
                    takephoto.setVisibility(View.GONE);
                    photopicker.setVisibility(View.VISIBLE);
                    answerPicture=imageBitmap;
                    photopicker.setImageBitmap(imageBitmap);
                    File f =new File(currentPhotoPath);
                    boolean deleted = f.delete();
                    Log.d("MyTag", ""+deleted);
                    sendEnable=true;
                    dialog.cancel();
                    dialog.show();
                    break;
                }
        }

    }
    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            //Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }
    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagePath;
    }



    private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.slastanna.android.fileprovider",
                    photoFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }
}

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "questory" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
