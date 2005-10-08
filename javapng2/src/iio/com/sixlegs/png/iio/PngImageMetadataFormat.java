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

	// IHDR attribute names
	static final String n_IHDR	= "IHDR";
	static final String n_IHDR_width 			= "width";
	static final String n_IHDR_height 			= "height";
	static final String n_IHDR_bitDepth 		= "bit_depth";
	static final String n_IHDR_colorType 		= "colorType";
	static final String n_IHDR_compressionMethod= "compressionMethod";
	static final String n_IHDR_filterMethod 	= "filterMethod";
	static final String n_IHDR_interlaceMethod 	= "interlaceMethod";

	// text chunk attribute names
	//FIXME: Should the different text chunks be differentiated?
	static final String n_TEXT= "TEXT";
	static final String n_TEXT_keyword 	= "keyword";
	static final String n_TEXT_text 	= "text";

	// pHYs chunk attribute names
	static final String n_pHYs = "pHYs";
	static final String n_pHYs_ppux = "pixelsPerUnitX";
	static final String n_pHYs_ppuy = "pixelsPerUnitY";
	static final String n_pHYs_unit = "unitsSpecifier";

	// time modified attributes
	static final String n_tIME = "tIME";
	static final String n_tIME_year 	= "year";
	static final String n_tIME_month 	= "month";
	static final String n_tIME_day 		= "day";
	static final String n_tIME_hour	 	= "hour";
	static final String n_tIME_minute 	= "minute";
	static final String n_tIME_second 	= "second";

	//TODO more chunks

	// singleton
	private static PngImageMetadataFormat defaultInstance =
		new PngImageMetadataFormat();

	// Make constructor private to enforce the singleton pattern
	private PngImageMetadataFormat() 
	{
		// Set the name of the root node
		super(rootName, CHILD_POLICY_REPEAT);

		setup_IHDR();
		setup_TextChunks();
		setup_pHYs();
		setup_tIME();
	}

	private void setup_IHDR()
	{
		addElement(n_IHDR, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_IHDR, n_IHDR_width, DATATYPE_INTEGER, true, null);
		addAttribute(n_IHDR, n_IHDR_height, DATATYPE_INTEGER, true, null);
		addAttribute(n_IHDR, n_IHDR_bitDepth, DATATYPE_INTEGER, true, null);
		addAttribute(n_IHDR, n_IHDR_colorType, DATATYPE_INTEGER, true, null);
		addAttribute(n_IHDR, n_IHDR_compressionMethod, DATATYPE_INTEGER, true, null);
		addAttribute(n_IHDR, n_IHDR_filterMethod, DATATYPE_INTEGER, true, null);
		addAttribute(n_IHDR, n_IHDR_interlaceMethod, DATATYPE_INTEGER, true, null);
	}

	private void setup_TextChunks()
	{
		addElement(n_TEXT, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_TEXT, n_TEXT_keyword, DATATYPE_STRING, true, null);
		addAttribute(n_TEXT, n_TEXT_keyword, DATATYPE_STRING, true, null);
	}

	private void setup_pHYs()
	{
		addElement(n_pHYs, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_pHYs, n_pHYs_ppux, DATATYPE_INTEGER, true, null);
		addAttribute(n_pHYs, n_pHYs_ppuy, DATATYPE_INTEGER, true, null);
		addAttribute(n_pHYs, n_pHYs_unit, DATATYPE_INTEGER, true, null);
	}

	private void setup_tIME()
	{
		addElement(n_tIME, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_tIME, n_tIME_year  , DATATYPE_INTEGER, true, null);
		addAttribute(n_tIME, n_tIME_month , DATATYPE_INTEGER, true, null);
		addAttribute(n_tIME, n_tIME_day   , DATATYPE_INTEGER, true, null);
		addAttribute(n_tIME, n_tIME_hour  , DATATYPE_INTEGER, true, null);
		addAttribute(n_tIME, n_tIME_minute, DATATYPE_INTEGER, true, null);
		addAttribute(n_tIME, n_tIME_second, DATATYPE_INTEGER, true, null);
	}

	// Check for legal element names
	public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) 
	{
		if(elementName.equals(n_IHDR)
				|| elementName.equals(n_TEXT)
				|| elementName.equals(n_pHYs)
				|| elementName.equals(n_tIME))
			return true;
		return false;
	}

	// Return the singleton instance
	public static PngImageMetadataFormat getDefaultInstance() 
	{
		return defaultInstance;
	}
}
