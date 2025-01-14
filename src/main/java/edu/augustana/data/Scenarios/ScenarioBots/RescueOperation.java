package edu.augustana.data.Scenarios.ScenarioBots;


import edu.augustana.dataModel.ScriptedMessage;
import edu.augustana.interfaces.Scenario;

import java.util.ArrayList;
import java.util.List;

public class RescueOperation implements Scenario {
    @Override
    public List<ScriptedMessage> getScriptedMessages() {
        List<ScriptedMessage> messages = new ArrayList<>();
        messages.add(new ScriptedMessage("QRL? DE Rescue Alpha. K", 0)); // Check frequency
        messages.add(new ScriptedMessage("Base, en route to QTH. QRU? K", 0)); // Confirm deployment
        messages.add(new ScriptedMessage("ETA 10 mins. QRU? K", 2)); // ETA update
        messages.add(new ScriptedMessage("At canyon. QRT or QSY? K", 5)); // Request instructions
        messages.add(new ScriptedMessage("Target visual. QRV for extraction? K", 8)); // Confirm extraction
        messages.add(new ScriptedMessage("Extraction done. QRX or RTB? SK", 10)); // Await further orders
        return messages;
    }
}

