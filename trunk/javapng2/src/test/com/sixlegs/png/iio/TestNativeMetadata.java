package com.sixlegs.png.iio;

import junit.framework.Test;

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
        return "com.sixlegs.png.iio.PngImageMetadata_v1";
    }
}
