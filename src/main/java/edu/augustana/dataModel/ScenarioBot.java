package edu.augustana.dataModel;

public abstract class ScenarioBot {
    public String name;
    public int msgIndex = 0;
    public abstract ScriptedMessage getNewMessage();
    public abstract ScriptedMessage getResponse(String message, double frequency,int time);


}
