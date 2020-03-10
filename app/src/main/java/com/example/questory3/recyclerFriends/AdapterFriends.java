package com.example.questory3.recyclerFriends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.questory3.EmailPasswordActivity;
import com.example.questory3.MainActivity;
import com.example.questory3.R;
import com.example.questory3.recycleQuest.MyAdapter;
import com.example.questory3.tables.MyMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.questory3.EmailPasswordActivity.databaseFD;
import static com.example.questory3.EmailPasswordActivity.databaseReference;
import static com.example.questory3.EmailPasswordActivity.userCurrent;
import static com.example.questory3.EmailPasswordActivity.userCurrentKey;
import static com.example.questory3.MainActivity.decodeBase64;


public class AdapterFriends extends RecyclerView.Adapter<AdapterFriends.Holder> {
    //private static MyClickListener clickListener;
     private HashSet<ContentFriends> contents;
     private Context context;
     public boolean friends;
     public static Color color;


    public AdapterFriends(HashSet<ContentFriends> contents, Context context) {
        this.contents = contents; this.context=context;
    }
    //установить набор contents
    public void setContents(HashSet<ContentFriends> contents) {
        this.contents = contents;

    }

    //обновить содержимое набора contents
    public void updateContents(ContentFriends con, int position){

        //contents.get(position).name=con.name;
        //contents.get(position).keyUser=con.keyUser;
        //contents.get(position).avatar =con.avatar;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context= viewGroup.getContext();
        View layout=null;
        layout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_content, viewGroup, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        ArrayList<ContentFriends> curcontents = new ArrayList(contents);

        holder.name.setText(curcontents.get(i).getName());
        if(curcontents.get(i).avatar==null){
            holder.ava.setVisibility(View.INVISIBLE); }else{
            holder.ava.setImageBitmap(curcontents.get(i).avatar);
        }
        if(curcontents.get(i).friend){
            holder.add.setVisibility(View.INVISIBLE);
        }else if(userCurrent.wait_friends_list.contains(curcontents.get(i).keyUser)){
            holder.add.setEnabled(false);
        }
        else{
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.add.setEnabled(false);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        holder.add.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorAccentPushed));
//                    }else{holder.add.setBackgroundResource(R.color.colorAccentPushed);}
                    MyMessage message = new MyMessage();
                    message.requesterKey=userCurrentKey;
                    message.requestedKey=curcontents.get(i).keyUser;
                    message.requestedName=curcontents.get(i).name;
                    message.isFriend=true;
                    if(!userCurrent.wait_friends_list.contains(curcontents.get(i).keyUser)){
                    userCurrent.wait_friends_list.add(curcontents.get(i).keyUser);
                    databaseFD.getReference("User").child(userCurrentKey).child("wait_friends_list").setValue(userCurrent.wait_friends_list);}
                    if(userCurrent.username!=null){
                    message.text=message.makeFriendRequestText(userCurrent.username);
                    databaseReference = databaseFD.getReference("Message");
                    databaseReference.push().setValue(message, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(context, "Ваше приглашение отправлено", Toast.LENGTH_SHORT).show();
                        }
                    });}

                }
            });
        }
    }




    @Override
    public int getItemCount() {
        return contents.size();
    }


class Holder extends RecyclerView.ViewHolder implements  View.OnClickListener {

    TextView name;
    ImageView ava;
    Button add;



    public Holder(@NonNull View itemView) {
        super(itemView);
        Log.d("MyTag", "9");
        itemView.setOnClickListener(this);
        name=itemView.findViewById(R.id.friendname);
        ava=itemView.findViewById(R.id.friendava);
        add=itemView.findViewById(R.id.buttonAdd);
        if(friends){
            add.setVisibility(View.GONE);
        }else{
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //holder.add.setBackgroundColor(R.color.colorAccentPushed);
                }
            });
        }

    }
    @Override
    public void onClick(View v) {
    }


}

}