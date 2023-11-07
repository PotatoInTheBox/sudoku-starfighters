package view_controller;

public class FrameRateTracker {

    private long lastFpsTime = 0l;
    private int fpsCounter = 0;
    private final int bufferSize; // buffer size
    private long[] fpsRecord;

    public FrameRateTracker(int frameCounterBufferSize) {
        bufferSize = frameCounterBufferSize;
        fpsRecord = new long[bufferSize];
    }

    public void logFrameUpdate() {
        long thisTime = System.nanoTime();
        fpsRecord[fpsCounter % bufferSize] = (thisTime - lastFpsTime);
        lastFpsTime = thisTime;
        fpsCounter++;
    }

    public double getAverageUpdate() {
        long total = 0l;
        for (int i = 0; i < bufferSize; i++) {
            total += fpsRecord[i];
        }
        double average = 1000d / ((double) (total / bufferSize) / 1_000_000d);
        return average;
    }
}
