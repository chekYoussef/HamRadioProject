package edu.augustana.data.Scenarios.ScenarioBots;

import edu.augustana.dataModel.ScriptedMessage;
import edu.augustana.interfaces.Scenario;

import java.util.ArrayList;
import java.util.List;

public class WeatherReport implements Scenario {
    @Override
    public List<ScriptedMessage> getScriptedMessages() {
        List<ScriptedMessage> messages = new ArrayList<>();
        messages.add(new ScriptedMessage("QRL? DE WX Station Delta. K", 0)); // Asking if the frequency is in use
        messages.add(new ScriptedMessage("Current temp 75°F, clear skies. QRU further data? DE WX Station Delta. K", 1)); // Providing temperature and asking for further requests
        messages.add(new ScriptedMessage("Wind 5 MPH steady. Shall I monitor for QSP? DE WX Station Delta. K", 2)); // Reporting wind and offering to relay any messages
        messages.add(new ScriptedMessage("Humidity 60%. Request for QTH secondary? DE WX Station Delta. K", 3)); // Reporting humidity and asking about additional location requests
        messages.add(new ScriptedMessage("WX rpt complete. QRU any further? DE WX Station Delta. SK", 4)); // Completing the weather report and asking if anything else is needed
        return messages;
    }
}
