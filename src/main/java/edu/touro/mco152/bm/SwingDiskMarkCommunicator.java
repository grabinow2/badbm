package edu.touro.mco152.bm;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

import javax.persistence.EntityManager;
import javax.swing.*;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import edu.touro.mco152.bm.ui.Gui;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A SwingWorker thread that takes parameters of a Disk benchmarking test from the App, and inputs them into a DiskMark.
 * The worker thread then invokes the start() method of DiskMark, and handles the output that DiskMark produces.
 * This class is the Context Object in the Strategy design pattern, and adheres to SRP by only acting as the
 * Context Object.
 */
public class SwingDiskMarkCommunicator extends SwingWorker<Boolean, DiskMark> implements BenchmarkCommunicator {

    DiskMark testRunner;
    int startFileNum = nextMarkNumber;
    DiskRun run = new DiskRun();

    @Override
    public void setCommunicatableProgress(int i) {
        setProgress(i);
    }

    @Override
    public void communicatablePublish(Object object) {
        publish((DiskMark) object);
    }

    @Override
    public void communicatableUpdateMetrics(Object object){
        App.updateMetrics((DiskMark) object);
    }

    @Override
    public void communicatableMsg(String s){
        msg(s);
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        System.out.println("*** starting new worker thread");
        msg("Running readTest "+readTest+"   writeTest "+writeTest);
        msg("num files: "+numOfMarks+", num blocks: "+numOfBlocks
                +", block size (kb): "+blockSizeKb+", blockSequence: "+blockSequence);

        testRunner = new DiskMark(SwingDiskMarkCommunicator.this);

        Gui.updateLegend();

        if (autoReset == true) {
            App.resetTestData();
            Gui.resetTestData();
        }

        if(writeTest) {

            testRunner.initializeTest(WRITE, numOfBlocks, numOfMarks, blockSizeKb*KILOBYTE,
                    startFileNum, setupDiskRun(DiskRun.IOMode.WRITE), App.writeSyncEnable ? "rwd" : "rw",
                    multiFile, dataDir.getAbsolutePath());

            runTest();
        }

        if (readTest && writeTest && !isCancelled()) {
            JOptionPane.showMessageDialog(Gui.mainFrame,
                    "For valid READ measurements please clear the disk cache by\n" +
                            "using the included RAMMap.exe or flushmem.exe utilities.\n" +
                            "Removable drives can be disconnected and reconnected.\n" +
                            "For system drives use the WRITE and READ operations \n" +
                            "independantly by doing a cold reboot after the WRITE",
                    "Clear Disk Cache Now",JOptionPane.PLAIN_MESSAGE);
        }

        if(readTest){
            testRunner.initializeTest(READ, numOfBlocks, numOfMarks, blockSizeKb*KILOBYTE,
                    startFileNum, setupDiskRun(DiskRun.IOMode.READ), App.writeSyncEnable ? "rwd" : "rw",
                    multiFile, dataDir.getAbsolutePath());

            runTest();
        }
        nextMarkNumber += numOfMarks;
        return true;
    }

    @Override
    protected void process(List<DiskMark> markList) {
        markList.stream().forEach((m) -> {
            if (m.type== DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(m);
            } else {
                Gui.addReadMark(m);
            }
        });
    }

    @Override
    protected void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }

    private void runTest() {

        try {
            run = (DiskRun) testRunner.start();
        }
        catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        postTestOperations();

    }

    private void postTestOperations() {
        persistDiskRun(run);
        Gui.runPanel.addRun(run);
    }

    /**
     * save stats via the entity manager.
     * @param run
     */
    private void persistDiskRun(DiskRun run) {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }

    private DiskRun setupDiskRun(DiskRun.IOMode IOMode) {

        run = new DiskRun(IOMode, blockSequence);

        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize(targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        //while the following three lines aren't really relevant to setting up the DiskRun object,
        // this is really the only place for them
        msg("disk info: ("+ run.getDiskInfo()+")");
        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        return run;
    }
}
