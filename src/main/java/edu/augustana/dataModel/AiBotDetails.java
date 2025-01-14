package edu.augustana.dataModel;


/**
 * Represents the details of an AI bot in the Scenario AI Builder.
 */
public class AiBotDetails {
    private String name;
    private String objective;
    private boolean isStartingBot;

    // Constructor
    public AiBotDetails(String name, String objective, boolean isStartingBot) {
        this.name = name;
        this.objective = objective;
        this.isStartingBot = isStartingBot;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public boolean isStartingBot() {
        return isStartingBot;
    }

    public void setStartingBot(boolean isStartingBot) {
        this.isStartingBot = isStartingBot;
    }

    @Override
    public String toString() {
        return name; // The ListView will display the bot's name.
    }
}
