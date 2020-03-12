package com.slastanna.questory.recycleQuest;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.slastanna.questory.R;
import com.slastanna.questory.ui.findQuests.FindQuestsFragment;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.slastanna.questory.MainActivity.decodeBase64;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {
    private static MyClickListener clickListener;
    public long itemid;
    public static boolean isWeapon;
    private ArrayList<Content> contents;
     private Context context;


    public MyAdapter(ArrayList<Content> contents, Context context) {
        this.contents = contents; this.context=context;
    }
    //установить набор contents
    public void setContents(ArrayList<Content> contents) {
        this.contents = contents;
    }

    //обновить содержимое набора contents
    public void updateContents(Content con, int position){
        contents.get(position).name=con.name;
        contents.get(position).description=con.description;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context= viewGroup.getContext();
        View layout=null;
        //выбор макета для одного content
        layout = LayoutInflater.from(viewGroup.getContext()).inflate(FindQuestsFragment.layout, viewGroup, false);

        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {


        holder.name.setText(contents.get(i).getName());
        holder.description.setText(contents.get(i).getDescription());
        if(!(contents.get(i).photo.equals(null)||contents.get(i).photo.equals(""))){


            Bitmap btm=decodeBase64(contents.get(i).photo);
            holder.img.setImageBitmap(btm);
        int a =1;
//        holder.description.setText("История этого гастронома началась очень оригинально – с блюдечка земляники, " +
//                        "поданного зимой графу Шереметеву. Его садовник совершил чудо – вырастил сладкую ягоду в " +
//                        "оранжерее. Причем он не только сохранил кустик, но и заставил его плодоносить. " +
//                        "За что и получил вольную от графа и 100 рублей подъемных, которые вложил в свое дело. " +
//                "Загаданное здание, окно-витрина со сладким. Надписи в оформлении.  " );
        //обработка нажатий кнопок на layout здесь

    }
    }




    @Override
    public int getItemCount() {
        return contents.size();
    }


class Holder extends RecyclerView.ViewHolder implements  View.OnClickListener {

    //Button chbtn, delbtn;
    TextView name, description;
    public ImageView img;



    public Holder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        name=itemView.findViewById(R.id.QNameText);
        description=itemView.findViewById(R.id.QDescText);
        img=itemView.findViewById(R.id.QPic);
        //img.setImageResource(R.drawable.test);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        // кнопки объявлять здесь

    }
    @Override
    public void onClick(View v) {
        if(v!=null){
        clickListener.onItemClick(getAdapterPosition(), v);}
    }


}

    public void setOnItemClickListener(MyClickListener clickListener) {
        MyAdapter.clickListener = clickListener;
    }

public interface MyClickListener {
    void onItemClick(int position, View v);

}


}