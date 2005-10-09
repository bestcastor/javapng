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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

import com.sixlegs.png.PngImage;
import com.sixlegs.png.PngConstants;
import com.sixlegs.png.TextChunk;


// Package private class to store data
class Metadata
{
	// IHDR chunk attributes
	class IHDR
	{
		int width;
		int height;
		int bitDepth;
		int colorType;
		int compressionMethod;
		int filterMethod;
		int interlaceMethod;
	}

	// tEXt, zTXt and iTXt attributes
	class TEXT
	{
		String keyword;
		String text;
	}

	//tIME attributes
	class tIME
	{
		int year;
		int month;
		int day;
		int hour;
		int minute;
		int second;
	}

	// class to hold the metadata before it is created into an XML document
	// metadata is stored here
	IHDR _IHDR = null;
	tIME _tIME = null;
	int[] _bKGD = null;
	int[] _sRGB = null;
	int[] _sBIT = null;
	int[] _hIST = null;
	byte[] _PLTE = null;
	float[] _cHRM = null;
	Integer[] _pHYs = null;
	String[] _iCCP = null;
	Float _gAMA = null;
	Object _tRNS = null;
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
				_tRNS = trans;
				break;

			case PngConstants.COLOR_TYPE_PALETTE:
				Object pltObj = png.getProperty(PngConstants.PALETTE_ALPHA);
				if (pltObj == null)
					return;
				_tRNS = pltObj;
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

		_gAMA = (Float) gamma;
	}

	private void read_pHYs(PngImage png)
	{
		Object p = png.getProperty(PngConstants.PIXELS_PER_UNIT_X);
		if (p == null)
			return;

		_pHYs = new Integer[] {
			(Integer) png.getProperty(PngConstants.PIXELS_PER_UNIT_X),
			(Integer) png.getProperty(PngConstants.PIXELS_PER_UNIT_Y),
			(Integer) png.getProperty(PngConstants.UNIT)
		};
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
