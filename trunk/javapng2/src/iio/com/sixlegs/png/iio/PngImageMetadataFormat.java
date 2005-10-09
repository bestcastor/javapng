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

	// ---------------------------------------- XML Node and attribute names

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
	// represents tEXt, zTXt and iTXt
	static final String n_TEXT= "TEXT";
	static final String n_TEXT_keyword 	= "keyword";
	static final String n_TEXT_text 	= "text";

	// pHYs chunk attribute names
	static final String n_pHYs = "pHYs";
	static final String n_pHYs_ppux = "pixelsPerUnitX";
	static final String n_pHYs_ppuy = "pixelsPerUnitY";
	static final String n_pHYs_unit = "unitsSpecifier";

	// time modified attribute names
	static final String n_tIME = "tIME";
	static final String n_tIME_year 	= "year";
	static final String n_tIME_month 	= "month";
	static final String n_tIME_day 		= "day";
	static final String n_tIME_hour	 	= "hour";
	static final String n_tIME_minute 	= "minute";
	static final String n_tIME_second 	= "second";

	// PLTE chunk attribute names
	static final String n_PLTE = "PLTE";
	static final String n_PLTE_sample = "entry";
	static final String n_PLTE_sample_num = "index";
	static final String n_PLTE_sample_r = "red";
	static final String n_PLTE_sample_g = "green";
	static final String n_PLTE_sample_b = "blue";

	// gAMA chunk attribute names
	static final String n_gAMA = "gAMA";
	static final String n_gAMA_val= "value";

	// tRNS chunk attribute names
	static final String n_tRNS = "tRNS";
	static final String n_tRNS_gs = "greyScaleTrans";
	static final String n_tRNS_r = "redTrans";
	static final String n_tRNS_g = "greenTrans";
	static final String n_tRNS_b = "blueTrans";
	static final String n_tRNS_plt	= "palleteTrans";
	static final String n_tRNS_plt_num = "index";
	static final String n_tRNS_plt_val = "value";

	// bKGD
	static final String n_bKGD = "bKGD";
	static final String n_bKGD_plt = "paletteIndex";
	static final String n_bKGD_grey = "grey";
	static final String n_bKGD_r = "red";
	static final String n_bKGD_g = "green";
	static final String n_bKGD_b = "blue";

	// sRGB
	static final String n_sRGB = "sRGB";
	static final String n_sRGB_val = "value";

	// sBIT
	static final String n_sBIT = "sBIT";
	static final String n_sBIT_gray = "gray";
	static final String n_sBIT_r = "red";
	static final String n_sBIT_g = "green";
	static final String n_sBIT_b = "blue";
	static final String n_sBIT_a = "alpha";

	// cHRM
	static final String n_cHRM = "cHRM";
	static final String n_cHRM_wx = "whitePointX";
	static final String n_cHRM_wy = "whitePointY";
	static final String n_cHRM_rx = "redX";
	static final String n_cHRM_ry = "redY";
	static final String n_cHRM_gx = "greenX";
	static final String n_cHRM_gy = "greenY";
	static final String n_cHRM_bx = "blueX";
	static final String n_cHRM_by = "blueY";

	// iCCP
	// TODO the iCCP chunks is not tested.
	static final String n_iCCP = "iCCP";
	static final String n_iCCP_name = "name";
	static final String n_iCCP_prof = "profile";

	// hIST
	static final String n_hIST = "hIST";
	static final String n_hIST_name= "entry";
	static final String n_hIST_idx = "index";
	static final String n_hIST_val = "value";

	// sPLT
	// TODO the sPLT chunk is not tested as no png image was found with a sPLT
	// chunk.
	// The reference test images contain spAL chunks.
	static final String n_sPLT = "sPLT";
	static final String n_sPLT_name = "paletteName";
	static final String n_sPLT_depth= "depth";
	static final String n_sPLT_node = "entry";
	static final String n_sPLT_r = "red";
	static final String n_sPLT_g = "green";
	static final String n_sPLT_b = "blue";
	static final String n_sPLT_f = "frequency";


	// singleton
	private static PngImageMetadataFormat defaultInstance =
		new PngImageMetadataFormat();

	// Make constructor private to enforce the singleton pattern
	private PngImageMetadataFormat() 
	{
		// Set the name of the root node
		super(rootName, CHILD_POLICY_REPEAT);

		setup_IHDR();
		setup_PLTE();
		setup_tRNS();
		setup_gAMA();
		setup_pHYs();
		setup_tIME();
		setup_TextChunks();
		setup_bKGD();
		setup_sRGB();
		setup_sBIT();
		setup_cHRM();
		setup_iCCP();
		setup_hIST();
		setup_sPLT();
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

	private void setup_sPLT()
	{
		addElement(n_sPLT, rootName, CHILD_POLICY_REPEAT);

		addAttribute(n_sPLT, n_sPLT_name, DATATYPE_STRING, true, null);
		addAttribute(n_sPLT, n_sPLT_depth, DATATYPE_INTEGER, true, null);
		
		addElement(n_sPLT_node, n_sPLT, CHILD_POLICY_EMPTY);

		addAttribute(n_sPLT_node, n_sPLT_r, DATATYPE_INTEGER, true, null);
		addAttribute(n_sPLT_node, n_sPLT_g, DATATYPE_INTEGER, true, null);
		addAttribute(n_sPLT_node, n_sPLT_b, DATATYPE_INTEGER, true, null);
		addAttribute(n_sPLT_node, n_sPLT_f, DATATYPE_INTEGER, true, null);
	}

	private void setup_hIST()
	{
		addElement(n_hIST, rootName, CHILD_POLICY_REPEAT);
		addElement(n_hIST_name, n_hIST, CHILD_POLICY_EMPTY);

		addAttribute(n_hIST_name, n_hIST_idx, DATATYPE_INTEGER, true, null);
		addAttribute(n_hIST_name, n_hIST_val, DATATYPE_INTEGER, true, null);
	}

	private void setup_iCCP()
	{
		addElement(n_iCCP, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_iCCP, n_iCCP_name, DATATYPE_STRING, true, null);
		addAttribute(n_iCCP, n_iCCP_prof, DATATYPE_STRING, true, null);
	}

	private void setup_cHRM()
	{
		addElement(n_cHRM, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_cHRM, n_cHRM_wx, DATATYPE_FLOAT, true, null);
		addAttribute(n_cHRM, n_cHRM_wy, DATATYPE_FLOAT, true, null);
		addAttribute(n_cHRM, n_cHRM_rx, DATATYPE_FLOAT, true, null);
		addAttribute(n_cHRM, n_cHRM_ry, DATATYPE_FLOAT, true, null);
		addAttribute(n_cHRM, n_cHRM_gx, DATATYPE_FLOAT, true, null);
		addAttribute(n_cHRM, n_cHRM_gy, DATATYPE_FLOAT, true, null);
		addAttribute(n_cHRM, n_cHRM_bx, DATATYPE_FLOAT, true, null);
		addAttribute(n_cHRM, n_cHRM_by, DATATYPE_FLOAT, true, null);
	}

	private void setup_sBIT()
	{
		addElement(n_sBIT, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_sBIT, n_sBIT_gray, DATATYPE_INTEGER, false, null);
		addAttribute(n_sBIT, n_sBIT_r, DATATYPE_INTEGER, false, null);
		addAttribute(n_sBIT, n_sBIT_g, DATATYPE_INTEGER, false, null);
		addAttribute(n_sBIT, n_sBIT_b, DATATYPE_INTEGER, false, null);
		addAttribute(n_sBIT, n_sBIT_a, DATATYPE_INTEGER, false, null);
	}

	private void setup_PLTE()
	{
		addElement(n_PLTE, rootName, CHILD_POLICY_REPEAT);
		addElement(n_PLTE_sample, n_PLTE, CHILD_POLICY_EMPTY);

		addAttribute(n_PLTE_sample, n_PLTE_sample_num, DATATYPE_INTEGER, true, null);
		addAttribute(n_PLTE_sample, n_PLTE_sample_r, DATATYPE_INTEGER, true, null);
		addAttribute(n_PLTE_sample, n_PLTE_sample_g, DATATYPE_INTEGER, true, null);
		addAttribute(n_PLTE_sample, n_PLTE_sample_b, DATATYPE_INTEGER, true, null);
	}

	private void setup_tRNS()
	{
		addElement(n_tRNS, rootName, CHILD_POLICY_REPEAT);
		
		addAttribute(n_tRNS, n_tRNS_gs, DATATYPE_INTEGER, false, null);
		addAttribute(n_tRNS, n_tRNS_r, DATATYPE_INTEGER, false, null);
		addAttribute(n_tRNS, n_tRNS_g, DATATYPE_INTEGER, false, null);
		addAttribute(n_tRNS, n_tRNS_b, DATATYPE_INTEGER, false, null);

		addElement(n_tRNS, n_tRNS_plt, CHILD_POLICY_EMPTY);

		addAttribute(n_tRNS_plt, n_tRNS_plt_num, DATATYPE_INTEGER, true, null);
		addAttribute(n_tRNS_plt, n_tRNS_plt_val, DATATYPE_INTEGER, true, null);
	}

	private void setup_bKGD()
	{
		addElement(n_bKGD, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_bKGD, n_bKGD_plt, DATATYPE_INTEGER, false, null);
		addAttribute(n_bKGD, n_bKGD_grey, DATATYPE_INTEGER, false, null);
		addAttribute(n_bKGD, n_bKGD_r, DATATYPE_INTEGER, false, null);
		addAttribute(n_bKGD, n_bKGD_g, DATATYPE_INTEGER, false, null);
		addAttribute(n_bKGD, n_bKGD_b, DATATYPE_INTEGER, false, null);
	}

	private void setup_sRGB()
	{
		addElement(n_sRGB, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_sRGB, n_sRGB_val, DATATYPE_INTEGER, true, null);
	}

	private void setup_gAMA()
	{
		addElement(n_gAMA, rootName, CHILD_POLICY_EMPTY);

		addAttribute(n_gAMA, n_gAMA_val, DATATYPE_FLOAT, true, null);
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
	public boolean canNodeAppear(String e, ImageTypeSpecifier imageType) 
	{
		if(		   e.equals(n_IHDR)
				|| e.equals(n_TEXT)
				|| e.equals(n_pHYs)
				|| e.equals(n_tIME)
				|| e.equals(n_PLTE)
				|| e.equals(n_PLTE_sample)
				|| e.equals(n_gAMA)
				|| e.equals(n_tRNS)
				|| e.equals(n_tRNS_plt)
				|| e.equals(n_bKGD)
				|| e.equals(n_sRGB)
				|| e.equals(n_sBIT)
				|| e.equals(n_cHRM)
				|| e.equals(n_iCCP)
				|| e.equals(n_hIST)
				|| e.equals(n_hIST_name)
				|| e.equals(n_sPLT)
				|| e.equals(n_sPLT_node)
				)
			return true;
		return false;
	}

	// Return the singleton instance
	public static PngImageMetadataFormat getDefaultInstance() 
	{
		return defaultInstance;
	}
}
