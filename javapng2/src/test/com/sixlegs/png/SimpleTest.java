package com.sixlegs.png;

import java.io.*;
import junit.framework.*;

public class SimpleTest
extends TestCase
{
    public void testRead()
    throws Exception
    {
        PngImage png = readResource("/images/misc/cc1.png");
        assertEquals(138, png.getWidth());
        assertEquals(180, png.getHeight());
    }

    public void testErrors()
    throws Exception
    {
        errorHelper("/images/suite/x00n0g01.png");
        errorHelper("/images/suite/xcrn0g04.png");
        errorHelper("/images/suite/xlfn0g04.png");
    }

    public void errorHelper(String path)
    throws Exception
    {
        try {
            readResource(path);
            fail("Expected exception");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private PngImage readResource(String path)
    throws IOException
    {
        PngImage png = new PngImage();
        InputStream in = getClass().getResourceAsStream(path);
        png.read(in, true);
        return png;
    }

    public SimpleTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(SimpleTest.class);
    }
}
