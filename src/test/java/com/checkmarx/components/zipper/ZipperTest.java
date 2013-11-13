package com.checkmarx.components.zipper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public void OfftestPattern1() {


        Zipper zipper = new Zipper();
        String filters = "**, !**/f1.*\n!**/f2.*, !* *   ,/pr,pr/, ! .DS_Store,  !.DS_Store, !! .DS_Store,,,   ,\" \" ";
        try {
            byte[] zip = zipper.zip(new File("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/temp/basedir"),filters,0,null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }

    public void OfftestPattern2() {


        Zipper zipper = new Zipper();
        String filters = "!***/*//*.txt";
        try {
            byte[] zip = zipper.zip(new File("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/temp/basedir"),filters,0,null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }


    public void OfftestPattern3() {


        Zipper zipper = new Zipper();
        String filters = "a*//*.txt";
        try {
            byte[] zip = zipper.zip(new File("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/temp/basedir"),filters,0,null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(true);
    }
    public void OfftestZip1() {


        Zipper zipper = new Zipper();
        String filters = "!f1.txt";

        ZipListener zipListener = new ZipListener() {
            public void updateProgress(String currentFileName, long currentCompressedSize) {
                System.out.println("ZipListener.updateProgress: " + currentFileName + " size: " + currentCompressedSize);
            }
        };

        try {
            FileOutputStream out = new FileOutputStream("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/zipper/target/work/testOutput.zip");
            zipper.zip(new File("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/temp/basedir"),filters,out,100000,zipListener);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        assertTrue(true);
    }

    public void testErrors1() {


        Zipper zipper = new Zipper();
        String filters = "!f1.txt";

        ZipListener zipListener = new ZipListener() {
            public void updateProgress(String currentFileName, long currentCompressedSize) {
                System.out.println("ZipListener.updateProgress: " + currentFileName + " size: " + currentCompressedSize);
            }
        };

        try {
            FileOutputStream out = new FileOutputStream("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/zipper/target/work/testOutput.zip");
            zipper.zip(new File("/Users/denis/Documents/iOSDevMac/Checkmarx/Zipper/temp/basedir"),filters,out,0,zipListener);
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        assertTrue(true);
    }
}

