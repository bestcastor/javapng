package com.sixlegs.png;

import java.io.*;
import junit.framework.*;

public class SuiteTest
extends TestCase
{
    public void testRead()
    throws Exception
    {
        long t = System.currentTimeMillis();
        SuiteViewer.main(new String[0]);
        t = System.currentTimeMillis() - t;
        System.err.println("Read PngSuite in " + t + " ms");
    }

    public SuiteTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(SuiteTest.class);
    }
}
