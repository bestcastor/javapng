package com.sixlegs.png;

import java.lang.reflect.Constructor;
import java.io.*;
import java.util.*;
import java.util.zip.Checksum;
import junit.framework.*;

abstract public class PngTestCase
extends TestCase
{
    private static final String TEST_METHODS = "testmethods";
    private static final String DELIMITER = ",";

    protected PngTestCase(String name)
    {
        super(name);
    }

    protected static TestSuite getSuite(Class testClass)
    {
        if (!TestCase.class.isAssignableFrom(testClass))
            throw new IllegalArgumentException("Must pass in a subclass of TestCase");
        String testMethods = System.getProperty(TEST_METHODS);
        if (testMethods == null || testMethods.length() == 0)
            return new TestSuite(testClass);

        TestSuite suite = new TestSuite();
        try {
            Constructor constructor = testClass.getConstructor(new Class[] {String.class});
            List testCaseNames = getTestCaseNames(testMethods);
            for (Iterator testCases = testCaseNames.iterator(); testCases.hasNext();) {
                String testCaseName = (String)testCases.next();
                suite.addTest((TestCase)constructor.newInstance(new Object[] {testCaseName}));
            }
        } catch (Exception e) {
            throw new RuntimeException(testClass.getName() + " doesn't have the proper constructor");
        }
        return suite;
    }

    private static List getTestCaseNames(String testMethods)
    {
        List testMethodNames = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(testMethods, DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            testMethodNames.add(tokenizer.nextToken());
        }
        return testMethodNames;
    }

    public static long getChecksum(Checksum checksum, File file, byte[] buf)
    throws IOException
    {
        checksum.reset();
        InputStream in = new FileInputStream(file);
        try {
            for (;;) {
                int amt = in.read(buf);
                if (amt < 0)
                    break;
                checksum.update(buf, 0, amt);
            }
        } finally {
            in.close();
        }
        return checksum.getValue();
    }

    public static void skipFully(DataInput in, int n)
    throws IOException
    {
        while (n > 0) {
            int amt = in.skipBytes(n);
            if (amt == 0) {
                in.readByte();
                n--;
            } else {
                n -= amt;
            }
        }
    }
}
