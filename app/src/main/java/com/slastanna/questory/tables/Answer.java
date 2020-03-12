package com.slastanna.questory.tables;

public class Answer {
    public String userKey;
    public String questKey, questName;
    public String userName;
    public String taskText, taskName;
    public String answer;
    public int points;
    public boolean ischeked=false;
   // public int attempts;
    public Answer(){}
    public Answer(String taskText, String userKey,String username, String answer)
    {this.taskText=taskText;this.userKey=userKey; this.answer=answer; userName=username;}
}
