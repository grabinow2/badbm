package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    /**
     * tests that getVersion method completes within 1 second
     * performance test
     */
    @Test
    @Timeout(1)
    void getVersion() {
        App.getVersion();
    }
}