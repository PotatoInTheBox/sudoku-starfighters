package view_controller.utils;

/**
 * FrameRateTracker is a tool for keeping track of how much time has passed.
 * More specifically it can keep track of how many updates have been made in
 * a given time interval. This is useful for debugging.
 */
public class FrameRateTracker {

    private long lastFpsTime = 0l;
    private int fpsCounter = 0;
    private final int bufferSize; // buffer size
    private long[] fpsRecord;
    private boolean isStarting = true;

    /**
     * Tracks the frame rate
     * 
     * @param frameCounterBufferSize Adjusts the buffer
     */
    public FrameRateTracker(int frameCounterBufferSize) {
        bufferSize = frameCounterBufferSize;
        fpsRecord = new long[bufferSize];
    }

    /**
     * Performs a log frame update
     */
    public void logFrameUpdate() {
        long thisTime = System.nanoTime();
        // don't record if a frame took longer than one second
        if (thisTime - lastFpsTime > 1_000_000_000d) {
            lastFpsTime = thisTime;
            return;
        }
        fpsRecord[fpsCounter % bufferSize] = (thisTime - lastFpsTime);
        lastFpsTime = thisTime;
        fpsCounter++;
        if (bufferSize == fpsCounter) {
            isStarting = false;
        }
    }

    /**
     * Gets the average duration for an update method call
     * 
     * @return The average duration
     */
    public double getAverageUpdate() {
        if (isStarting && fpsCounter == 0) {
            return 0;
        }
        long total = 0l;
        for (int i = 0; i < bufferSize && !isStarting; i++) {
            total += fpsRecord[i];
        }
        for (int i = 0; i < fpsCounter % bufferSize && isStarting; i++) {
            total += fpsRecord[i];
        }
        if (!isStarting) {
            double average = 1000d / (((double) total / bufferSize) / 1_000_000d);
            return average;
        } else {
            double average = 1000d / (((double) total / (fpsCounter % bufferSize)) / 1_000_000d);
            return average;
        }

    }
}
