package com.slastanna.questory.tables;

import java.util.ArrayList;

public class User {
    public String username="";
    public String firstname="";
    public String surname="";
    public String email="";
    public String password="";
    public String avatar="";
    public String userKey;
    //public boolean verified_email;
    public ArrayList<String> activeQuests=new ArrayList<>();
    public ArrayList<String> endedQuests=new ArrayList<>();
    public ArrayList<String> friends=new ArrayList<>();
    public ArrayList<String> wait_friends_list=new ArrayList<>();
    public ArrayList<String> forCheking=new ArrayList<>();
    public String birth_date;
    public User(){}
    public User(String username, String email, String password)
    {this.username=username; this.email=email; this.password=password;}
}
