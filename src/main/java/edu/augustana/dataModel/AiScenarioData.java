package edu.augustana.dataModel;

import java.util.ArrayList;

// container to serialize/deserialize all scenario-related data.
public class AiScenarioData {
    private final String name;
    private final String description;
    private final String notes;
    private final ArrayList<AiBotDetails> botsDetails;

    public AiScenarioData(String name, String description, String notes,ArrayList<AiBotDetails> botsDetails) {
        this.name = name;
        this.description = description;
        this.notes = notes;
        this.botsDetails = botsDetails;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getNotes() {
        return notes;
    }

    public ArrayList<AiBotDetails> getBotsDetails() {
        return botsDetails;
    }
}

