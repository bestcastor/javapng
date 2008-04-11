package com.sixlegs.image.png;

import java.io.*;
import junit.framework.*;

public class IoTest
extends TestCase
{
    public void testConcatenatedImages()
    throws Exception
    {
        InputStream in = getClass().getResourceAsStream("/images/misc/concat.dat");
        PngImage p1 = new PngImage(in, false);
        p1.getEverything();
        PngImage p2 = new PngImage(in, false);
        p2.getEverything();
        assertEquals(32, p1.getWidth());
        assertEquals(32, p2.getWidth());
    }

    public IoTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(IoTest.class);
    }

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }
}
