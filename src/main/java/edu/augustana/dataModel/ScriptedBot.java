package edu.augustana.dataModel;

import edu.augustana.data.Scenarios.ScenarioBots.DefaultScenario;
import edu.augustana.data.Scenarios.ScenarioBots.MountainExpedition;
import edu.augustana.data.Scenarios.ScenarioBots.RescueOperation;
import edu.augustana.data.Scenarios.ScenarioBots.WeatherReport;
import edu.augustana.interfaces.Scenario;

import java.util.ArrayList;
import java.util.List;

public class ScriptedBot extends ScenarioBot {

    private String scenarioType;
    private List<ScriptedMessage> scriptedMessages = new ArrayList<>();

    public ScriptedBot(String scenarioType) {
        this.scenarioType = scenarioType;
        initializeScenarioMessages();
    }

    // Initialize radio-themed messages based on the scenario type
    private void initializeScenarioMessages() {
        Scenario scenario;

        switch (scenarioType) {
            case "Rescue Operation":
                scenario = new RescueOperation();
                break;
            case "Weather Report":
                scenario = new WeatherReport();
                break;
            case "Mountain Expedition":
                scenario = new MountainExpedition();
                break;
            default:
                scenario = new DefaultScenario();
                break;
        }

        scriptedMessages.addAll(scenario.getScriptedMessages());
    }

    @Override
    public ScriptedMessage getNewMessage() {
        // Return a message that corresponds to the given time, if available
        if (scriptedMessages.size() > msgIndex) {
            msgIndex++;
            return scriptedMessages.get(msgIndex);
        } else {

            return null; // No new message if no message is scheduled for this time
        }
    }

    @Override
    public ScriptedMessage getResponse(String message, double frequency, int time) {
        // Example responses based on keywords in the message
        if (message.toLowerCase().contains("confirm")) {
            return new ScriptedMessage("Confirmation received. Proceeding as directed. Over.", time);
        } else if (message.toLowerCase().contains("instructions")) {
            return new ScriptedMessage("Instructions acknowledged. Standing by for further details. Over.", time);
        } else if (message.toLowerCase().contains("proceed")) {
            return new ScriptedMessage("Proceeding with the operation as requested. Over.", time);
        }
        return null; // No specific response if message does not trigger a response
    }

    @Override
    public String toString() {
        return scenarioType + " Radio Bot";
    }
}
