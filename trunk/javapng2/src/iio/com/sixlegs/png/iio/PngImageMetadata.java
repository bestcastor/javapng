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

import com.sixlegs.png.PngImage;
import com.sixlegs.png.PngConstants;
import com.sixlegs.png.TextChunk;
import com.sixlegs.png.SuggestedPalette;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataNode;

public class PngImageMetadata 
extends IIOMetadata 
{
	private static final String nativeMetadataFormatName = 
		"com.sixlegs.png.iio.PngImageMetadata_v1";

	// IHDR chunk attributes
	private class IHDR
	{
		int width;
		int height;
		int bitDepth;
		int colorType;
		int compressionMethod;
		int filterMethod;
		int interlaceMethod;
	}

	// gAMA chunk attribute
	private class gAMA
	{
		float value;
	}

	// tEXt, zTXt and iTXt attributes
	private class TEXT
	{
		String keyword;
		String text;
	}

	// tRNS chunk attributes
	private class tRNS
	{
		int [] trans;
		byte [] plt;
	}

	// pHYs attributes
	private class pHYs
	{
		int pixelsPerUnitX;
		int pixelsPerUnitY;
		int unitsSpecifier;
	}

	//tIME attributes
	private class tIME
	{
		int year;
		int month;
		int day;
		int hour;
		int minute;
		int second;
	}

	// class to hold the metadata before it is created into an XML document
	private class Metadata
	{
		// metadata is stored here
		IHDR _IHDR = null;
		pHYs _pHYs = null;
		tIME _tIME = null;
		gAMA _gAMA = null;
		tRNS _tRNS = null;
		int[] _bKGD = null;
		int[] _sRGB = null;
		int[] _sBIT = null;
		int[] _hIST = null;
		byte[] _PLTE = null;
		float[] _cHRM = null;
		String[] _iCCP = null;
		List _TEXT = null;
		List _sPLT = null;

		// Sets up the metadata
		public Metadata(PngImage png)
		{
			read_IHDR(png);

			read_PLTE(png);
			read_gAMA(png);
			read_TEXT(png);
			read_pHYs(png);
			read_tIME(png);
			read_tRNS(png);
			read_bKGD(png);
			read_sRGB(png);
			read_sBIT(png);
			read_cHRM(png);
			read_iCCP(png);
			read_hIST(png);
			read_sPLT(png);
		}

		private void read_IHDR(PngImage png)
		{
			_IHDR = new IHDR();
			_IHDR.width = png.getWidth();
			_IHDR.height = png.getHeight();
			_IHDR.bitDepth = png.getBitDepth();
			_IHDR.colorType = png.getColorType();
			_IHDR.compressionMethod = ((Integer)
					png.getProperty(PngConstants.COMPRESSION)).intValue();
			_IHDR.filterMethod = ((Integer)
					png.getProperty(PngConstants.FILTER)).intValue();
			_IHDR.interlaceMethod = png.getInterlace();
		}

		private void read_sPLT(PngImage png)
		{
			Object splt = png.getProperty(PngConstants.SUGGESTED_PALETTES);
			if (splt == null)
				return;

			_sPLT = (List) splt;
		}

		private void read_hIST(PngImage png)
		{
			Object hist = png.getProperty(PngConstants.HISTOGRAM);
			if (hist == null)
				return;

			_hIST = (int[]) hist;
		}

		private void read_iCCP(PngImage png)
		{
			Object prof = png.getProperty(PngConstants.ICC_PROFILE);
			if (prof == null)
				return;

			//FIXME what is an iCCP profile? text? assuming it is text here.
			_iCCP = new String [] {
				new String((byte[]) prof),
				(String) png.getProperty(PngConstants.ICC_PROFILE_NAME)
			};
		}

		private void read_cHRM(PngImage png)
		{
			Object floatArray = png.getProperty(PngConstants.CHROMATICITY);
			if (floatArray == null)
				return;

			_cHRM = (float []) floatArray;
		}

		private void read_sBIT(PngImage png)
		{
			Object sBits = png.getProperty(PngConstants.SIGNIFICANT_BITS);
			if (sBits == null)
				return;

			if (sBits instanceof byte[])
			{
				byte[] bytes = (byte[]) sBits;
				_sBIT = new int[bytes.length];
				for (int i=0; i<bytes.length; i++)
				{
					_sBIT[i] = (int) bytes[i];
				}
			}
			else
				_sBIT = (int []) sBits;
		}

		private void read_sRGB(PngImage png)
		{
			Object ri = png.getProperty(PngConstants.RENDERING_INTENT);
			if (ri == null)
				return;

			_sRGB = new int[] {((Integer) ri).intValue()};
		}

		private void read_bKGD(PngImage png)
		{
			Object intArrayObj = png.getProperty(PngConstants.BACKGROUND);
			if (intArrayObj == null)
				return;

			_bKGD = (int []) intArrayObj;
		}

		private void read_PLTE(PngImage png)
		{
			Object palette = png.getProperty(PngConstants.PALETTE);
			if (palette == null)
				return;

			_PLTE = (byte []) palette;
		}

		private void read_tRNS(PngImage png)
		{
			switch (_IHDR.colorType)
			{
				case PngConstants.COLOR_TYPE_RGB:
				case PngConstants.COLOR_TYPE_GRAY:
					Object trans = png.getProperty(PngConstants.TRANSPARENCY);
					if (trans == null)
						return;
					_tRNS = new tRNS();
					_tRNS.trans = (int []) trans;
					break;

				case PngConstants.COLOR_TYPE_PALETTE:
					Object pltObj = png.getProperty(PngConstants.PALETTE_ALPHA);
					if (pltObj == null)
						return;
					_tRNS = new tRNS();
					_tRNS.plt = (byte []) pltObj;
					break;
			}
		}

		private void read_TEXT(PngImage png)
		{
			List text_chunks = (List)
				png.getProperty(PngConstants.TEXT_CHUNKS);

			if (text_chunks == null)
				return;

			_TEXT = new ArrayList();

			Iterator itr = text_chunks.iterator();

			while (itr.hasNext())
			{
				TextChunk t = (TextChunk) itr.next();

				TEXT txt = new TEXT();
				txt.keyword = t.getKeyword();
				txt.text = t.getText();

				_TEXT.add(txt);
			}
		}

		private void read_gAMA(PngImage png)
		{
			// Make sure a gAMA chunk was read
			Object gamma = png.getProperty(PngConstants.GAMMA);
			if (gamma == null)
				return;

			_gAMA = new gAMA();
			_gAMA.value = ((Float) gamma).floatValue();
		}

		private void read_pHYs(PngImage png)
		{
			// Make sure a pHYs chunk was read
			if (png.getProperty(PngConstants.PIXELS_PER_UNIT_X) == null)
				return;

			_pHYs = new pHYs();

			_pHYs.pixelsPerUnitX = ((Integer)
					png.getProperty(PngConstants.PIXELS_PER_UNIT_X)).intValue();
			_pHYs.pixelsPerUnitY = ((Integer)
					png.getProperty(PngConstants.PIXELS_PER_UNIT_Y)).intValue();
			_pHYs.unitsSpecifier = ((Integer)
					png.getProperty(PngConstants.UNIT)).intValue();
		}

		// Have to do this because there is no way (that i know of) to store a
		// date in an XML Node
		private void read_tIME(PngImage png)
		{
			Date date = (Date) png.getProperty(PngConstants.TIME);

			if (date == null)
				return;

			_tIME = new tIME();

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			_tIME.year 		= cal.get(Calendar.YEAR);
			_tIME.month 	= cal.get(Calendar.MONTH);
			_tIME.day 		= cal.get(Calendar.DAY_OF_MONTH);
			_tIME.hour 		= cal.get(Calendar.HOUR_OF_DAY);
			_tIME.minute 	= cal.get(Calendar.MINUTE);
			_tIME.second 	= cal.get(Calendar.SECOND);
		}
	}

	private Metadata metadata;

	public PngImageMetadata(PngImage png) 
	{
		super(false, nativeMetadataFormatName,
				"com.sixlegs.png.iio.PngImageMetadata", null, null);
		metadata = new Metadata(png);
	}

	private void checkFormatName(String formatName)
	{
		if (!formatName.equals(nativeMetadataFormatName))
			throw new IllegalArgumentException(
					"Unsuported format: " + formatName);
	}

	public IIOMetadataFormat getMetadataFormat(String formatName) 
	{
		checkFormatName(formatName);
		return PngImageMetadataFormat.getDefaultInstance();
	}

	public Node getAsTree(String formatName) 
	{
		checkFormatName(formatName);

		IIOMetadataNode root =
			new IIOMetadataNode(nativeMetadataFormatName);

		add_IHDR(root);
		add_PLTE(root);
		add_gAMA(root);
		add_TEXT(root);
		add_pHYs(root);
		add_tIME(root);
		add_tRNS(root);
		add_bKGD(root);
		add_sRGB(root);
		add_sBIT(root);
		add_cHRM(root);
		add_iCCP(root);
		add_hIST(root);
		add_sPLT(root);

		return root;
	}

	private void add_IHDR(IIOMetadataNode root)
	{
		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_IHDR);

		node.setAttribute(PngImageMetadataFormat.n_IHDR_width,
				Integer.toString(metadata._IHDR.width));
		node.setAttribute(PngImageMetadataFormat.n_IHDR_height,
				Integer.toString(metadata._IHDR.height));
		node.setAttribute(PngImageMetadataFormat.n_IHDR_bitDepth,
				Integer.toString(metadata._IHDR.bitDepth));
		node.setAttribute(PngImageMetadataFormat.n_IHDR_colorType,
				Integer.toString(metadata._IHDR.colorType));
		node.setAttribute(PngImageMetadataFormat.n_IHDR_compressionMethod,
				Integer.toString(metadata._IHDR.compressionMethod));
		node.setAttribute(PngImageMetadataFormat.n_IHDR_filterMethod,
				Integer.toString(metadata._IHDR.filterMethod));
		node.setAttribute(PngImageMetadataFormat.n_IHDR_interlaceMethod,
				Integer.toString(metadata._IHDR.interlaceMethod));

		root.appendChild(node);
	}

	private void add_sPLT(IIOMetadataNode root)
	{
		if (metadata._sPLT == null)
			return;

		Iterator itr = metadata._sPLT.iterator();
		while (itr.hasNext())
		{
			SuggestedPalette s = (SuggestedPalette) itr.next();

			IIOMetadataNode splt_node = new
				IIOMetadataNode(PngImageMetadataFormat.n_sPLT);

			splt_node.setAttribute(PngImageMetadataFormat.n_sPLT_name,
					s.getName());
			splt_node.setAttribute(PngImageMetadataFormat.n_sPLT_depth,
					Integer.toString(s.getSampleDepth()));

			int count = s.getSampleCount();
			for (int i=0; i<count; i++)
			{
				short[] pixels = new short[4];
				s.getSample(i, pixels);

				IIOMetadataNode node = new
					IIOMetadataNode(PngImageMetadataFormat.n_sPLT_node);

				node.setAttribute(PngImageMetadataFormat.n_sPLT_r,
						Integer.toString(pixels[0]));
				node.setAttribute(PngImageMetadataFormat.n_sPLT_g,
						Integer.toString(pixels[1]));
				node.setAttribute(PngImageMetadataFormat.n_sPLT_b,
						Integer.toString(pixels[2]));
				node.setAttribute(PngImageMetadataFormat.n_sPLT_f,
						Integer.toString(pixels[3]));

				splt_node.appendChild(node);
			} //for

			root.appendChild(splt_node);
		} //while
	}

	private void add_hIST(IIOMetadataNode root)
	{
		if (metadata._hIST == null)
			return;

		IIOMetadataNode main_node = new
			IIOMetadataNode(PngImageMetadataFormat.n_hIST);

		for (int i=0; i<metadata._hIST.length; i++)
		{
			IIOMetadataNode node = new
				IIOMetadataNode(PngImageMetadataFormat.n_hIST_name);

			node.setAttribute(PngImageMetadataFormat.n_hIST_idx,
					Integer.toString(i));
			node.setAttribute(PngImageMetadataFormat.n_hIST_val,
					Integer.toString(metadata._hIST[i]));

			main_node.appendChild(node);
		}

		root.appendChild(main_node);
	}

	private void add_iCCP(IIOMetadataNode root)
	{
		if (metadata._iCCP == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_iCCP);

		node.setAttribute(PngImageMetadataFormat.n_iCCP_name,
				metadata._iCCP[0]);
		node.setAttribute(PngImageMetadataFormat.n_iCCP_prof,
				metadata._iCCP[1]);

		root.appendChild(node);
	}

	private void add_cHRM(IIOMetadataNode root)
	{
		if (metadata._cHRM == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_cHRM);

		node.setAttribute(PngImageMetadataFormat.n_cHRM_wx,
				Float.toString(metadata._cHRM[0]));
		node.setAttribute(PngImageMetadataFormat.n_cHRM_wy,
				Float.toString(metadata._cHRM[1]));
		node.setAttribute(PngImageMetadataFormat.n_cHRM_rx,
				Float.toString(metadata._cHRM[2]));
		node.setAttribute(PngImageMetadataFormat.n_cHRM_ry,
				Float.toString(metadata._cHRM[3]));
		node.setAttribute(PngImageMetadataFormat.n_cHRM_gx,
				Float.toString(metadata._cHRM[4]));
		node.setAttribute(PngImageMetadataFormat.n_cHRM_gy,
				Float.toString(metadata._cHRM[5]));
		node.setAttribute(PngImageMetadataFormat.n_cHRM_bx,
				Float.toString(metadata._cHRM[6]));
		node.setAttribute(PngImageMetadataFormat.n_cHRM_by,
				Float.toString(metadata._cHRM[7]));

		root.appendChild(node);
	}

	private void add_sBIT(IIOMetadataNode root)
	{
		if (metadata._sBIT == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_sBIT);

		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
				node.setAttribute(PngImageMetadataFormat.n_sBIT_gray,
					Integer.toString( metadata._sBIT[0]));
				break;

			case PngConstants.COLOR_TYPE_RGB:
				node.setAttribute(PngImageMetadataFormat.n_sBIT_r,
					Integer.toString(metadata._sBIT[0]));
				node.setAttribute(PngImageMetadataFormat.n_sBIT_g,
					Integer.toString(metadata._sBIT[1]));
				node.setAttribute(PngImageMetadataFormat.n_sBIT_b,
					Integer.toString(metadata._sBIT[2]));
				break;

			case PngConstants.COLOR_TYPE_PALETTE:
				node.setAttribute(PngImageMetadataFormat.n_sBIT_r,
					Integer.toString(metadata._sBIT[0]));
				node.setAttribute(PngImageMetadataFormat.n_sBIT_g,
					Integer.toString(metadata._sBIT[1]));
				node.setAttribute(PngImageMetadataFormat.n_sBIT_b,
					Integer.toString(metadata._sBIT[2]));
				break;

			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				node.setAttribute(PngImageMetadataFormat.n_sBIT_gray,
					Integer.toString(metadata._sBIT[0]));
				node.setAttribute(PngImageMetadataFormat.n_sBIT_a,
					Integer.toString(metadata._sBIT[1]));
				break;
				
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				node.setAttribute(PngImageMetadataFormat.n_sBIT_r,
					Integer.toString(metadata._sBIT[0]));
				node.setAttribute(PngImageMetadataFormat.n_sBIT_g,
					Integer.toString(metadata._sBIT[1]));
				node.setAttribute(PngImageMetadataFormat.n_sBIT_b,
					Integer.toString(metadata._sBIT[2]));
				node.setAttribute(PngImageMetadataFormat.n_sBIT_a,
					Integer.toString(metadata._sBIT[3]));
				break;
		}

		root.appendChild(node);
	}

	private void add_sRGB(IIOMetadataNode root)
	{
		if (metadata._sRGB == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_sRGB);

		node.setAttribute(PngImageMetadataFormat.n_sRGB_val,
				Integer.toString(metadata._sRGB[0]));

		root.appendChild(node);
	}

	private void add_PLTE(IIOMetadataNode root)
	{
		if (metadata._PLTE == null)
			return;

		IIOMetadataNode plt_node = new
			IIOMetadataNode(PngImageMetadataFormat.n_PLTE);

		byte [] plt = metadata._PLTE;
		for (int i=0; i<plt.length; i+=3)
		{
			IIOMetadataNode node = new
				IIOMetadataNode(PngImageMetadataFormat.n_PLTE_sample);

			node.setAttribute(PngImageMetadataFormat.n_PLTE_sample_num, 
					Integer.toString((int) (i/3)));
			node.setAttribute(PngImageMetadataFormat.n_PLTE_sample_r, 
					Integer.toString(0xFF & plt[i]));
			node.setAttribute(PngImageMetadataFormat.n_PLTE_sample_g, 
					Integer.toString(0xFF & plt[i+1]));
			node.setAttribute(PngImageMetadataFormat.n_PLTE_sample_b, 
					Integer.toString(0xFF & plt[i+2]));

			plt_node.appendChild(node);
		}

		root.appendChild(plt_node);
	}

	private void add_bKGD(IIOMetadataNode root)
	{
		if (metadata._bKGD == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_bKGD);

		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				node.setAttribute(PngImageMetadataFormat.n_bKGD_grey,
						Integer.toString(metadata._bKGD[0]));
				break;

			case PngConstants.COLOR_TYPE_RGB:
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				node.setAttribute(PngImageMetadataFormat.n_bKGD_r,
						Integer.toString(metadata._bKGD[0]));
				node.setAttribute(PngImageMetadataFormat.n_bKGD_g,
						Integer.toString(metadata._bKGD[1]));
				node.setAttribute(PngImageMetadataFormat.n_bKGD_b,
						Integer.toString(metadata._bKGD[2]));
				break;	

			case PngConstants.COLOR_TYPE_PALETTE:
				node.setAttribute(PngImageMetadataFormat.n_bKGD_plt,
						Integer.toString(metadata._bKGD[0]));
				break;
		}

		root.appendChild(node);
	}

	private void add_tRNS(IIOMetadataNode root)
	{
		tRNS _tRNS = metadata._tRNS;

		if (_tRNS == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_tRNS);

		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
				if (_tRNS.trans == null)
					return;
				node.setAttribute(PngImageMetadataFormat.n_tRNS_gs,
						Integer.toString(_tRNS.trans[0]));
				break;

			case PngConstants.COLOR_TYPE_RGB:
				if (_tRNS.trans == null)
					return;
				node.setAttribute(PngImageMetadataFormat.n_tRNS_r,
						Integer.toString(_tRNS.trans[0]));
				node.setAttribute(PngImageMetadataFormat.n_tRNS_g,
						Integer.toString(_tRNS.trans[1]));
				node.setAttribute(PngImageMetadataFormat.n_tRNS_b,
						Integer.toString(_tRNS.trans[2]));
				break;

			case PngConstants.COLOR_TYPE_PALETTE:
				if (_tRNS.plt == null)
					return;
				byte [] t = _tRNS.plt;
				for (int i=0; i<t.length; i++)
				{
					IIOMetadataNode n = new
						IIOMetadataNode(PngImageMetadataFormat.n_tRNS_plt);
					n.setAttribute(PngImageMetadataFormat.n_tRNS_plt_num,
							Integer.toString(i));
					n.setAttribute(PngImageMetadataFormat.n_tRNS_plt_val,
							Integer.toString((int) t[i]));
					node.appendChild(n);
				}
				break;
		}

		root.appendChild(node);
	}

	private void add_gAMA(IIOMetadataNode root)
	{
		if (metadata._gAMA == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_gAMA);

		node.setAttribute(PngImageMetadataFormat.n_gAMA_val,
				Float.toString(metadata._gAMA.value));

		root.appendChild(node);
	}

	private void add_TEXT(IIOMetadataNode root)
	{
		if (metadata._TEXT == null)
			return;

		Iterator textChunks = metadata._TEXT.iterator();
		while (textChunks.hasNext()) 
		{
			TEXT txt = (TEXT) textChunks.next();

			IIOMetadataNode node = new
				IIOMetadataNode(PngImageMetadataFormat.n_TEXT);
			node.setAttribute(PngImageMetadataFormat.n_TEXT_keyword, txt.keyword);
			node.setAttribute(PngImageMetadataFormat.n_TEXT_text, txt.text);

			root.appendChild(node);
		}
	}

	private void add_pHYs(IIOMetadataNode root)
	{
		if (metadata._pHYs == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_pHYs);

		node.setAttribute(PngImageMetadataFormat.n_pHYs_ppux,
				Integer.toString(metadata._pHYs.pixelsPerUnitX));
		node.setAttribute(PngImageMetadataFormat.n_pHYs_ppuy,
				Integer.toString(metadata._pHYs.pixelsPerUnitY));
		node.setAttribute(PngImageMetadataFormat.n_pHYs_unit,
				Integer.toString(metadata._pHYs.unitsSpecifier));

		root.appendChild(node);
	}

	private void add_tIME(IIOMetadataNode root)
	{
		if (metadata._tIME == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_tIME);

		node.setAttribute(PngImageMetadataFormat.n_tIME_year,
				Integer.toString(metadata._tIME.year));
		node.setAttribute(PngImageMetadataFormat.n_tIME_month,
				Integer.toString(metadata._tIME.month));
		node.setAttribute(PngImageMetadataFormat.n_tIME_day,
				Integer.toString(metadata._tIME.day));
		node.setAttribute(PngImageMetadataFormat.n_tIME_hour,
				Integer.toString(metadata._tIME.hour));
		node.setAttribute(PngImageMetadataFormat.n_tIME_minute,
				Integer.toString(metadata._tIME.minute));
		node.setAttribute(PngImageMetadataFormat.n_tIME_second,
				Integer.toString(metadata._tIME.second));

		root.appendChild(node);
	}

	// To suport writeable metadata, implement the following
	// three methods
	public boolean isReadOnly() 
	{
		return true;
	}
	public void reset() 
	{
	}
	public void mergeTree(String formatName, Node root)
	{
	}
}
