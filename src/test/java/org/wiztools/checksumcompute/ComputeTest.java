package org.wiztools.checksumcompute;

import org.wiztools.checksumcompute.ProgressCallback;
import org.wiztools.checksumcompute.Compute;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author subhash
 */
public class ComputeTest {

    public ComputeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of compute method, of class Compute.
     */
    @Test
    public void testCompute() throws Exception {
        System.out.println("compute");
        File f = new File("src/test/resources/fireworks.jpg");
        ProgressCallback progressCallback = new MyProgressCallback();
        new Compute().compute(f, progressCallback);
    }

}