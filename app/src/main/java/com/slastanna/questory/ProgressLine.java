package com.slastanna.questory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mapbox.mapboxsdk.storage.Resource;

import java.util.ArrayList;

import javax.crypto.Cipher;

import androidx.annotation.Nullable;

public class ProgressLine extends View {

    Paint p;
    Rect rect;
    int maxWidth, maxHeight;
    ArrayList<Integer> colors=new ArrayList<>();
    int red, green, orange;
    Canvas canvas;

    public ProgressLine(Context context, @Nullable AttributeSet attr) {
        super(context, attr);
        p = new Paint();
        rect = new Rect();
        red = this.getResources().getColor(R.color.design_default_color_error);
        green = this.getResources().getColor(R.color.colorPrimary);
        orange = this.getResources().getColor(R.color.holo_orange_light);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas=canvas;
        if (canvas!=null){
        maxWidth=canvas.getWidth();
        maxHeight=canvas.getHeight();
        Log.d("MyTag", "maxheight "+maxHeight);

        if(colors.size()!=0){
            int width=maxWidth/colors.size();
            int curx=0;
            for(int i=0; i<colors.size(); i++){
                drawLine(canvas, colors.get(i), curx, curx+width);
                curx+=width;
            }
            drawSeparator(canvas, width);
            p.setColor(Color.GRAY);
            p.setStrokeWidth(7);
            canvas.drawLine(0,0,maxWidth,0,p);
            canvas.drawLine(0,maxHeight,maxWidth,maxHeight,p);
        }}

    }

    void drawLine(Canvas canvas, int color, int start, int end){
        Paint p = new Paint();
        p.setColor(color);
        p.setStrokeWidth(maxHeight);
        canvas.drawLine(start,maxHeight/2,end,maxHeight/2,p);
    }
    void drawSeparator(Canvas canvas, int width){
        Paint p = new Paint();
        p.setColor(Color.GRAY);
        p.setStrokeWidth(maxHeight);
        int x = width;
        for(int i=0; i<colors.size()-1; i++){
            canvas.drawLine(x,maxHeight/2,x+3,maxHeight/2,p);
            x+=width;
        }
    }

    public void setLines(ArrayList<Integer> steps){
        for(int i=0; i<steps.size(); i++){
            switch (steps.get(i)){
                case 0: colors.add(red); break;
                case 1: colors.add(orange); break;
                case 2: colors.add(green); break;
            }
        }
        draw(canvas);
    }


}



