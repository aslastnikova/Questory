package com.example.questory3.tables;


import java.util.ArrayList;

public class Quest {
    public String qname="";
    public String ownerName="";
    public String ownerKey="";
    public String description="";
    public String qpicture="";
    public boolean isprivate;
    public boolean islinear;
    public boolean anonymousAnswers;
    public int duration=0; //in minutes
    public float kilomenters=0.f;
    public ArrayList<String> admins=new ArrayList<>();
    public ArrayList<String> players=new ArrayList<>();
    public ArrayList<String> tasks=new ArrayList<>();
    public Quest(){}
    public Quest(String name, String ownerName,String ownerKey, boolean isprivate, boolean islinear)
    {this.qname=name; this.ownerName=ownerName; this.ownerKey=ownerKey; this.islinear=islinear; this.isprivate=isprivate;}
}
