package com.slastanna.questory.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.slastanna.questory.MainActivity;
import com.slastanna.questory.R;
import com.slastanna.questory.tables.MyMessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;

public class NotificationService extends Service {
    DatabaseReference databaseReference;
    FirebaseDatabase databaseFD;
    SharedPreferences myPref;
    public static final String CHANNEL_ID = "QuestoryChannel";
    public NotificationService() {
        databaseFD = FirebaseDatabase.getInstance();
        databaseReference = databaseFD.getReference("Message");
        Log.d("MyTag", "Service is working1");
    }

    public void onCreate() {
        super.onCreate();

        Log.d("MyTag", "Service is working2");
        getInfo();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Questory")
//                .setContentText(input)
                .setSmallIcon(R.drawable.icon_main)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return Service.START_STICKY;
    }

    void getInfo(){



        myPref =  getSharedPreferences("Questory_user_base_1542", Context.MODE_PRIVATE);
        String userkey= myPref.getString("userKey", "");
        Log.d("MyTag", "userKey"+userkey);
        if(!userkey.equals("")) {
            Query query = databaseReference.orderByChild("requestedKey").equalTo(userkey);

            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("MyTag", "ServiceGot"+dataSnapshot.getValue());
                    MyMessage curmessage=null;
                    try {
                    curmessage=dataSnapshot.getValue(MyMessage.class);
                    }catch(Exception e){}
                    if(curmessage!=null&&curmessage.text!=null&&!curmessage.text.equals("")){
                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(NotificationService.this, CHANNEL_ID)
                                        .setSmallIcon(R.drawable.icon_main)
                                        .setContentText(curmessage.text)
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManager =
                                NotificationManagerCompat.from(NotificationService.this);
                        notificationManager.notify(2, builder.build());
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
        }else{stopSelf();}
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("MyTag", "Service is 4");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


        public void onDestroy() {
        super.onDestroy();
        Log.d("MyTag", "Service is working5");
    }
}
