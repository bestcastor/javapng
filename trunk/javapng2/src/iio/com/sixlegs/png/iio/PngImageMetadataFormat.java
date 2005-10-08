package com.sixlegs.png.iio;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class PngImageMetadataFormat 
extends IIOMetadataFormatImpl 
{
	private static final String rootName = "com.sixlegs.png.iio.PngImageMetadata_v1";

	// -------------------------------------------------- Node names
	// FIXME Is there a convention for attribute and node names?

	// root attributes
	static final String root_width 				= "width";
	static final String root_height 			= "height";
	static final String root_bitDepth 			= "bit_depth";
	static final String root_colorType 			= "color_type";
	static final String root_compressionMethod 	= "compression_method";
	static final String root_filterMethod 		= "filter_method";
	static final String root_interlaceMethod 	= "interlace_method";

	// text attributes
	static final String textData = "TextualInformation";
	static final String textData_keyValue 		= "KeywordValuePair";
	static final String textData_keyValue_key 	= "key";
	static final String textData_keyValue_val 	= "value";

	// physical dimension attributes
	static final String physDimData = "PhysicalDimensions";
	static final String physDimData_ppux = "pixels_per_unit_X";
	static final String physDimData_ppuy = "pixels_per_unit_Y";
	static final String physDimData_unit = "units_specifier";

	// time modified attributes
	static final String timeData = "ImageLastModificationTime";
	static final String timeData_year 		= "year";
	static final String timeData_month	 	= "month";
	static final String timeData_day 		= "day";
	static final String timeData_hour	 	= "hour";
	static final String timeData_minute 	= "minute";
	static final String timeData_second 	= "second";

	// singleton
	private static PngImageMetadataFormat defaultInstance =
		new PngImageMetadataFormat();

	// Make constructor private to enforce the singleton pattern
	private PngImageMetadataFormat() 
	{
		// Set the name of the root node
		super(rootName, CHILD_POLICY_REPEAT);

		setupRootNode();
		setupTextualInfo();
		setupPhysicalPixelDim();
		setupImageLastModTime();
	}

	private void setupRootNode()
	{
		addAttribute(rootName, root_width, DATATYPE_INTEGER, true, null);
		addAttribute(rootName, root_height, DATATYPE_INTEGER, true, null);
		addAttribute(rootName, root_bitDepth, DATATYPE_INTEGER, true, null);
		addAttribute(rootName, root_colorType, DATATYPE_INTEGER, true, null);
		addAttribute(rootName, root_compressionMethod, DATATYPE_INTEGER, true, null);
		addAttribute(rootName, root_filterMethod, DATATYPE_INTEGER, true, null);
		addAttribute(rootName, root_interlaceMethod, DATATYPE_INTEGER, true, null);
	}

	private void setupTextualInfo()
	{
		addElement(textData, rootName, CHILD_POLICY_REPEAT);
		addElement(textData_keyValue, textData, CHILD_POLICY_EMPTY);
		addAttribute(textData_keyValue, textData_keyValue_key, DATATYPE_STRING, true, null);
		addAttribute(textData_keyValue, textData_keyValue_val, DATATYPE_STRING, true, null);
	}

	private void setupPhysicalPixelDim()
	{
		addElement(physDimData, rootName, CHILD_POLICY_EMPTY);
		addAttribute(physDimData, physDimData_ppux, DATATYPE_INTEGER, true, null);
		addAttribute(physDimData, physDimData_ppuy, DATATYPE_INTEGER, true, null);
		addAttribute(physDimData, physDimData_unit, DATATYPE_INTEGER, true, null);
	}

	private void setupImageLastModTime()
	{
		addElement(timeData, rootName, CHILD_POLICY_EMPTY);
		addAttribute(timeData, timeData_year  , DATATYPE_INTEGER, true, null);
		addAttribute(timeData, timeData_month , DATATYPE_INTEGER, true, null);
		addAttribute(timeData, timeData_day   , DATATYPE_INTEGER, true, null);
		addAttribute(timeData, timeData_hour  , DATATYPE_INTEGER, true, null);
		addAttribute(timeData, timeData_minute, DATATYPE_INTEGER, true, null);
		addAttribute(timeData, timeData_second, DATATYPE_INTEGER, true, null);
	}

	// Check for legal element names
	public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) 
	{
		if(elementName.equals(timeData)
				|| elementName.equals(textData)
				|| elementName.equals(textData_keyValue)
				|| elementName.equals(physDimData))
			return true;
		return false;
	}

	// Return the singleton instance
	public static PngImageMetadataFormat getDefaultInstance() 
	{
		return defaultInstance;
	}
}
