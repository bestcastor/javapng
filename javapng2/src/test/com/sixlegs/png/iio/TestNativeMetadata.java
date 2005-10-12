package com.sixlegs.png.iio;

import junit.framework.Test;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.*;

// Tests the native metadata format javax_imageio_png_1.0
// by comparing it to Sun's implementation
public class TestNativeMetadata
extends MetadataTestCase
{
	public static Test suite()
    throws Exception
    {
        return createSuite(TestNativeMetadata.class);
    }

	public TestNativeMetadata(String name) 
	{
		super(name);
	}

    protected String getFormatName()
    {
        return "javax_imageio_png_1.0";
    }

	protected Node getSunsTree()
	throws Exception
	{
		// Redirecting stderr to stdout
		java.io.PrintStream stderr = System.err;
		System.setErr(System.out);

		Node n = null;
		try {
			n = sunIR.getImageMetadata(0).getAsTree("javax_imageio_png_1.0");
		}
		catch (Throwable e)
		{ }

		// re-enabling stderr
		System.setErr(stderr);
		return n;
	}
}
