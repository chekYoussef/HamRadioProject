package edu.augustana.dataModel;

// container to serialize/deserialize all scenario-related data.
public class ScenarioData {
    private double duration;
    private String synopsis;
    private String botType;
    private double transmissionSpeed;

    // Constructor
    public ScenarioData(double duration, String synopsis, String botType, double transmissionSpeed) {
        this.duration = duration;
        this.synopsis = synopsis;
        this.botType = botType;
        this.transmissionSpeed = transmissionSpeed;
    }

    // Method to save scenario data to JSON

    @Override
    public String toString() {
        return "ScenarioData{" +
                "duration=" + duration +
                ", synopsis='" + synopsis + '\'' +
                ", botType='" + botType + '\'' +
                ", transmissionSpeed=" + transmissionSpeed +
                '}';
    }


    // Getters
    public double getDuration() { return duration; }
    public String getSynopsis() { return synopsis; }
    public String getBotType() { return botType; }
    public double getTransmissionSpeed() { return transmissionSpeed; }
}

