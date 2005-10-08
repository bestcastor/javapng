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

	// class to hold the metadata
	private class Metadata
	{
		// IHDR attributes
		public int width;
		public int height;
		public int bitDepth;
		public int colorType;
		public int compressionMethod;
		public int filterMethod;
		public int interlaceMethod;

		// tEXt, zTXt and iTXt attributes
		public List textKeys = new ArrayList();
		public List textVals = new ArrayList();

		// pHYs attributes
		public boolean pHYs_flag = false;
		public int pixelsPerUnitX;
		public int pixelsPerUnitY;
		public int unitsSpecifier;

		//tIME attributes
		public boolean tIME_flag = false;
		public int year;
		public int month;
		public int day;
		public int hour;
		public int minute;
		public int second;

		// Sets up the metadata
		public void readPng(PngImage png)
		{
			readIHDR(png);
			readTextAttribs(png);
			readPhysAttribs(png);
			readTimeAttribs(png);
		}

		private void readIHDR(PngImage png)
		{
			width = png.getWidth();
			height = png.getHeight();
			bitDepth = png.getBitDepth();
			colorType = png.getColorType();
			compressionMethod = ((Integer)
					png.getProperty(PngConstants.COMPRESSION)).intValue();
			filterMethod = ((Integer)
					png.getProperty(PngConstants.FILTER)).intValue();
			interlaceMethod = png.getInterlace();
		}

		private void readTextAttribs(PngImage png)
		{
			List text_chunks = (List)
				png.getProperty(PngConstants.TEXT_CHUNKS);

			if (text_chunks == null)
				return;


			Iterator itr = text_chunks.iterator();

			while (itr.hasNext())
			{
				TextChunk t = (TextChunk) itr.next();

				//NOTE: unsure about these
				metadata.textKeys.add(t.getKeyword());
				metadata.textVals.add(t.getText());
			}
		}

		private void readPhysAttribs(PngImage png)
		{
			// Make sure a pHYs chunk was read
			if (png.getProperty(PngConstants.PIXELS_PER_UNIT_X) == null)
				return;

			pHYs_flag = true;

			pixelsPerUnitX = ((Integer)
					png.getProperty(PngConstants.PIXELS_PER_UNIT_X)).intValue();
			pixelsPerUnitY = ((Integer)
					png.getProperty(PngConstants.PIXELS_PER_UNIT_Y)).intValue();
			unitsSpecifier = ((Integer)
					png.getProperty(PngConstants.UNIT)).intValue();
		}

		// Have to do this because there is no way (that i know of) to store a
		// date in an XML Node
		private void readTimeAttribs(PngImage png)
		{
			Date date = (Date) png.getProperty(PngConstants.TIME);

			if (date == null)
				return;

			tIME_flag = true;

			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			year 	= cal.get(Calendar.YEAR);
			month 	= cal.get(Calendar.MONTH);
			day 	= cal.get(Calendar.DAY_OF_MONTH);
			hour 	= cal.get(Calendar.HOUR_OF_DAY);
			minute 	= cal.get(Calendar.MINUTE);
			second 	= cal.get(Calendar.SECOND);
		}
	}

	private Metadata metadata;

	public PngImageMetadata(PngImage png) 
	{
		super(false, nativeMetadataFormatName,
				"com.sixlegs.png.iio.PngImageMetadata", null, null);
		metadata = new Metadata();
		metadata.readPng(png);
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

		setRootAttributes(root);
		addTextMetadata(root);
		addPhysicalMetadata(root);
		addTimeMetadata(root);

		return root;
	}

	private void setRootAttributes(IIOMetadataNode root)
	{
		root.setAttribute(PngImageMetadataFormat.root_width,
				Integer.toString(metadata.width));
		root.setAttribute(PngImageMetadataFormat.root_height,
				Integer.toString(metadata.height));
		root.setAttribute(PngImageMetadataFormat.root_bitDepth,
				Integer.toString(metadata.bitDepth));
		root.setAttribute(PngImageMetadataFormat.root_colorType,
				Integer.toString(metadata.colorType));
		root.setAttribute(PngImageMetadataFormat.root_compressionMethod,
				Integer.toString(metadata.compressionMethod));
		root.setAttribute(PngImageMetadataFormat.root_filterMethod,
				Integer.toString(metadata.filterMethod));
		root.setAttribute(PngImageMetadataFormat.root_interlaceMethod,
				Integer.toString(metadata.interlaceMethod));
	}

	private void addTextMetadata(IIOMetadataNode root)
	{
		IIOMetadataNode textNode = new
			IIOMetadataNode(PngImageMetadataFormat.textData);

		Iterator keywordIter = metadata.textKeys.iterator();
		Iterator valueIter = metadata.textVals.iterator();

		while (keywordIter.hasNext()) 
		{
			IIOMetadataNode node = new
				IIOMetadataNode(PngImageMetadataFormat.textData_keyValue);

			node.setAttribute(PngImageMetadataFormat.textData_keyValue_key, 
					(String) keywordIter.next());
			node.setAttribute(PngImageMetadataFormat.textData_keyValue_val, 
					(String) valueIter.next());

			textNode.appendChild(node);
		}

		root.appendChild(textNode);
	}

	private void addPhysicalMetadata(IIOMetadataNode root)
	{
		if (metadata.pHYs_flag)
		{
			IIOMetadataNode phys = new
				IIOMetadataNode(PngImageMetadataFormat.physDimData);

			phys.setAttribute(PngImageMetadataFormat.physDimData_ppux,
					Integer.toString(metadata.pixelsPerUnitX));
			phys.setAttribute(PngImageMetadataFormat.physDimData_ppuy,
					Integer.toString(metadata.pixelsPerUnitY));
			phys.setAttribute(PngImageMetadataFormat.physDimData_unit,
					Integer.toString(metadata.unitsSpecifier));

			root.appendChild(phys);
		}
	}

	private void addTimeMetadata(IIOMetadataNode root)
	{
		
		if (metadata.tIME_flag)
		{
			IIOMetadataNode time= new
				IIOMetadataNode(PngImageMetadataFormat.timeData);

			time.setAttribute(PngImageMetadataFormat.timeData_year,
					Integer.toString(metadata.year));
			time.setAttribute(PngImageMetadataFormat.timeData_month,
					Integer.toString(metadata.month));
			time.setAttribute(PngImageMetadataFormat.timeData_day,
					Integer.toString(metadata.day));
			time.setAttribute(PngImageMetadataFormat.timeData_hour,
					Integer.toString(metadata.hour));
			time.setAttribute(PngImageMetadataFormat.timeData_minute,
					Integer.toString(metadata.minute));
			time.setAttribute(PngImageMetadataFormat.timeData_second,
					Integer.toString(metadata.second));

			root.appendChild(time);
		}
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
