package com.sixlegs.png.iio;

import javax.imageio.metadata.IIOMetadataFormatImpl;
import junit.framework.Test;

public class TestStandardMetadata
extends MetadataTestCase
{
	public static Test suite()
    throws Exception
    {
        return createSuite(TestStandardMetadata.class);
    }

	public TestStandardMetadata(String name) 
	{
		super(name);
	}

    protected String getFormatName()
    {
        return IIOMetadataFormatImpl.standardMetadataFormatName;
    }
}
