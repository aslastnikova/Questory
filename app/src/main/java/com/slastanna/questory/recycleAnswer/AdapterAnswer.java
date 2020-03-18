package com.slastanna.questory.recycleAnswer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.slastanna.questory.R;
import com.slastanna.questory.recycleQuest.Content;
import com.slastanna.questory.tables.MyMessage;
import com.slastanna.questory.tables.Rating;
import com.slastanna.questory.ui.forcheck.ForCheckFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.slastanna.questory.EmailPasswordActivity.databaseFD;
import static com.slastanna.questory.EmailPasswordActivity.databaseReference;
import static com.slastanna.questory.EmailPasswordActivity.userCurrent;
import static com.slastanna.questory.EmailPasswordActivity.userCurrentKey;
import static com.slastanna.questory.MainActivity.decodeBase64;


public class AdapterAnswer extends RecyclerView.Adapter<AdapterAnswer.Holder> {
    //private static MyClickListener clickListener;
    private ArrayList<ContentAnswer> contents;
     private Context context;
     //public boolean deleted, send;


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

        return new Holder(layout);
    }

    @Override
    public void onViewRecycled(@NonNull Holder holder) {
        super.onViewRecycled(holder);
        Log.d("MyTag", "recycled: "+holder.name.getText());
        if(contents.size()>(int)holder.getItemId()){
        if(contents.get((int)holder.getItemId()).ispicture){
        contents.get((int)holder.getItemId()).isshown=false;}}
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        if(contents.get(i)!=null) {
            holder.name.setText(contents.get(i).getName());
            holder.task.setText(contents.get(i).getTask());
            holder.img.setVisibility(View.GONE);
            if (contents.get(i).getAnswer() != null) {
                if (!contents.get(i).ispicture) {
                    //holder.img.setVisibility(View.GONE);
                    holder.ans.setVisibility(View.VISIBLE);
                    holder.showImg.setVisibility(View.GONE);
                    holder.ans.setText(contents.get(i).getAnswer());
                } else {
                    contents.get(i).ispicture=true;
                    holder.ans.setVisibility(View.GONE);
                    if(!contents.get(i).isshown){
                        holder.img.setVisibility(View.GONE);
                        holder.showImg.setVisibility(View.VISIBLE);}else{
                        holder.showImg.setVisibility(View.GONE);
                        holder.img.setVisibility(View.VISIBLE);
                    }
                    //Drawable image = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray (contents.get(i).getAnswer(), 0, contents.get(i).getAnswer().length()));
                    holder.showImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bitmap img = decodeBase64(contents.get(i).getAnswer());
                            contents.get(i).isshown=true;
                            holder.img.setVisibility(View.VISIBLE);
                            holder.img.setImageBitmap(img);
                            holder.showImg.setVisibility(View.GONE);
                        }
                    });


                }
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
                    message.requestedKey = contents.get(i).keyUser;
                    message.requestedName = contents.get(i).name;
                    message.isChecked = true;
                    message.text = message.makeCheckedMessage(contents.get(i).nameTask, contents.get(i).nameQuest, false, 0);
                    databaseReference = databaseFD.getReference("Message");
                    if (!contents.get(i).send) {
                        databaseReference.push().setValue(message, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                //Toast.makeText(context, "Ваше приглашение отправлено", Toast.LENGTH_SHORT).show();
                                contents.get(i).send = true;
                                userCurrent.forCheking.remove(i);
                                databaseFD.getReference("User").child(userCurrentKey).child("forCheking").setValue(userCurrent.forCheking);
                                databaseReference.child(contents.get(i).key).removeValue();

                                contents.remove(i);
                                notifyItemRemoved(i);
                                notifyDataSetChanged();
                                if (contents.size() == 0) {
                                    ForCheckFragment.previewText.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }

                }
            });

            holder.rightans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    contents.get(i).deleted = false;
                    databaseReference = databaseFD.getReference("Rating");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                    Rating currRating = issue.getValue(Rating.class);
                                    if (currRating != null) {
                                        if (contents.size() > i) {
                                            if (!contents.get(i).deleted && currRating.keyQuest.equals(contents.get(i).keyQuest)
                                                    && currRating.keyUser.equals(contents.get(i).keyUser)&& !contents.get(i).pointssend) {
                                                contents.get(i).pointssend=true;
                                                databaseReference = databaseFD.getReference("Rating");
                                                databaseReference.child(issue.getKey()).child("points").setValue(currRating.points + contents.get(i).points).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            MyMessage message = new MyMessage();
                                                            //message.requesterKey=userCurrentKey;
                                                            message.requestedKey = contents.get(i).keyUser;
                                                            message.requestedName = contents.get(i).name;
                                                            message.isChecked = true;
                                                            message.text = message.makeCheckedMessage(contents.get(i).nameTask, contents.get(i).nameQuest, true, contents.get(i).points);
                                                            databaseReference = databaseFD.getReference("Message");
                                                            if (!contents.get(i).send&&contents.get(i).pointssend) {
                                                                databaseReference.push().setValue(message, new DatabaseReference.CompletionListener() {
                                                                    @Override
                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                        //Toast.makeText(context, "Ваше приглашение отправлено", Toast.LENGTH_SHORT).show();
                                                                        if (contents.size() != 0) {
                                                                            contents.get(i).send = true;
                                                                            contents.get(i).deleted = true;
                                                                            userCurrent.forCheking.remove(i);
                                                                            databaseFD.getReference("User").child(userCurrentKey).child("forCheking").setValue(userCurrent.forCheking);
                                                                            databaseReference = databaseFD.getReference("Answer");
                                                                            databaseReference.child(contents.get(i).key).removeValue();
                                                                            contents.remove(i);
                                                                            notifyItemRemoved(i);
                                                                            notifyDataSetChanged();
                                                                            if (contents.size() == 0) {
                                                                                ForCheckFragment.previewText.setVisibility(View.VISIBLE);
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });

                                                break;
                                            }
                                        }
                                    }

                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


//                    MyMessage message = new MyMessage();
//                    //message.requesterKey=userCurrentKey;
//                    message.requestedKey = contents.get(i).keyUser;
//                    message.requestedName = contents.get(i).name;
//                    message.isChecked = true;
//                    message.text = message.makeCheckedMessage(contents.get(i).nameTask, contents.get(i).nameQuest, true, contents.get(i).points);
//                    databaseReference = databaseFD.getReference("Message");
//                    if (!contents.get(i).send&&contents.get(i).pointssend) {
//                        databaseReference.push().setValue(message, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                //Toast.makeText(context, "Ваше приглашение отправлено", Toast.LENGTH_SHORT).show();
//                                if (contents.size() != 0) {
//                                    contents.get(i).send = true;
//                                    contents.get(i).deleted = true;
//                                    userCurrent.forCheking.remove(i);
//                                    databaseFD.getReference("User").child(userCurrentKey).child("forCheking").setValue(userCurrent.forCheking);
//                                    databaseReference = databaseFD.getReference("Answer");
//                                    databaseReference.child(contents.get(i).key).removeValue();
//                                    contents.remove(i);
//                                    notifyItemRemoved(i);
//                                    notifyDataSetChanged();
//                                    if (contents.size() == 0) {
//                                        ForCheckFragment.previewText.setVisibility(View.VISIBLE);
//                                    }
//                                }
//                            }
//                        });
//                    }


                }
            });

        }
    }

    @Override
    public long getItemId(int position) {
        ContentAnswer content = contents.get(position);
        return content.id;
    }


    @Override
    public int getItemCount() {
        return contents.size();
    }


static class Holder extends RecyclerView.ViewHolder implements  View.OnClickListener {

    ImageButton rightans, falseans;
    TextView name, task, ans;
    ImageView img;
    Button showImg;



    public Holder(@NonNull View itemView) {
        super(itemView);
        Log.d("MyTag", "9");
        itemView.setOnClickListener(this);
        name=itemView.findViewById(R.id.name);
        task=itemView.findViewById(R.id.task);
        ans=itemView.findViewById(R.id.answer);
        rightans=itemView.findViewById(R.id.right);
        falseans=itemView.findViewById(R.id.falseans);
        showImg=itemView.findViewById(R.id.buttonPhoto);
        img=itemView.findViewById(R.id.img);

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        // кнопки объявлять здесь

    }
    @Override
    public void onClick(View v) {
        //clickListener.onItemClick(getAdapterPosition(), v);
    }


}




}