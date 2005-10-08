/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 2005 Dimitri Koussa

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version.
*/

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
