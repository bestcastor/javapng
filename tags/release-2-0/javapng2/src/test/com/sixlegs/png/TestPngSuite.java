package com.sixlegs.png;

import java.io.*;
import junit.framework.*;

public class TestPngSuite
extends PngTestCase
{
    public void testRead()
    throws Exception
    {
        long t = System.currentTimeMillis();
        new SuiteViewer().render(false);
        t = System.currentTimeMillis() - t;
        System.err.println("Read PngSuite in " + t + " ms");
    }

    public void testNoReduce()
    throws Exception
    {
        PngConfig config = new PngConfig.Builder()
            .gammaCorrect(false)
            .reduce16(false)
            .build();
        new SuiteViewer(config).render(false);
    }

    public void testConvertIndexed()
    throws Exception
    {
        PngConfig config = new PngConfig.Builder()
            .convertIndexed(true)
            .build();
        new SuiteViewer(config).render(false);
    }

    public TestPngSuite(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return getSuite(TestPngSuite.class);
    }
}
