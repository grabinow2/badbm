package edu.touro.mco152.bm;

import javax.swing.*;

public abstract class SwingBenchmarker extends SwingWorker implements Benchmark {

    @Override
    public void start() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void startTimer() {

    }

    @Override
    public double endTimer() {
        return 0;
    }

    @Override
    public double getCumMin() {
        return 0;
    }

    @Override
    public double getCumMax() {
        return 0;
    }

    @Override
    public double getCumAvg() {
        return 0;
    }
}
