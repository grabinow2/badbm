package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * This class is the Concrete Strategy Object in the Strategy design pattern.
 */
public class DiskMark implements Benchmark {

    private static DecimalFormat df = new DecimalFormat("###.###");
    private static final int KILOBYTE = 1024;
    private static final int MEGABYTE = KILOBYTE * KILOBYTE;

    public enum MarkType { READ,WRITE }

    private BenchmarkCommunicator communicator;

    MarkType type;

    private int numOfMarks;

    private int numOfBlocks;

    private int unitsInTest;

    private int blockSize;
    private byte [] blockArr;

    private int startFileNumber;

    private DiskRun run;

    private String testFileDirAsString;

    private boolean multiFile;

    private File testFile;

    private String randomAccessFileMode;

    private int markNum = 0;       // x-axis
    private double bwMbSec = 0;    // y-axis
    private double cumMin = 0;
    private double cumMax = 0;
    private double cumAvg = 0;
    private double sec = 0;
    private double mbWritten;

    private int unitsComplete;

    private Util.SpeedTimer speedTimer = new Util.SpeedTimer(); //In adherence to SRP

    public DiskMark(BenchmarkCommunicator communicator){
        this.communicator = communicator;
    }

    /**
     * Sets up the Disk Benchmark test. NOTE: it is necessary to invoke this method prior to invoking start().
     *
     * @param type determines if this will be a Write or Read test
     * @param numOfBlocks how many blocks will be written/read to/from
     * @param numOfMarks how many marks will be written/read
     * @param blockSize the size of a block in the client system's disk
     * @param startFileNumber the number of the starting file (if using multifile)
     * @param run the DiskRun object that will be used in the testing operation
     * @param randomAccessFileMode the second parameter of java.io.RandomAccessFile 's c-tor
     * @param multiFile true if every mark written/read is to/from a different file.
     * @param testFileDirAsString
     */
    public void initializeTest(MarkType type, int numOfBlocks, int numOfMarks, int blockSize, int startFileNumber,
                               DiskRun run,String randomAccessFileMode, boolean multiFile, String testFileDirAsString) {

        this.type = type;

        this.numOfBlocks = numOfBlocks;

        this.numOfMarks = numOfMarks;

        this.unitsInTest = this.numOfBlocks * this.numOfMarks;

        this.blockSize = blockSize;

        blockArr = new byte[this.blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte) 0xff;
            }
        }

        this.startFileNumber = startFileNumber;

        this.run = run;

        this.randomAccessFileMode = randomAccessFileMode;

        this.multiFile = multiFile;

        this.testFileDirAsString = testFileDirAsString;
    }

    @Override
    public Object start() throws IOException {

        unitsComplete = 0;

        if (!multiFile)
           testFile = new File(testFileDirAsString + File.separator + "testdata.jdm");

        for (int m = startFileNumber; m < startFileNumber + numOfMarks && !isCancelled(); m++) {

            if(multiFile)
                testFile = new File(testFileDirAsString + File.separator + "testdata" + m + ".jdm");

            setMarkNum(m);

            startTimer();

            long totalBytesWrittenInMark = 0;

            RandomAccessFile rAccFile = new RandomAccessFile(testFile, randomAccessFileMode);
            for (int b = 0; b < numOfBlocks; b++) {
                if (run.getBlockOrder() == DiskRun.BlockSequence.RANDOM) {
                    int rLoc = Util.randInt(0, numOfBlocks - 1);
                    rAccFile.seek(rLoc * blockSize);
                } else {
                    rAccFile.seek(b * blockSize);
                }

                execDiskOp(rAccFile);

                totalBytesWrittenInMark += blockSize;
                updateCompletionRate();
            }

            long elapsedTimeNs = stopTimer();

            calculateResults(elapsedTimeNs, totalBytesWrittenInMark);

            communicateResults();

            setDiskRunStats();
        }
        return run;
    }

    @Override
    public boolean isCancelled() {
        return communicator.isCancelled();
    }

    private void communicateResults() {
        msg(getRecentStatsAsString());
        updateMetrics(DiskMark.this);
        publish(DiskMark.this);
    }

    private void updateCompletionRate() {
        unitsComplete++;
        float percentComplete = (float) unitsComplete / (float) unitsInTest * 100f;
        setProgress((int) percentComplete);
    }


    private void execDiskOp(RandomAccessFile rAccFile) throws IOException {
        if (type == MarkType.WRITE)
            rAccFile.write(blockArr, 0, blockSize);
        else
            rAccFile.readFully(blockArr, 0, blockSize);
    }

    /**
     * loads the stats into the DiskRun object to be returned
     * todo: DiskRun violates SRP
     */
    private void setDiskRunStats() {
        run.setRunMax(getCumMax());
        run.setRunMin(getCumMin());
        run.setRunAvg(getCumAvg());
        run.setEndTime(new Date());
    }

    private void calculateResults(long elapsedTimeNs, long totalBytesWrittenInMark) {
        sec = (double) elapsedTimeNs / (double) 1000000000;
        mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
        setBwMbSec(mbWritten / sec);
    }

    @Override
    public void startTimer() { speedTimer.start(); }

    @Override
    public long stopTimer() {
        return speedTimer.stop();
    }

    public int getMarkNum() { return markNum; }

    public void setMarkNum(int markNum) { this.markNum = markNum; }

    public double getBwMbSec() { return bwMbSec; }

    public void setBwMbSec(double bwMbSec) { this.bwMbSec = bwMbSec; }

    @Override
    public void setCumMin(double min) { cumMin = min; }

    @Override
    public double getCumMin() {
        return cumMin;
    }

    @Override
    public void setCumMax(double max) { cumMax = max; }

    @Override
    public double getCumMax() {
        return cumMax;
    }

    @Override
    public void setCumAvg(double avg) { cumAvg = avg; }

    @Override
    public double getCumAvg() {
        return cumAvg;
    }

    String getBwMbSecAsString() {
        return df.format(getBwMbSec());
    }

    String getMinAsString() {
        return df.format(getCumMin());
    }

    String getMaxAsString() {
        return df.format(getCumMax());
    }

    String getAvgAsString() {
        return df.format(getCumAvg());
    }

    public double getSec() {
        return sec;
    }

    public double getMbWritten() {
        return mbWritten;
    }

    @Override
    public void publish(Object object) {
        communicator.communicatablePublish(object);
    }

    @Override
    public void setProgress(int i) {
        communicator.setCommunicatableProgress(i);
    }

    @Override
    public void updateMetrics(Object object){
        communicator.communicatableUpdateMetrics(object);
    }

    @Override
    public void msg(String s){
        communicator.communicatableMsg(s);
    }

    public String getRecentStatsAsString(){
        return "m:" + getMarkNum() + " write IO is " + getBwMbSecAsString() + " MB/s     "
                + "(" + Util.displayString(getMbWritten()) + "MB written in "
                + Util.displayString(getSec()) + " sec)";
    }

    @Override
    public String toString() {
        return "Mark("+type+"): "+getMarkNum()+" bwMbSec: "+getBwMbSecAsString()+" avg: "+getAvgAsString();
    }

}
