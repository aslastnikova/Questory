package com.example.questory3.recycleQuest;

import android.graphics.drawable.Drawable;

public class Content {

        public  String name, description;
        public  String photo;
        public  String key;

        public  Content(){}

        public Content(String name, String description, String photo, String key) {
            this.name = name;
            this.description = description;
            this.photo = photo;
            this.key = key;

        }

        public String getName() { return name; }
        public String getPhoto() { return photo; }

        public void setName(String name) { this.name = name; }

        public String getDescription() {
            return description;
        }

        public void setDescription(String surname) {
            this.description = surname;
        }

}
