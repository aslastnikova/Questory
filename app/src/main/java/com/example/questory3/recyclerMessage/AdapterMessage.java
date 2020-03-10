package com.example.questory3.recyclerMessage;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.questory3.R;
import com.example.questory3.tables.MyMessage;
import com.example.questory3.tables.Quest;
import com.example.questory3.tables.User;
import com.example.questory3.ui.findQuests.FindQuestsFragment;
import com.example.questory3.ui.message.MessageFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.questory3.EmailPasswordActivity.databaseFD;
import static com.example.questory3.EmailPasswordActivity.databaseReference;
import static com.example.questory3.EmailPasswordActivity.userCurrent;
import static com.example.questory3.EmailPasswordActivity.userCurrentKey;
import static com.example.questory3.MainActivity.decodeBase64;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.Holder> {
    private static AdapterMessage.MyClickListener clickListener;
    public long itemid;
    public static boolean isWeapon;
    private ArrayList<MyMessage> contents;
    private Context context;


    public AdapterMessage(ArrayList<MyMessage> contents, Context context) {
        this.contents = contents; this.context=context;
    }
    //установить набор contents
    public void setContents(ArrayList<MyMessage> contents) {
        this.contents = contents;
    }


    @NonNull
    @Override
    public AdapterMessage.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context= viewGroup.getContext();
        View layout=null;
        
        layout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_content, viewGroup, false);

        return new AdapterMessage.Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMessage.Holder holder, int i) {


        holder.text.setText(contents.get(i).text);
        if(contents.get(i).isFriend){
            holder.read.setVisibility(View.GONE);
            holder.friend_layout.setVisibility(View.VISIBLE);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.accept.setEnabled(false);
                    holder.decline.setEnabled(false);
                    userCurrent.friends.add(contents.get(i).requesterKey);
                    databaseReference=databaseFD.getReference("User");
                    databaseReference.child(userCurrentKey).child("friends").setValue(userCurrent.friends);
                    databaseReference.child(contents.get(i).requesterKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.wait_friends_list.remove(userCurrentKey);
                            databaseReference.child(contents.get(i).requesterKey).child("wait_friends_list").setValue(user.wait_friends_list);
                            if(!user.friends.contains(contents.get(i).requestedKey)){
                            user.friends.add(contents.get(i).requestedKey);}
                            databaseReference=databaseFD.getReference("Message");
                            databaseReference.child(contents.get(i).messageKey).removeValue();
                            contents.remove(i);
                            notifyItemRemoved(i);
                            notifyDataSetChanged();
                            MyMessage message = new MyMessage();
                            if(user.username!=null){
                            message.text=message.makeFriendRequestAcceptedText(user.username);
                            databaseFD.getReference("Message").push().
                                    setValue(message, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    holder.accept.setEnabled(true);
                                    holder.decline.setEnabled(true);
                                }
                            });}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.accept.setEnabled(false);
                    holder.decline.setEnabled(false);
                    databaseReference=databaseFD.getReference("Message");
                    databaseReference.child(contents.get(i).messageKey).removeValue();
                    databaseReference=databaseFD.getReference("User");
                    databaseReference.child(contents.get(i).requesterKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.wait_friends_list.remove(userCurrentKey);
                            try{
                            databaseReference.child(contents.get(i).requesterKey).child("wait_friends_list").setValue(user.wait_friends_list);}
                            catch (Exception e){}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    contents.remove(i);
                    notifyItemRemoved(i);
                    notifyDataSetChanged();
                    if(contents.size()==0){
                        MessageFragment.previewText.setVisibility(View.VISIBLE);
                    }
                    MyMessage message = new MyMessage();
                    //TODO проверь этот кусок
                    try{
                    if(contents.get(i).requestedName!=null){
                    message.text=message.makeFriendRequestDeclineText(contents.get(i).requestedName);
                    databaseFD.getReference("Message").push().
                            setValue(message, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                }
                            });}}catch (Exception e){}
                }
            });


        }
        else if(contents.get(i).isAdminRequest){
            //TODO проверь этот кусок кода
            holder.read.setVisibility(View.GONE);
            holder.friend_layout.setVisibility(View.VISIBLE);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.accept.setEnabled(false);
                    holder.decline.setEnabled(false);
                    databaseReference=databaseFD.getReference("Quest");
                    //databaseReference.child(userCurrentKey).child("friends").setValue(userCurrent.friends);
                    databaseReference.child(contents.get(i).questKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Quest quest = dataSnapshot.getValue(Quest.class);
                            quest.admins.set(0, contents.get(i).requestedKey);
                            databaseReference.child(contents.get(i).questKey).child("admins").setValue(quest.admins);
                            databaseReference=databaseFD.getReference("Message");
                            databaseReference.child(contents.get(i).messageKey).removeValue();
                            contents.remove(i);
                            notifyItemRemoved(i);
                            notifyDataSetChanged();
                            MyMessage message = new MyMessage();
                            if(contents.get(i).requestedName!=null&&contents.get(i).questName!=null){
                            message.text=message.makeAdminRequestAcceptedText(contents.get(i).requestedName, contents.get(i).questName);
                            databaseFD.getReference("Message").push().
                                    setValue(message, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError,
                                                               DatabaseReference databaseReference) {
                                            holder.accept.setEnabled(true);
                                            holder.decline.setEnabled(true);
                                        }
                                    });}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.accept.setEnabled(false);
                    holder.decline.setEnabled(false);
                    databaseReference=databaseFD.getReference("Message");
                    databaseReference.child(contents.get(i).messageKey).removeValue();
                    contents.remove(i);
                    notifyItemRemoved(i);
                    notifyDataSetChanged();
                    MyMessage message = new MyMessage();
                    if(contents.get(i).requestedName!=null&&contents.get(i).questName!=null){
                    message.text=message.makeAdminRequestDeclineText(contents.get(i).requestedName, contents.get(i).questName);
                    databaseFD.getReference("Message").push().
                            setValue(message, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    holder.accept.setEnabled(true);
                                    holder.decline.setEnabled(true);
                                }
                            });}
                }
            });
        }
        else {
            holder.friend_layout.setVisibility(View.GONE);
            holder.read.setVisibility(View.VISIBLE);
            holder.read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference=databaseFD.getReference("Message");
                    databaseReference.child(contents.get(i).messageKey).removeValue();
                    contents.remove(i);
                    notifyItemRemoved(i);
                    notifyDataSetChanged();
                    if(contents.size()==0){
                        MessageFragment.previewText.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


    }




    @Override
    public int getItemCount() {
        return contents.size();
    }


    class Holder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        Button accept, decline, read;
        TextView text;
        LinearLayout friend_layout;




        public Holder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            accept=itemView.findViewById(R.id.accept);
            decline=itemView.findViewById(R.id.decline);
            read=itemView.findViewById(R.id.read);
            text=itemView.findViewById(R.id.message_text);
            friend_layout=itemView.findViewById(R.id.friend_layout);
        }
        @Override
        public void onClick(View v) {
            //clickListener.onItemClick(getAdapterPosition(), v);
        }


    }

    public void setOnItemClickListener(AdapterMessage.MyClickListener clickListener) {
        AdapterMessage.clickListener = clickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);

    }


}
