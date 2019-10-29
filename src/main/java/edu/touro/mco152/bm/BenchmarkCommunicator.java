package edu.touro.mco152.bm;

/**
 * An object that has the ability to communicate the progress and results of Benchmark objects.
 */
public interface BenchmarkCommunicator {

    /**
     * Sets a progress field. Modelled after the SwingWorker setProgress method, usually is most useful when set to a
     * number between 0 and 99 (although that is not enforced).
     * @param i value of progress.
     */
    void setCommunicatableProgress(int i);

    /**
     * Publishes an object containing results of a benchmark-test operation.
     * @param object In most cases, object should be the Benchmark object.
     */
    void communicatablePublish(Object object);

    /**
     * Updates certain metrics in a benchmark application from the results of a recent benchmark-test operation.
     * @param object That will contain these results.
     */
    void communicatableUpdateMetrics(Object object);

    /**
     * A simple encapsulation of a println-esque message-poster.
     * @param s the message to be posted.
     */
    void communicatableMsg(String s);

    /**
     * determines if an ongoing benchmark-test operation is to be cancelled
     * @return true if the test is to be cancelled, false otherwise.
     */
    boolean isCancelled();
}
