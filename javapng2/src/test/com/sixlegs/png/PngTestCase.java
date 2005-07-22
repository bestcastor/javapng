package com.sixlegs.png;

import java.lang.reflect.Constructor;
import java.util.*;
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
}
