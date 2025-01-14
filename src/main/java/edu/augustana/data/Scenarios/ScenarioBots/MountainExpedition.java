package edu.augustana.data.Scenarios.ScenarioBots;

import edu.augustana.dataModel.ScriptedMessage;
import edu.augustana.interfaces.Scenario;

import java.util.ArrayList;
import java.util.List;

public class MountainExpedition implements Scenario {
    @Override
    public List<ScriptedMessage> getScriptedMessages() {
        List<ScriptedMessage> messages = new ArrayList<>();
        messages.add(new ScriptedMessage("QTH? DE Bravo Expedition. Alt 5000 ft. Log OK? K", 0)); // Asking for location confirmation
        messages.add(new ScriptedMessage("WX check in progress. QRU? DE Bravo Expedition. K", 2)); // Asking if there are updates
        messages.add(new ScriptedMessage("QRV on ascent. Equip FB. Any traffic? DE Bravo Expedition. K", 4)); // Reporting readiness and asking for updates
        messages.add(new ScriptedMessage("QTH halfway. WX FB. Proceed summit? DE Bravo Expedition. K", 6)); // Reporting halfway status and asking for clearance
        messages.add(new ScriptedMessage("QSY summit. QRT on standby. DE Bravo Expedition. SK", 10)); // Reporting summit reached, switching operations
        return messages;
    }
}
