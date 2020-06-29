package com.slastanna.questory.tables;


import com.mapbox.geojson.Feature;

import java.util.ArrayList;
import java.util.HashMap;

public class Rating {
    public String keyQuest;
    public String nameUser;
    public String keyUser;
    public String lastTaskKey;
    //public String coordinates;
    public String dateStart, dateEnd;
    public int attemptsOnLastTask;
    public int points;
    public ArrayList<ProgressTracker> progress = new ArrayList<>();
    public boolean ended, hintTaken;
    public Rating(){}
}
