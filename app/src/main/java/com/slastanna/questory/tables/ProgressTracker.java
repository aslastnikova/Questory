package com.slastanna.questory.tables;

public class ProgressTracker {
    public String coordinates; // JSON
    public int state; //TODO rename in state
    public int mapstate=0; // 0 - current; 1 - hidden; 2 - last;
    public ProgressTracker(){}
    public ProgressTracker(String coordinates, int state){
        this.state=state;
        this.coordinates=coordinates;
    }
    public ProgressTracker(String coordinates, int state, int mapstate){
        this.state=state;
        this.coordinates=coordinates;
        this.mapstate=mapstate;
    }
}
