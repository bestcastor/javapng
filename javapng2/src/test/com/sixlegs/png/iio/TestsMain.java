package com.sixlegs.png.iio;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestsMain {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite t = new TestSuite();

		/* Adding tests */
		//t.addTest(Test_PngImageReaderSpi.suite());
		//t.addTest(Test_PngImageReader.suite());
		//t.addTest(Test_NativeMetadata.suite());
		t.addTest(Test_StandardMetadata.suite());

		return t;
	}
}
