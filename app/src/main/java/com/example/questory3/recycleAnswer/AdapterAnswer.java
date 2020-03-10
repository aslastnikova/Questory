package com.example.questory3.recycleAnswer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.questory3.R;
import com.example.questory3.tables.MyMessage;
import com.example.questory3.tables.Rating;
import com.example.questory3.ui.findQuests.FindQuestsFragment;
import com.example.questory3.ui.forcheck.ForCheckFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.questory3.EmailPasswordActivity.databaseFD;
import static com.example.questory3.EmailPasswordActivity.databaseReference;
import static com.example.questory3.EmailPasswordActivity.userCurrent;
import static com.example.questory3.EmailPasswordActivity.userCurrentKey;
import static com.example.questory3.MainActivity.decodeBase64;
import static com.example.questory3.MainActivity.encodeToBase64;


public class AdapterAnswer extends RecyclerView.Adapter<AdapterAnswer.Holder> {
    //private static MyClickListener clickListener;
    private ArrayList<ContentAnswer> contents;
     private Context context;
     public boolean deleted, send;


    public AdapterAnswer(ArrayList<ContentAnswer> contents, Context context) {
        this.contents = contents; this.context=context;
    }
    //установить набор contents
    public void setContents(ArrayList<ContentAnswer> contents) {
        this.contents = contents;
    }

    //обновить содержимое набора contents
    public void updateContents(ContentAnswer con, int position){
        contents.get(position).name=con.name;
        contents.get(position).task=con.task;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context= viewGroup.getContext();
        View layout=null;
        //выбор макета для одного content
        layout = LayoutInflater.from(viewGroup.getContext()).inflate(ForCheckFragment.layout, viewGroup, false);
        Log.d("MyTag", "8");
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Log.d("MyTag", "3");

        holder.name.setText(contents.get(i).getName());
        holder.task.setText(contents.get(i).getTask());
        if(contents.get(i).getAnswer().length()<=300){
            holder.img.setVisibility(View.GONE);
        holder.ans.setText(contents.get(i).getAnswer());}else{
            holder.ans.setVisibility(View.GONE);
            Bitmap img = decodeBase64(contents.get(i).getAnswer());
            holder.img.setImageBitmap(img);
        }
        databaseReference = databaseFD.getReference("Answer");

        holder.falseans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO working here
                databaseReference = databaseFD.getReference("Answer");
                //databaseReference.child(contents.get(i).key).child("ischeked").setValue(true);


                MyMessage message = new MyMessage();
                //message.requesterKey=userCurrentKey;
                message.requestedKey=contents.get(i).keyUser;
                message.requestedName=contents.get(i).name;
                message.isChecked=true;
                message.text=message.makeCheckedMessage(contents.get(i).nameTask, contents.get(i).nameQuest, false, 0);
                databaseReference = databaseFD.getReference("Message");
                if(!send){
                databaseReference.push().setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        //Toast.makeText(context, "Ваше приглашение отправлено", Toast.LENGTH_SHORT).show();
                        send=true;
                        userCurrent.forCheking.remove(i);
                        databaseFD.getReference("User").child(userCurrentKey).child("forCheking").setValue(userCurrent.forCheking);
                        databaseReference.child(contents.get(i).key).removeValue();

                        contents.remove(i);
                        notifyItemRemoved(i);
                        notifyDataSetChanged();
                        if(contents.size()==0){
                            ForCheckFragment.previewText.setVisibility(View.VISIBLE);
                        }
                    }
                });}

            }
        });
        holder.rightans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleted = false;
                databaseReference = databaseFD.getReference("Rating");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                Rating currRating = issue.getValue(Rating.class);
                                if(currRating!=null&&!deleted){
                                if(currRating.keyQuest.equals(contents.get(i).keyQuest)&&currRating.keyUser.equals(contents.get(i).keyUser)){
                                    databaseReference = databaseFD.getReference("Rating");
                                     databaseReference.child(issue.getKey()).child("points").setValue(currRating.points+contents.get(i).points);




                                    MyMessage message = new MyMessage();
                                    //message.requesterKey=userCurrentKey;
                                    message.requestedKey=contents.get(i).keyUser;
                                    message.requestedName=contents.get(i).name;
                                    message.isChecked=true;
                                    message.text=message.makeCheckedMessage(contents.get(i).nameTask, contents.get(i).nameQuest, true, contents.get(i).points);
                                    databaseReference = databaseFD.getReference("Message");
                                    if(!send){
                                    databaseReference.push().setValue(message, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            //Toast.makeText(context, "Ваше приглашение отправлено", Toast.LENGTH_SHORT).show();
                                            send=true;
                                            if(!deleted){

                                            deleted =true;
                                            userCurrent.forCheking.remove(i);
                                            databaseFD.getReference("User").child(userCurrentKey).child("forCheking").setValue(userCurrent.forCheking);
                                            databaseReference = databaseFD.getReference("Answer");
                                            databaseReference.child(contents.get(i).key).removeValue();
                                            contents.remove(i);
                                            notifyItemRemoved(i);
                                            notifyDataSetChanged();
                                            if(contents.size()==0){
                                                ForCheckFragment.previewText.setVisibility(View.VISIBLE);
                                            }}
                                        }
                                    });}

                                    //notifyItemRemoved(i);
                                    //notifyDataSetChanged();


                                 }
                            }

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


//        holder.description.setText("История этого гастронома началась очень оригинально – с блюдечка земляники, " +
//                        "поданного зимой графу Шереметеву. Его садовник совершил чудо – вырастил сладкую ягоду в " +
//                        "оранжерее. Причем он не только сохранил кустик, но и заставил его плодоносить. " +
//                        "За что и получил вольную от графа и 100 рублей подъемных, которые вложил в свое дело. " +
//                "Загаданное здание, окно-витрина со сладким. Надписи в оформлении.  " );
        //обработка нажатий кнопок на layout здесь

    }




    @Override
    public int getItemCount() {
        return contents.size();
    }


class Holder extends RecyclerView.ViewHolder implements  View.OnClickListener {

    ImageButton rightans, falseans;
    TextView name, task, ans;
    ImageView img;




    public Holder(@NonNull View itemView) {
        super(itemView);
        Log.d("MyTag", "9");
        itemView.setOnClickListener(this);
        name=itemView.findViewById(R.id.name);
        task=itemView.findViewById(R.id.task);
        ans=itemView.findViewById(R.id.answer);
        rightans=itemView.findViewById(R.id.right);
        falseans=itemView.findViewById(R.id.falseans);
        img=itemView.findViewById(R.id.img);

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        // кнопки объявлять здесь

    }
    @Override
    public void onClick(View v) {
        //clickListener.onItemClick(getAdapterPosition(), v);
    }


}

//    public void setOnItemClickListener(MyClickListener clickListener) {
//        AdapterFriends.clickListener = clickListener;
//    }

//public interface MyClickListener {
//    void onItemClick(int position, View v);
//
//}


}