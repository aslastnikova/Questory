package com.example.questory3.recycleAnswer;

public class ContentAnswer {

        public  String name, task;
        public  String answer;
        public  String key;
        public String keyQuest, keyUser, nameQuest, nameTask;
        public int points;

        public ContentAnswer(){}

        public ContentAnswer(String name, String task, String answer, String key) {
            this.name = name;
            this.task = task;
            this.answer = answer;
            this.key = key;

        }

        public String getName() { return name; }
        public String getTask() { return task; }

        public String getAnswer() {
             return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

    public void setName(String name) { this.name = name; }



        public void setTask(String surname) {
            this.task = surname;
        }

}