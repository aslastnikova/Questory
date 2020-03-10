package com.example.questory3.recyclerFriends;

import android.graphics.Bitmap;

public class ContentFriends {

        public  String name;
        public Bitmap avatar;
        public  String keyUser;
        public boolean friend;

        public ContentFriends(){}

        public ContentFriends(String name, String key) {
            this.name = name;
            this.keyUser = key;

        }

        public String getName() { return name; }
         public void setName(String name) { this.name = name; }





}
