package com.sixlegs.png.iio;

import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadata;
import junit.framework.Test;
import org.w3c.dom.*;

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

	protected Node getSunsTree()
	throws Exception
	{
		return sunIR.getImageMetadata(0).getAsTree("javax_imageio_1.0");
	}
}
