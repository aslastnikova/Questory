package com.example.questory3.tables;

import java.util.ArrayList;

public class Task {
    //public String questKey;
    public String rightAnswer;
    public boolean forAdmin;
    public String address;
    public String taskHeading;
   // public ArrayList<Answer> answers = new ArrayList<>();
    public int attempts;
    public boolean hiddenAddress;
    public String typeTask;
    public String taskText;
    public String tpicture;
    public String hint, taskComment;
    public int taskPoints;
    public float longitude, latitude;
    public Task(){}
    public Task(String questKey, boolean forAdmin, int num, String taskText)
    {
        //this.questKey = questKey;
        this.forAdmin = forAdmin;
        this.taskText=taskText;}
}
