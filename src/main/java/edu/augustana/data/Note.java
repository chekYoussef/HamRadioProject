package edu.augustana.data;

public enum Note {
    TONE; // Generic tone for Morse code

    public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
    private double frequency; // Frequency in Hz

    // Constructor to set frequency
    Note() {
        this.frequency = 440.0; // Default to 440 Hz (A4)
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    // Generate a sine wave based on the frequency and volume
    public byte[] data(int volume) {
        byte[] sin = new byte[SAMPLE_RATE];
        double maxAmplitude = 127.0 * (volume * 80.0 / 100.0 / 100.0);

        for (int i = 0; i < sin.length; i++) {
            double period = (double) SAMPLE_RATE / frequency;
            double angle = 2.0 * Math.PI * i / period;
            sin[i] = (byte) (Math.sin(angle) * maxAmplitude);
        }
        return sin;
    }
}
