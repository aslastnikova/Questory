package com.slastanna.questory.tables;

public class MyMessage {
    public boolean isChecked, isFriend, isAdminRequest;
    public String text, requesterKey, requestedKey, messageKey, requestedName;
    public String questKey, questName;
    public String makeFriendRequestText(String username){
        String text;
        text="Пользователь "+username+" предлагает вам дружбу.";
        return text;
    }
    public String makeFriendRequestAcceptedText(String username){
        String text;
        text="Пользователь "+username+" принял ваше предложение дружбы.";
        return text;
    }
    public String makeFriendRequestDeclineText(String username){
        String text;
        text="Пользователь "+username+" не принял ваше предложение дружбы.";
        return text;
    }
    public String makeAdminRequestText(String username, String questname){
        String text;
        text="Пользователь "+username+" предлагает вам стать администратором квеста "+questname;
        return text;
    }
    public String makeAdminRequestAcceptedText(String username, String questName){
        String text;
        text="Пользователь "+username+" принял ваше предложение стать администратором квеста "+questName;
        return text;
    }
    public String makeAdminRequestDeclineText(String username, String questName){
        String text;
        text="Пользователь "+username+" не принял ваше предложение стать администратором квеста "+questName;
        return text;
    }
    public String makeCheckedMessage(String taskname, String questname, boolean iscorrect, int points){
        String text;
        text="Ваш ответ на задание \""+taskname+"\" квеста \""+questname+"\" проверено.";
        if(iscorrect){
            text+=" Вам начислено "+points+" баллов.";
        }else{
            text+=" К сожалению, ответ неправильный.";
        }

        return text;
    }
    public MyMessage(){}
}
