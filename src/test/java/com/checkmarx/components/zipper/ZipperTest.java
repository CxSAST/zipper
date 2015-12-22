package com.checkmarx.components.zipper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.notification.RunListener.ThreadSafe;

/**
 * Created with IntelliJ IDEA. User: denis Date: 12/11/2013 Time: 15:52 Description:
 */
@ThreadSafe
public class ZipperTest {

	String root;
	String output;

	@Before
	public void setup() {
		String dir = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		String projectRoot = dir.substring(0, dir.indexOf("build"));
		root = projectRoot + "/src";
		output = projectRoot + "/build/output" + UUID.randomUUID() + ".zip";
	}

	@Test
	public void OfftestPattern1() throws IOException {

		Zipper zipper = new Zipper();
		String filters = "**, !**/f1.*\n!**/f2.*, !* *   ,/pr,pr/, ! .DS_Store,  !.DS_Store, !! .DS_Store,,,   ,\" \" ";

		byte[] zip = zipper.zip(new File(root), filters, 0, null);
	}

	@Test
	public void OfftestPattern2() throws IOException {

		Zipper zipper = new Zipper();
		String filters = "!***/*//*.txt";

		byte[] zip = zipper.zip(new File(root), filters, 0, null);
	}

	@Test(expected = IOException.class)
	public void OfftestPattern3() throws IOException {
		Zipper zipper = new Zipper();
		String filters = "a*//*.txt";

		byte[] zip = zipper.zip(new File("root"), filters, 0, null);
	}

	@Test
	public void OfftestZip1() throws IOException {

		Zipper zipper = new Zipper();
		String filters = "!f1.txt";

		ZipListener zipListener = new ZipListener() {
			@Override
			public void updateProgress(String currentFileName, long currentCompressedSize) {
				System.out.println("ZipListener.updateProgress: " + currentFileName + " size: " + currentCompressedSize);
			}
		};

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(output);
			zipper.zip(new File(root), filters, out, 100000, zipListener);

		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	@Test
	@Ignore
	public void testZip2() throws IOException {

		Zipper zipper = new Zipper();
		String filters = "!**/*.mov";

		ZipListener zipListener = new ZipListener() {
			@Override
			public void updateProgress(String currentFileName, long currentCompressedSize) {
				System.out.println("ZipListener.updateProgress: " + currentFileName + " size: " + currentCompressedSize);
			}
		};

		FileOutputStream out = null;
		try {
			byte[] outArray = zipper.zip(new File(root), filters, 0, zipListener);

			out = new FileOutputStream(output);
			out.write(outArray);
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	@Test
	public void OfftestErrors1() throws IOException {

		Zipper zipper = new Zipper();
		String filters = "!f1.txt";

		ZipListener zipListener = new ZipListener() {
			@Override
			public void updateProgress(String currentFileName, long currentCompressedSize) {
				System.out.println("ZipListener.updateProgress: " + currentFileName + " size: " + currentCompressedSize);
			}
		};
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(output);
			zipper.zip(new File(root), filters, out, 0, zipListener);
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	@Test
	public void testBrokenLink() throws IOException {
		Zipper zipper = new Zipper();
		File baseDir = new File(root);

		byte[] out = zipper.zip(baseDir, "", -1, null);

	}
}
