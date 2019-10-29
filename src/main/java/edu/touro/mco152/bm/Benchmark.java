package edu.touro.mco152.bm;

import java.io.File;
import java.io.IOException;

/**
 * An object that can perform benchmark tests on various computer components. This interface represents the Strategy
 * interface of the Strategy design pattern.
 */
public interface Benchmark {

    /**
     * This method will run the benchmark test in its entirety.
     *
     * @return An object that can contain statistics of the test
     * @throws Exception In the event of some exception. For instance, an IOException when
     * benchmarking a computer's disk.
     */
    Object start() throws Exception;

    /**
     * Determines if the test was cancelled. In most cases, this should call the isCancelled() method of the
     * BenchmarkCommunicator interface.
     * @return true if the test was cancelled, false otherwise.
     */
    boolean isCancelled();

    /**
     * Starts a timer. This method should be called somewhere at the beginning of the start() method body.
     */
    void startTimer();

    /**
     * Stops the timer. This method should be called somewhere at the end of the start() method body.
     *
     * @return the time elapsed from the most recent startTimer() method call in nanoseconds.
     */
    long stopTimer();

    /**
     * Sets the cumulative minimum of all benchmark tests ran in a given set.
     * @param min
     */
    void setCumMin(double min);

    /**
     * @return The cumulative minimum of all benchmark tests ran in a given set.
     */
    double getCumMin();

    /**
     * Sets the cumulative maximum of all benchmark tests ran in a given set.
     * @param max
     */
    void setCumMax(double max);

    /**
     * @return The cumulative maximum of all benchmark tests ran in a given set.
     */
    double getCumMax();

    /**
     * Sets the cumulative average of all benchmark tests ran in a given set.
     * @param avg
     */
    void setCumAvg(double avg);

    /**
     * @return The cumulative average of all benchmark tests ran in a given set.
     */
    double getCumAvg();

    /**
     * Publishes an Object containing information of the most recent test(s) ran. Generally, this should simply invoke
     * the communicatablePublish() method of the BenchmarkCommunicator interface.
     * @param object containing relevant information. In most cases, is this Object implementing Benchmark.
     */
    void publish(Object object);

    /**
     * Sets the Progress field. Modelled after the SwingWorker progress field, this can be a value from
     * 0 to 99 corresponding with a percent of completion (although that isn't enforced). Generally,
     * this should simply invoke the communicatableSetProgress() method of the BenchmarkCommunicator interface.
     * @param i value of progress.
     */
    void setProgress(int i);

    /**
     * Similar to publish(), this will update certain metrics of the benchmark application based on the most recent
     * test(s) ran. Generally, this should simply invoke the communicatableUpdateMetrics() method of the
     * BenchmarkCommunicator interface.
     * @param object containing relevant information. In most cases, is this Object implementing Benchmark.
     */
    void updateMetrics(Object object);

    /**
     * A simple encapsulation of a println statement (or something equivalent).Generally,
     * this should simply invoke the msg() method of the BenchmarkCommunicator interface.
     * @param s the string to be posted.
     */
    void msg(String s);

}
