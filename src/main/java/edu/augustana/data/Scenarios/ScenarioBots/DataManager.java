package edu.augustana.data.Scenarios.ScenarioBots;

import edu.augustana.dataModel.ScenarioData;

public class DataManager {
    private static DataManager instance;
    private ScenarioData scenarioData;

    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public ScenarioData getScenarioData() {
        return scenarioData;
    }

    public void setScenarioData(ScenarioData scenarioData) {
        this.scenarioData = scenarioData;
    }
}

