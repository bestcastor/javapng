package com.sixlegs.png;

import java.io.*;
import junit.framework.*;

public class SimpleTest
extends TestCase
{
    public void testRead()
    throws Exception
    {
        PngImage png = new PngImage();
        InputStream in = getClass().getResourceAsStream("/images/misc/cc1.png");
        png.read(in, true);
        assertEquals(138, png.getWidth());
        assertEquals(180, png.getHeight());
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
