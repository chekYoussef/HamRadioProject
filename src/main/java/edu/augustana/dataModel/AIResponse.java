package edu.augustana.dataModel;

public class AIResponse {
    private final String message;
    private final boolean isObjectiveComplete;

    public AIResponse(String message, boolean isObjectiveComplete) {
        this.message = message;
        this.isObjectiveComplete = isObjectiveComplete;
    }

    public String getMessage() {
        return message;
    }

    public boolean isObjectiveAccomplished() {
        return isObjectiveComplete;
    }

    @Override
    public String toString() {
        return "AiResponse{" +
                "message='" + message + '\'' +
                ", isObjectiveComplete=" + isObjectiveComplete +
                '}';
    }
}