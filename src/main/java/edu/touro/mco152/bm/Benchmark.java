package edu.touro.mco152.bm;

/**
 * An object that can perform benchmark tests on various computer components.
 */
public interface Benchmark {

    void start();

    void reset();

    void startTimer();

    double endTimer();

    double getCumMin();

    double getCumMax();

    double getCumAvg();

}
