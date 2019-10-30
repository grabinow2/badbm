package edu.touro.mco152.bm.persist;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DiskRunTest {

    DiskRun diskRun = new DiskRun();

    /**
     * cross-checks that setter is functioning properly
     * @param numMarks a set of ints
     * Broken!!! I made it set to 5 always
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 5, 7, 7000})
    void setNumMarks(int numMarks) {

        diskRun.setNumMarks(numMarks);

        assertEquals(numMarks, diskRun.getNumMarks());
    }
}