package edu.augustana.dataModel;

public class CWMessage {
    private final String cwText;
    private final String englishText;
    private final double frequency;

    public CWMessage(String cwText,String engText, double frequency) {
        this.cwText = cwText;
        this.englishText = engText;
        this.frequency = frequency;
    }

    public String getCwText() {
        return cwText;
    }
    public String getOriginalMessage() {
        return englishText;
    }

    public double getFrequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return "CWMessage{" +
                "cwText='" + cwText + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
