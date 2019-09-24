package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    /**
     * Parameterized Test to confirm that deleteDirectory safely returns false when illegal or incorrect Files are
     * passed in to test the Boundary conditions: Conformance, Reference, and Existence.
     * Utilizes JUnit 5's Implicit Argument Conversion to create File objects from strings
     * @param path
     */
    @ParameterizedTest
    @ValueSource(strings = {"A Cow", "<>*:!", "C:\\NonExistantDir\\NonExistantFile.txt"})
    void deleteDirectory(File path) {
        assertFalse(Util.deleteDirectory(path));
    }

    /**
     * forces nullPointerException error condition that occurs when a null File is passed to deleteDirectory method
     * @param path is null
     */
    @ParameterizedTest
    @NullSource
    void deleteDirectoryException(File path){
        assertThrows(NullPointerException.class, () -> {Util.deleteDirectory(path);} );
    }

    /**
     * Test to establish that the randInt method of Util class returns an integer between min and max inclusive
     * in accordance with the Right testing concept.
     */
    @Test
    void randInt() {

        int min = 1;
        int max = 5;

        int rand = Util.randInt(min,max);

        assertTrue(rand >= min, "random number returned was less than min arg passed in");
        assertTrue(rand <= max, "random number returned was greater than max arg passed in");

    }
}