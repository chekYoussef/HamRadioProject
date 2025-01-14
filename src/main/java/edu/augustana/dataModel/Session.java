package edu.augustana.dataModel;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private final String sessionId;
    private final String botName;
    private final String botObjective;
    private final List<String> history;

    public Session(String sessionId,String botName, String botObjective) {
        this.sessionId = sessionId;
        this.botName = botName;
        this.botObjective = botObjective;
        this.history = new ArrayList<>();
    }

    public String getSessionId() {
        return sessionId;
    }
    public String getBotObjective() {
        return botObjective;
    }
    public String getBotName() {
        return botName;
    }

    public List<String> getHistory() {
        return history;
    }

    public void addMessage(String message) {
        history.add(message);
    }

    public String getLastMessage() {
        return history.isEmpty() ? null : history.get(history.size() - 1);
    }
}
