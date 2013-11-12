package com.checkmarx.components.zipper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: denis
 * Date: 12/11/2013
 * Time: 15:52
 * Description:
 */
public class ZipperTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ZipperTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ZipperTest.class);
    }

    public void testPattern1() {


        Zipper zipper = new Zipper();
        String filters = "**, !**/f1.*\n!**/f2.*, !* *   ,/pr,pr/, ! .DS_Store,  !.DS_Store, !! .DS_Store,,,   ,\" \" ";
        byte[] zip = zipper.zip(new File("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/temp/basedir"),filters);

        assertTrue(true);
    }

    public void testPattern2() {


        Zipper zipper = new Zipper();
        String filters = "!**/*.txt";
        byte[] zip = zipper.zip(new File("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/temp/basedir"),filters);

        assertTrue(true);
    }
}

