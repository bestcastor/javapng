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

//TODO maybe add more metadata information?
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

	// tEXt, zTXt and iTXt attributes
	private class TEXT
	{
		String keyword;
		String text;
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
		List _TEXT = new ArrayList();

		// Sets up the metadata
		public Metadata(PngImage png)
		{
			read_IHDR(png);
			read_TEXT(png);
			read_pHYs(png);
			read_tIME(png);
		}

		private void read_IHDR(PngImage png)
		{
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

		private void read_TEXT(PngImage png)
		{
			List text_chunks = (List)
				png.getProperty(PngConstants.TEXT_CHUNKS);

			if (text_chunks == null)
				return;

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
		add_TEXT(root);
		add_pHYs(root);
		add_tIME(root);

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

	private void add_TEXT(IIOMetadataNode root)
	{
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
