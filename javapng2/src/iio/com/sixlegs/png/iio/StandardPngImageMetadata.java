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
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

/*
* Represents format neutral meta-data.
* 
* The DTD is available from
* http://java.sun.com/j2se/1.4.2/docs/api/javax/imageio/metadata/doc-files/standard_metadata.html
*/
class StandardPngImageMetadata 
{
	private static final String list_sperator = ", ";
	private Metadata metadata;

	public StandardPngImageMetadata(Metadata metadata) 
	{
		this.metadata = metadata;
	}

	public Node getAsTree()
	{
		// Mandatory
		IIOMetadataNode root =
			new IIOMetadataNode(IIOMetadataFormatImpl.standardMetadataFormatName);

		root.appendChild(get_Chroma());
		root.appendChild(get_Compression());
		root.appendChild(get_Data());
		root.appendChild(get_Dimension());
		root.appendChild(get_Document());
		root.appendChild(get_Text());
		root.appendChild(get_Transparency());

		return root;
	}

	public IIOMetadataNode get_Chroma()
	{
		IIOMetadataNode chroma = new IIOMetadataNode("Chroma");

		add_ColorSpaceType(chroma);
		add_NumChannels(chroma);
		add_Gamma(chroma);
		add_BlackIsZero(chroma);
		add_Palette(chroma);
		add_BackgroundIndex(chroma);
		add_BackgroundColor(chroma);

		return chroma;
	}

	private void add_ColorSpaceType(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("ColorSpaceType");

		String clr = null;
		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_RGB:
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
			case PngConstants.COLOR_TYPE_PALETTE:
				clr = "RGB";
				break;
			case PngConstants.COLOR_TYPE_GRAY:
			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				clr = "GRAY";
				break;
			default:
				return;
		}

		node.setAttribute("name", clr);
		root.appendChild(node);
	}

	// DTD wants "List of Integer" here?
	private void add_NumChannels(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("NumChannels");

		String num = null;
		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
			case PngConstants.COLOR_TYPE_PALETTE:
				num = "1";
				break;
			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				num = "2";
				break;
			case PngConstants.COLOR_TYPE_RGB:
				num = "4";
				break;
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				num = "4";
				break;
			default:
				return;
		}

		node.setAttribute("value", num);
		root.appendChild(node);
	}

	private void add_Gamma(IIOMetadataNode root)
	{
		if (metadata._gAMA == null)
			return;

		IIOMetadataNode node = new IIOMetadataNode("Gamma");

		node.setAttribute("value", metadata._gAMA.toString());
		root.appendChild(node);
	}

	private void add_BlackIsZero(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("BlackIsZero");

		node.setAttribute("value", "TRUE");
		root.appendChild(node);
	}

	private void add_Palette(IIOMetadataNode root)
	{
		if (metadata._PLTE == null)
			return;

		byte[] trans = null;
		if (metadata._tRNS != null)
			trans = (byte []) metadata._tRNS;

		IIOMetadataNode node = new IIOMetadataNode("Palette");

		byte [] plt = metadata._PLTE;
		for (int i=0; i<plt.length; i+=3)
		{
			int alpha = 255;
			if (trans != null && i/3 < trans.length)
				alpha = trans[i/3];
			add_PaletteEntry(node, 
					(int) (i/3), 
					0xFF & plt[i], 
					0xFF & plt[i+1], 
					0xFF & plt[i+2],
					alpha);
		}

		root.appendChild(node);
	}

	private void add_PaletteEntry(IIOMetadataNode root, int i, int r, int g, int b, int a)
	{
		IIOMetadataNode node = new IIOMetadataNode("PaletteEntry");

		node.setAttribute("index",	Integer.toString(i));
		node.setAttribute("red",	Integer.toString(r));
		node.setAttribute("green",	Integer.toString(g));
		node.setAttribute("blue",	Integer.toString(b));
		node.setAttribute("alpha",	Integer.toString(a));

		root.appendChild(node);
	}

	private void add_BackgroundIndex(IIOMetadataNode root)
	{
		if (metadata._bKGD == null || 
				metadata._IHDR.colorType != PngConstants.COLOR_TYPE_PALETTE)
			return;

		IIOMetadataNode node = new IIOMetadataNode("BackgroundIndex");

		node.setAttribute("value", Integer.toString(metadata._bKGD[0]));
		root.appendChild(node);
	}

	private void add_BackgroundColor(IIOMetadataNode root)
	{
		if (metadata._bKGD == null ||
				metadata._IHDR.colorType == PngConstants.COLOR_TYPE_PALETTE)
			return;

		IIOMetadataNode node = new IIOMetadataNode("BackgroundColor");

		String r, g, b;
		if (metadata._IHDR.colorType == PngConstants.COLOR_TYPE_GRAY_ALPHA
				|| metadata._IHDR.colorType == PngConstants.COLOR_TYPE_GRAY)
			//XXX is this correct?
			r = g = b = Integer.toString(metadata._bKGD[0]);
		else 
		{
			r = Integer.toString(metadata._bKGD[0]);
			g = Integer.toString(metadata._bKGD[1]);
			b = Integer.toString(metadata._bKGD[2]);
		}

		node.setAttribute("red", r);
		node.setAttribute("green", g);
		node.setAttribute("blue", b);

		root.appendChild(node);
	}

	public IIOMetadataNode get_Compression()
	{
		IIOMetadataNode node = new IIOMetadataNode("Compression");

		add_CompressionTypeName(node);
		add_Lossless(node);
		add_NumProgressiveScans(node);

		return node;
	}

	private void add_CompressionTypeName(IIOMetadataNode root)
	{
		if (metadata._IHDR.compressionMethod != 0)
			return;

		IIOMetadataNode node = new IIOMetadataNode("CompressionTypeName");

		//XXX Is this correct?
		node.setAttribute("value", "DEFLATE/INFLATE");
		root.appendChild(node);
	}

	private void add_Lossless(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("Lossless");
		
		node.setAttribute("value", "TRUE");

		root.appendChild(node);
	}

	private void add_NumProgressiveScans(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("NumProgressiveScans");

		String num;
		if (metadata._IHDR.interlaceMethod == 0)
			num = "0";
		else
			num = "7";

		node.setAttribute("value",num);

		root.appendChild(node);
	}

	public IIOMetadataNode get_Data()
	{
		IIOMetadataNode node = new IIOMetadataNode("Data");

		add_PlanarConfiguration(node);
		add_SampleFormat(node);
		add_BitsPerSample(node);
		//add_SignificantBitsPerSample(node);
		//add_SampleMSB(node);

		return node;
	}

	// XXX Not sure what to put here, options are
	// "PixelInterleaved", "PlaneInterleaved", "LineInterleaved",
	// "TileInterleaved"
	private void add_PlanarConfiguration(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("PlanarConfiguration");
		node.setAttribute("value", "PlaneInterleaved" );
		root.appendChild(node);
	}

	private void add_SampleFormat(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("SampleFormat");

		node.setAttribute("value", "UnsignedIntegral");

		root.appendChild(node);
	}

	private void add_BitsPerSample(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("BitsPerSample");

		String _ = list_sperator;
		String d = Integer.toString(metadata._IHDR.bitDepth);
		String list = null;
		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
				list = d;
				break;
			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				list = d + _ + d;
				break;
			case PngConstants.COLOR_TYPE_PALETTE:
				list = "8";
				break;
			case PngConstants.COLOR_TYPE_RGB:
				list = d + _ + d + _ + d;
				break;
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				list = d + _ + d + _ + d + _ + d;
				break;
			default:
				return;
		}

		node.setAttribute("value", list);
		root.appendChild(node);
	}

	//TODO
	//private void add_SignificantBitsPerSample(IIOMetadataNode root)
	//{
	//	IIOMetadataNode node = new IIOMetadataNode("SignificantBitsPerSample");
	//	root.appendChild(node);
	//}

	//private void add_SampleMSB(IIOMetadataNode root)
	//{
	//	IIOMetadataNode node = new IIOMetadataNode("SampleMSB");
	//	root.appendChild(node);
	//}


	public IIOMetadataNode get_Dimension()
	{
		IIOMetadataNode node = new IIOMetadataNode("Dimension");

		add_PixelAspectRatio(node);
		add_ImageOrientation(node);
		add_HorizontalPixelSize(node);
		add_VerticalPixelSize(node);
		add_HorizontalScreenSize(node);
		add_VerticalScreenSize(node);

		return node;
	}

	private void add_PixelAspectRatio(IIOMetadataNode root)
	{
		if (metadata._pHYs == null)
			return;

		IIOMetadataNode node = new IIOMetadataNode("PixelAspectRatio");

		node.setAttribute("value",
				Float.toString((float)(metadata._pHYs[0].intValue()
						/ metadata._pHYs[1].intValue())));

		root.appendChild(node);
	}

	private void add_ImageOrientation(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("ImageOrientation");

		node.setAttribute("value", "Normal");

		root.appendChild(node);
	}

	private void add_HorizontalPixelSize(IIOMetadataNode root)
	{
		if (metadata._pHYs == null || metadata._pHYs[2].intValue() == 0)
			return;

		IIOMetadataNode node = new IIOMetadataNode("HorizontalPixelSize");

		node.setAttribute("value",
				Float.toString(1000/metadata._pHYs[0].intValue()));

		root.appendChild(node);
	}

	private void add_VerticalPixelSize(IIOMetadataNode root)
	{
		if (metadata._pHYs == null || metadata._pHYs[2].intValue() == 0)
			return;

		IIOMetadataNode node = new IIOMetadataNode("VerticalPixelSize");

		node.setAttribute("value",
				Float.toString(1000/metadata._pHYs[1].intValue()));

		root.appendChild(node);
	}

	private void add_HorizontalScreenSize(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("HorizontalScreenSize");

		node.setAttribute("value", Integer.toString(metadata._IHDR.width));

		root.appendChild(node);
	}

	private void add_VerticalScreenSize(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("VerticalScreenSize");

		node.setAttribute("value", Integer.toString(metadata._IHDR.height));

		root.appendChild(node);
	}

	public IIOMetadataNode get_Document()
	{
		IIOMetadataNode node = new IIOMetadataNode("Document");

		add_ImageModificationTime(node);

		return node;
	}

	private void add_ImageModificationTime(IIOMetadataNode root)
	{
		if (metadata._tIME == null)
			return;

		IIOMetadataNode node = new IIOMetadataNode("ImageCreationTime");

		node.setAttribute("year",	Integer.toString(metadata._tIME.year));
		node.setAttribute("month",	Integer.toString(metadata._tIME.month));
		node.setAttribute("day",	Integer.toString(metadata._tIME.day));
		node.setAttribute("hour",	Integer.toString(metadata._tIME.hour));
		node.setAttribute("minute",	Integer.toString(metadata._tIME.minute));
		node.setAttribute("second",	Integer.toString(metadata._tIME.second));

		root.appendChild(node);
	}

	//TODO flesh these out more by treating them as
	// tEXt, iTXt and zTXt
	public IIOMetadataNode get_Text()
	{
		IIOMetadataNode node = new IIOMetadataNode("Text");

		if (metadata._TEXT != null)
		{
			Iterator textChunks = metadata._TEXT.iterator();
			while (textChunks.hasNext()) 
			{
				Metadata.TEXT txt = (Metadata.TEXT) textChunks.next();

				IIOMetadataNode t = new IIOMetadataNode("TextEntry");

				t.setAttribute("keyword", txt.keyword);
				t.setAttribute("value", txt.text);
				//t.setAttribute("compression", "none");

				node.appendChild(t);
			}
		}

		return node;
	}

	public IIOMetadataNode get_Transparency()
	{
		IIOMetadataNode node = new IIOMetadataNode("Transparency");

		add_Alpha(node);

		return node;
	}

	private void add_Alpha(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("Alpha");

		String val = "none";
		if (metadata._IHDR.colorType == PngConstants.COLOR_TYPE_RGB_ALPHA
				|| metadata._IHDR.colorType == PngConstants.COLOR_TYPE_GRAY_ALPHA
				|| metadata._tRNS != null)
				val = "nonpremultiplied";

		node.setAttribute("value", val);
		root.appendChild(node);
	}
}
