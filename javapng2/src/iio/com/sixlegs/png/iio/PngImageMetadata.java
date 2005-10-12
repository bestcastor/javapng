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
import com.sixlegs.png.PngChunk;
import com.sixlegs.png.SuggestedPalette;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class PngImageMetadata 
extends IIOMetadata 
{
	// Format defined by syn - with bug
	static final String nativeMetadataFormatName = 
		"javax_imageio_png_1.0";

	private Metadata metadata;

	public PngImageMetadata(PngImage png, Map unknownChunks) 
	{
		super(true, nativeMetadataFormatName,
				"com.sixlegs.png.iio.PngImageMetadata", 
				null, null);
		metadata = new Metadata(png, unknownChunks);
	}

	public IIOMetadataFormat getMetadataFormat(String formatName) 
	{
		if (formatName.equals(nativeMetadataFormatName))
			return PngImageMetadataFormat.getDefaultInstance();
		else
		if (formatName.equals(IIOMetadataFormatImpl.standardMetadataFormatName))
			return IIOMetadataFormatImpl.getStandardFormatInstance();
		else
			throw new IllegalArgumentException(formatName);
	}

	public Node getAsTree(String formatName) 
	{
		if (formatName.equals(nativeMetadataFormatName))
			return getNativeTree();
		else
		if (formatName.equals(IIOMetadataFormatImpl.standardMetadataFormatName))
			return getStandardTree();
		else
			throw new IllegalArgumentException(formatName);
	}

	public boolean isReadOnly() 
	{
		//TODO
		return true;
	}
	
	public void reset() 
	{
		//TODO
	}

	public void mergeTree(String formatName, Node root)
	{
		//TODO
	}

	// ----------- Methods needed to support the Standard metadata format
	//
	// The DTD is available from
	// http://java.sun.com/j2se/1.4.2/docs/api/javax/imageio/metadata/doc-files/standard_metadata.html
	//

	// Used to separate elements such as in a List of Integers
	private static final String list_sperator = " ";

	protected IIOMetadataNode getStandardChromaNode()
	{
		boolean not_empty = false;
		IIOMetadataNode chroma = new IIOMetadataNode("Chroma");

		not_empty |= add_ColorSpaceType(chroma);
		not_empty |= add_NumChannels(chroma);
		not_empty |= add_Gamma(chroma);
		not_empty |= add_BlackIsZero(chroma);
		not_empty |= add_Palette(chroma);
		not_empty |= add_BackgroundIndex(chroma);
		not_empty |= add_BackgroundColor(chroma);

		if (not_empty)
			return chroma;
		return null;
	}

	protected IIOMetadataNode getStandardCompressionNode()
	{
		boolean not_empty = false;
		IIOMetadataNode node = new IIOMetadataNode("Compression");

		not_empty |= add_CompressionTypeName(node);
		not_empty |= add_Lossless(node);
		not_empty |= add_NumProgressiveScans(node);

		if (not_empty)
			return node;
		return null;
	}

	protected IIOMetadataNode getStandardDataNode()
	{
		boolean not_empty = false;
		IIOMetadataNode node = new IIOMetadataNode("Data");

		not_empty |= add_PlanarConfiguration(node);
		not_empty |= add_SampleFormat(node);
		not_empty |= add_BitsPerSample(node);
		not_empty |= add_SignificantBitsPerSample(node);
		//not_empty |= add_SampleMSB(node);

		if (not_empty)
			return node;
		return null;
	}

	protected IIOMetadataNode getStandardDimensionNode()
	{
		boolean not_empty = false;
		IIOMetadataNode node = new IIOMetadataNode("Dimension");

		not_empty |= add_PixelAspectRatio(node);
		not_empty |= add_ImageOrientation(node);
		not_empty |= add_HorizontalPixelSize(node);
		not_empty |= add_VerticalPixelSize(node);
		//not_empty |= add_HorizontalScreenSize(node);
		//not_empty |= add_VerticalScreenSize(node);

		if (not_empty)
			return node;
		return null;
	}

	protected IIOMetadataNode getStandardDocumentNode()
	{
		boolean not_empty = false;
		IIOMetadataNode node = new IIOMetadataNode("Document");

		not_empty |= add_ImageModificationTime(node);

		if (not_empty)
			return node;
		return null;
	}

	protected IIOMetadataNode getStandardTextNode()
	{
		boolean not_empty = false;
		IIOMetadataNode node = new IIOMetadataNode("Text");

		if (metadata._TEXT != null)
		{
			Iterator textChunks = metadata._TEXT.iterator();
			while (textChunks.hasNext()) 
			{
				TextChunk txt = (TextChunk) textChunks.next();

				IIOMetadataNode t = new IIOMetadataNode("TextEntry");

				t.setAttribute("keyword", txt.getKeyword());
				t.setAttribute("value", txt.getText());

				//FIXME Sun sometimes has the encoding attribute; figure out
				//when they use it.
				//if (txt.getType() == PngChunk.iTXt)
					t.setAttribute("encoding", "ISO-8859-1");
				
				//FIXME what about compressed iTXt?
				if (txt.getType() == PngChunk.zTXt)
					t.setAttribute("compression", "deflate");
				else
					t.setAttribute("compression", "none");

				node.appendChild(t);
			}
			not_empty = true;
		}

		if (not_empty)
			return node;
		return null;
	}

	// Could not find specification for this element in DTD
	//protected IIOMetadataNode getStandardTileNode()
	//{
	//}

	protected IIOMetadataNode getStandardTransparencyNode()
	{
		boolean not_empty = false;
		IIOMetadataNode node = new IIOMetadataNode("Transparency");

		not_empty |= add_Alpha(node);

		if (not_empty)
			return node;
		return null;
	}

	private boolean add_ColorSpaceType(IIOMetadataNode root)
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
				return false;
		}

		node.setAttribute("name", clr);
		root.appendChild(node);

		return true;
	}

	// DTD wants "List of Integer" here?
	private boolean add_NumChannels(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("NumChannels");

		String num = null;
		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
				num = "1";
				break;
			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				num = "2";
				break;
			case PngConstants.COLOR_TYPE_PALETTE:
				if (metadata._tRNS == null)
					num = "3";
				else
					num = "4";
				break;
			case PngConstants.COLOR_TYPE_RGB:
				num = "3";
				break;
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				num = "4";
				break;
			default:
				return false;
		}

		node.setAttribute("value", num);
		root.appendChild(node);
		return true;
	}

	private boolean add_Gamma(IIOMetadataNode root)
	{
		if (metadata._gAMA == null)
			return false;

		IIOMetadataNode node = new IIOMetadataNode("Gamma");

		node.setAttribute("value", metadata._gAMA.toString());
		root.appendChild(node);
		return true;
	}

	private boolean add_BlackIsZero(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("BlackIsZero");

		node.setAttribute("value", "true");
		root.appendChild(node);
		return true;
	}

	private boolean add_Palette(IIOMetadataNode root)
	{
		if (metadata._PLTE == null)
			return false;

		IIOMetadataNode node = new IIOMetadataNode("Palette");

		byte[] trans = null;
		if (metadata._tRNS != null)
			trans = (byte []) metadata._tRNS;

		byte [] plt = metadata._PLTE;

		int entries = plt.length/3;
		int extraEntries = getPLTENumOfEntries(entries) - entries;

		for (int i=0; i<plt.length; i+=3)
		{
			int alpha = -1;
			if (trans != null )
			{
				if (i/3 < trans.length)
					alpha = trans[i/3];
				else
					alpha = 255;
			}

			add_PaletteEntry(node, 
					(int) (i/3), 
					0xFF & plt[i], 
					0xFF & plt[i+1], 
					0xFF & plt[i+2],
					alpha);
		}
		for (int i=0; i<extraEntries; i++)
			add_PaletteEntry(node, i+entries, 0, 0, 0, trans == null ? -1 : 255);

		root.appendChild(node);
		return true;
	}

	private void add_PaletteEntry(IIOMetadataNode root, int i, int r, int g, int b, int a)
	{
		IIOMetadataNode node = new IIOMetadataNode("PaletteEntry");

		node.setAttribute("index",	Integer.toString(i));
		node.setAttribute("red",	Integer.toString(r));
		node.setAttribute("green",	Integer.toString(g));
		node.setAttribute("blue",	Integer.toString(b));
		if (a >= 0)
			node.setAttribute("alpha",	Integer.toString(a));

		root.appendChild(node);
	}

	private boolean add_BackgroundIndex(IIOMetadataNode root)
	{
		if (metadata._bKGD == null || 
				metadata._IHDR.colorType != PngConstants.COLOR_TYPE_PALETTE)
			return false;

		IIOMetadataNode node = new IIOMetadataNode("BackgroundIndex");

		node.setAttribute("value", Integer.toString(metadata._bKGD[0]));
		root.appendChild(node);
		return true;
	}

	private boolean add_BackgroundColor(IIOMetadataNode root)
	{
		if (metadata._bKGD == null ||
				metadata._IHDR.colorType == PngConstants.COLOR_TYPE_PALETTE)
			return false;

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
		return true;
	}

	private boolean add_CompressionTypeName(IIOMetadataNode root)
	{
		if (metadata._IHDR.compressionMethod != 0)
			return false;

		IIOMetadataNode node = new IIOMetadataNode("CompressionTypeName");

		node.setAttribute("value", "deflate");
		root.appendChild(node);
		return true;
	}

	private boolean add_Lossless(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("Lossless");
		
		node.setAttribute("value", "true");

		root.appendChild(node);
		return true;
	}

	private boolean add_NumProgressiveScans(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("NumProgressiveScans");

		String num;
		if (metadata._IHDR.interlaceMethod == 0)
			num = "1";
		else
			num = "7";

		node.setAttribute("value",num);

		root.appendChild(node);
		return true;
	}


	private boolean add_PlanarConfiguration(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("PlanarConfiguration");
		node.setAttribute("value", "PixelInterleaved" );
		root.appendChild(node);
		return true;
	}

	private boolean add_SampleFormat(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("SampleFormat");

		if (metadata._IHDR.colorType == PngConstants.COLOR_TYPE_PALETTE)
			node.setAttribute("value", "Index");
		else
			node.setAttribute("value", "UnsignedIntegral");

		root.appendChild(node);
		return true;
	}

	private boolean add_BitsPerSample(IIOMetadataNode root)
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
				int s = getPLTENumOfEntries(metadata._PLTE.length/3);
				s = (int) Math.round(Math.log(s)/Math.log(2));
				list = s + _ + s + _ + s;
				if (metadata._tRNS != null)
					list += _ + s;
				break;
			case PngConstants.COLOR_TYPE_RGB:
				list = d + _ + d + _ + d;
				break;
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				list = d + _ + d + _ + d + _ + d;
				break;
			default:
				return false;
		}

		node.setAttribute("value", list);
		root.appendChild(node);
		return true;
	}

	private boolean add_SignificantBitsPerSample(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("SignificantBitsPerSample");

		if (metadata._IHDR.colorType != PngConstants.COLOR_TYPE_PALETTE)
			return false;

		node.setAttribute("value", "4 4 4");
		root.appendChild(node);
		return true;
	}

	//private boolean add_SampleMSB(IIOMetadataNode root)
	//{
	//	IIOMetadataNode node = new IIOMetadataNode("SampleMSB");
	//	root.appendChild(node);
	//	return true;
	//}

	private boolean add_PixelAspectRatio(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("PixelAspectRatio");
		float par;
		if (metadata._pHYS == null)
		{
			par = 1.0f;
		}
		else
		{
			float ppx = metadata._pHYS[0].intValue();
			float ppy = metadata._pHYS[1].intValue();
			par = ppx/ppy;
		}
		node.setAttribute("value", Float.toString(par));
		root.appendChild(node);
		return true;
	}

	private boolean add_ImageOrientation(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("ImageOrientation");

		node.setAttribute("value", "Normal");

		root.appendChild(node);
		return true;
	}

	private boolean add_HorizontalPixelSize(IIOMetadataNode root)
	{
		if (metadata._pHYS == null || metadata._pHYS[2].intValue() == 0)
			return false;

		IIOMetadataNode node = new IIOMetadataNode("HorizontalPixelSize");

		node.setAttribute("value",
				Float.toString(1000/metadata._pHYS[0].intValue()));

		root.appendChild(node);
		return true;
	}

	private boolean add_VerticalPixelSize(IIOMetadataNode root)
	{
		if (metadata._pHYS == null || metadata._pHYS[2].intValue() == 0)
			return false;

		IIOMetadataNode node = new IIOMetadataNode("VerticalPixelSize");

		node.setAttribute("value",
				Float.toString(1000/metadata._pHYS[1].intValue()));

		root.appendChild(node);
		return true;
	}

	private boolean add_HorizontalScreenSize(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("HorizontalScreenSize");

		node.setAttribute("value", Integer.toString(metadata._IHDR.width));

		root.appendChild(node);
		return false;
	}

	private boolean add_VerticalScreenSize(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("VerticalScreenSize");

		node.setAttribute("value", Integer.toString(metadata._IHDR.height));

		root.appendChild(node);
		return true;
	}

	private boolean add_ImageModificationTime(IIOMetadataNode root)
	{
		if (metadata._tIME == null)
			return false;

		Calendar cal = metadata._tIME;

		IIOMetadataNode node = new IIOMetadataNode("ImageModificationTime");

		node.setAttribute("year", Integer.toString(cal.get(Calendar.YEAR)));
		node.setAttribute("month", Integer.toString(cal.get(Calendar.MONTH) + 1));
		node.setAttribute("day", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
		node.setAttribute("hour", Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
		node.setAttribute("minute", Integer.toString(cal.get(Calendar.MINUTE)));
		node.setAttribute("second", Integer.toString(cal.get(Calendar.SECOND)));

		root.appendChild(node);
		return true;
	}


	private boolean add_Alpha(IIOMetadataNode root)
	{
		IIOMetadataNode node = new IIOMetadataNode("Alpha");

		String val = "none";
		if (metadata._IHDR.colorType == PngConstants.COLOR_TYPE_RGB_ALPHA
				|| metadata._IHDR.colorType == PngConstants.COLOR_TYPE_GRAY_ALPHA
				|| metadata._tRNS != null)
				val = "nonpremultipled";

		node.setAttribute("value", val);
		root.appendChild(node);
		return true;
	}

	// ----------- Methods needed to support the Native metadata format
	//
	// This format is very similar to the javax_imageio_png_1.0
	//
	// It differs in that it is not susceptible to the bug described here
	// http://developer.java.sun.com/developer/bugParade/bugs/4518989.html
	//

	private IIOMetadataNode getNativeTree()
	{
		IIOMetadataNode root =
			new IIOMetadataNode(nativeMetadataFormatName);

		add_IHDR(root);
		add_PLTE(root);
		add_bKGD(root);
		add_cHRM(root);
		add_gAMA(root);
		add_hIST(root);
		add_iCCP(root);
		add_iTXt(root);
		add_pHYS(root);
		add_sBIT(root);
		add_sPLT(root);
		add_sRGB(root);
		add_tEXt(root);
		add_tIME(root);
		add_tRNS(root);
		add_zTXt(root);
		add_unknown_chunks(root);

		return root;
	}

	private void add_IHDR(IIOMetadataNode root)
	{
		IIOMetadataNode node = new
			IIOMetadataNode("IHDR");

		String colorType = null;
		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
				colorType = "Grayscale";
				break;
			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				colorType = "GrayAlpha";
				break;
			case PngConstants.COLOR_TYPE_PALETTE:
				colorType = "Palette";
				break;
			case PngConstants.COLOR_TYPE_RGB:
				colorType = "RGB";
				break;
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				colorType = "RGBAlpha";
				break;
		}
				
		node.setAttribute("width", Integer.toString(metadata._IHDR.width));
		node.setAttribute("height", Integer.toString(metadata._IHDR.height));
		node.setAttribute("bitDepth", Integer.toString(metadata._IHDR.bitDepth));
		node.setAttribute("colorType", colorType);
		node.setAttribute("compressionMethod", "deflate");
		node.setAttribute("filterMethod", "adaptive");
		node.setAttribute("interlaceMethod", 
				metadata._IHDR.interlaceMethod == 0 ? "none" : "adam7");

		root.appendChild(node);
	}
	
	private void add_unknown_chunks(IIOMetadataNode root)
	{
		Map map = metadata._unknownChunks;
		if (map.isEmpty())
			return;

		IIOMetadataNode node = new IIOMetadataNode("UnknownChunks");

		Iterator keyItr = map.keySet().iterator();
		while (keyItr.hasNext())
		{
			Integer type = (Integer) keyItr.next();
			int typeInt = type.intValue();
			char[] typeChars = new char[] {
				(char) ((typeInt >> 24 ) & 0xFF),
				(char) ((typeInt >> 16 ) & 0xFF),
				(char) ((typeInt >>  8 ) & 0xFF),
				(char) ( typeInt         & 0xFF)
			};
			String typeName = new String(typeChars);
			IIOMetadataNode ucn = new IIOMetadataNode("UnknownChunk");
			ucn.setAttribute("type", typeName);
			node.appendChild(ucn);
		}

		root.appendChild(node);
	}

	// Not tested
	private void add_sPLT(IIOMetadataNode root)
	{
		if (metadata._sPLT == null)
			return;

		Iterator itr = metadata._sPLT.iterator();
		while (itr.hasNext())
		{
			SuggestedPalette s = (SuggestedPalette) itr.next();

			IIOMetadataNode splt_node = new IIOMetadataNode("sPLT");

			int count = s.getSampleCount();
			for (int i=0; i<count; i++)
			{
				short[] pixels = new short[4];
				s.getSample(i, pixels);

				IIOMetadataNode node = new IIOMetadataNode("sPLTEntry");

				node.setAttribute("index", Integer.toString(i));
				node.setAttribute("red", Integer.toString(pixels[0]));
				node.setAttribute("green", Integer.toString(pixels[1]));
				node.setAttribute("blue", Integer.toString(pixels[2]));
				node.setAttribute("alpha", Integer.toString(pixels[3]));

				splt_node.appendChild(node);
			} //for

			root.appendChild(splt_node);
		} //while
	}

	private void add_hIST(IIOMetadataNode root)
	{
		if (metadata._hIST == null)
			return;

		IIOMetadataNode main_node = new IIOMetadataNode("hIST");

		for (int i=0; i<metadata._hIST.length; i++)
		{
			IIOMetadataNode node = new IIOMetadataNode("hISTEntry");

			node.setAttribute("index", Integer.toString(i));
			node.setAttribute("value", Integer.toString(metadata._hIST[i]));

			main_node.appendChild(node);
		}

		root.appendChild(main_node);
	}

	private void add_iCCP(IIOMetadataNode root)
	{
		if (metadata._iCCP == null)
			return;

		IIOMetadataNode node = new IIOMetadataNode("iCCP");

		node.setAttribute("profileName", metadata._iCCP[0]);
		node.setAttribute("compressionMethod", "deflate");
		//node.setAttribute("data", metadata._iCCP[1]);

		root.appendChild(node);
	}

	private void add_cHRM(IIOMetadataNode root)
	{
		if (metadata._cHRM == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode("cHRM");

		node.setAttribute("whitePointX",	conv_cHRM(0));
		node.setAttribute("whitePointY",	conv_cHRM(1));
		node.setAttribute("redX", 			conv_cHRM(2));
		node.setAttribute("redY", 			conv_cHRM(3));
		node.setAttribute("greenX", 		conv_cHRM(4));
		node.setAttribute("greenY", 		conv_cHRM(5));
		node.setAttribute("blueX", 			conv_cHRM(6));
		node.setAttribute("blueY", 			conv_cHRM(7));

		root.appendChild(node);
	}

	private String conv_cHRM(int idx)
	{
		return Integer.toString((int) Math.round(metadata._cHRM[idx] * 1e5));
	}

	private void add_sBIT(IIOMetadataNode root)
	{
		if (metadata._sBIT == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode("sBIT");

		String nodeName = null;
		String nodeValue = null;

		IIOMetadataNode sbit = null;

		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
				sbit = new IIOMetadataNode("sBIT_Grayscale");
				sbit.setAttribute("gray", Integer.toString(metadata._sBIT[0]));
				break;

			case PngConstants.COLOR_TYPE_RGB:
				sbit = new IIOMetadataNode("sBIT_RGB");
				sbit.setAttribute("red", Integer.toString(metadata._sBIT[0]));
				sbit.setAttribute("green", Integer.toString(metadata._sBIT[1]));
				sbit.setAttribute("blue", Integer.toString(metadata._sBIT[2]));
				break;

			case PngConstants.COLOR_TYPE_PALETTE:
				sbit = new IIOMetadataNode("sBIT_Palette");
				sbit.setAttribute("red", Integer.toString(metadata._sBIT[0]));
				sbit.setAttribute("green", Integer.toString(metadata._sBIT[1]));
				sbit.setAttribute("blue", Integer.toString(metadata._sBIT[2]));
				break;

			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				sbit = new IIOMetadataNode("sBIT_GrayAlpha");
				sbit.setAttribute("gray", Integer.toString(metadata._sBIT[0]));
				sbit.setAttribute("alpha", Integer.toString(metadata._sBIT[1]));
				break;
				
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				sbit = new IIOMetadataNode("");
				sbit.setAttribute("red", Integer.toString(metadata._sBIT[0]));
				sbit.setAttribute("green", Integer.toString(metadata._sBIT[1]));
				sbit.setAttribute("blue", Integer.toString(metadata._sBIT[2]));
				sbit.setAttribute("alpha", Integer.toString(metadata._sBIT[3]));
				break;
		}

		node.appendChild(sbit);
		root.appendChild(node);
	}

	private void add_sRGB(IIOMetadataNode root)
	{
		if (metadata._sRGB == null)
			return;

		IIOMetadataNode node = new IIOMetadataNode("sRGB");

		String ri = null;
		switch (metadata._sRGB.intValue())
		{
			case 0:
				ri = "Perceptual";
				break;
			case 1:
				ri = "Relative colorimetric";
				break;
			case 2:
				ri = "Saturation";
				break;
			case 3:
				ri = "Absolute colorimetric";
				break;
		}
		node.setAttribute("renderingIntent", ri);

		root.appendChild(node);
	}

	// Sun seems to insist on having extra (enpty) palette entries
	private int getPLTENumOfEntries(int entries)
	{
		if (entries == 0)
			return 0;
		if (entries <= 2)
			return 2;
		if (entries <= 4)
			return 4;
		if (entries <= 16)
			return 16;
		return 256;
	}

	private void add_PLTE(IIOMetadataNode root)
	{
		if (metadata._PLTE == null)
			return;

		IIOMetadataNode plt_node = new
			IIOMetadataNode("PLTE");

		byte [] plt = metadata._PLTE;

		int entries = plt.length/3;
		int extraEntries = getPLTENumOfEntries(entries) - entries;

		for (int i=0; i<plt.length; i+=3)
		{
			IIOMetadataNode node = new
				IIOMetadataNode("PLTEEntry");

			node.setAttribute("index", Integer.toString((int) (i/3)));
			node.setAttribute("red", Integer.toString(0xFF & plt[i]));
			node.setAttribute("green", Integer.toString(0xFF & plt[i+1]));
			node.setAttribute("blue", Integer.toString(0xFF & plt[i+2]));

			plt_node.appendChild(node);
		}

		for (int i=0; i<extraEntries; i++)
			add_PaletteEntry(plt_node, i + entries, 0, 0, 0, -1);

		root.appendChild(plt_node);
	}

	private void add_bKGD(IIOMetadataNode root)
	{
		if (metadata._bKGD == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode("bKGD");

		IIOMetadataNode n = null;
		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
			case PngConstants.COLOR_TYPE_GRAY_ALPHA:
				n = new IIOMetadataNode("bKGD_Grayscale");
				n.setAttribute("gray", Integer.toString(metadata._bKGD[0]));
				break;

			case PngConstants.COLOR_TYPE_RGB:
			case PngConstants.COLOR_TYPE_RGB_ALPHA:
				n = new IIOMetadataNode("bKGD_RGB");
				n.setAttribute("red", Integer.toString(metadata._bKGD[0]));
				n.setAttribute("green", Integer.toString(metadata._bKGD[1]));
				n.setAttribute("blue", Integer.toString(metadata._bKGD[2]));
				break;	

			case PngConstants.COLOR_TYPE_PALETTE:
				n = new IIOMetadataNode("bKGD_Palette");
				n.setAttribute("index", Integer.toString(metadata._bKGD[0]));
				break;
		}

		node.appendChild(n);
		root.appendChild(node);
	}

	private void add_tRNS(IIOMetadataNode root)
	{
		if (metadata._tRNS == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode("tRNS");

		IIOMetadataNode subNode = null;

		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
				subNode = new IIOMetadataNode("tRNS_Grayscale");
				int[] g = (int[]) metadata._tRNS;
				subNode.setAttribute("gray",
						Integer.toString(g[0]));
				break;

			case PngConstants.COLOR_TYPE_RGB:
				subNode = new IIOMetadataNode("tRNS_RGB");
				int[] trans = (int[]) metadata._tRNS;
				subNode.setAttribute("red", Integer.toString(trans[0]));
				subNode.setAttribute("green", Integer.toString(trans[1]));
				subNode.setAttribute("blue", Integer.toString(trans[2]));
				break;

			case PngConstants.COLOR_TYPE_PALETTE:
				subNode = new IIOMetadataNode("tRNS_Palette");
				byte [] t = (byte[]) metadata._tRNS;
				for (int i=0; i<t.length; i++)
				{
					IIOMetadataNode n = new
						IIOMetadataNode("tRNS_PaletteEntry");

					n.setAttribute("index", Integer.toString(i));
					n.setAttribute("alpha", Integer.toString((int) t[i]));

					subNode.appendChild(n);
				}
				break;
		}
		node.appendChild(subNode);
		root.appendChild(node);
	}

	private void add_gAMA(IIOMetadataNode root)
	{
		if (metadata._gAMA == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode("gAMA");

		String val = Integer.toString((int)
				Math.round(metadata._gAMA.floatValue() * 1e5));

		node.setAttribute("value", val);

		root.appendChild(node);
	}

	private void add_zTXt(IIOMetadataNode root)
	{
		if (metadata._TEXT == null)
			return;

		boolean textPresent = false;

		IIOMetadataNode node = new
			IIOMetadataNode("tEXt");

		Iterator textChunks = metadata._TEXT.iterator();
		while (textChunks.hasNext()) 
		{
			TextChunk txt = (TextChunk) textChunks.next();

			if (txt.getType() != PngChunk.zTXt)
				continue;

			IIOMetadataNode n = new
				IIOMetadataNode("zTXtEntry");

			n.setAttribute("keyword", txt.getKeyword());
			n.setAttribute("compressionMethod", "deflate");
			n.setAttribute("text", txt.getText());

			node.appendChild(n);
			textPresent = true;
		}

		if (textPresent)
			root.appendChild(node);
	}

	private void add_iTXt(IIOMetadataNode root)
	{
		if (metadata._TEXT == null)
			return;

		boolean textPresent = false;
		IIOMetadataNode node = new
			IIOMetadataNode("iTXt");

		Iterator textChunks = metadata._TEXT.iterator();
		while (textChunks.hasNext()) 
		{
			TextChunk txt = (TextChunk) textChunks.next();

			if (txt.getType() != PngChunk.iTXt)
				continue;

			IIOMetadataNode n = new
				IIOMetadataNode("iTXtEntry");

			n.setAttribute("keyword", txt.getKeyword());
			//FIXME Find out whether this iTXt was compressed
			n.setAttribute("compressionFlag", "FALSE");
			n.setAttribute("languageTag", txt.getLanguage());
			n.setAttribute("translatedKeyword", txt.getTranslatedKeyword());
			n.setAttribute("text", txt.getText());

			node.appendChild(n);
			textPresent = true;
		}

		if (textPresent)
			root.appendChild(node);
	}

	private void add_tEXt(IIOMetadataNode root)
	{
		if (metadata._TEXT == null)
			return;

		boolean textPresent = false;
		IIOMetadataNode node = new IIOMetadataNode("tEXt");

		Iterator textChunks = metadata._TEXT.iterator();
		while (textChunks.hasNext()) 
		{
			TextChunk txt = (TextChunk) textChunks.next();

			if (txt.getType() != PngChunk.tEXt)
				continue;

			IIOMetadataNode n = new IIOMetadataNode("tEXtEntry");

			n.setAttribute("keyword", txt.getKeyword());
			n.setAttribute("value", txt.getText());

			node.appendChild(n);
			textPresent = true;
		}

		if (textPresent)
			root.appendChild(node);
	}

	private void add_pHYS(IIOMetadataNode root)
	{
		if (metadata._pHYS == null)
			return;

		IIOMetadataNode node = new IIOMetadataNode("pHYS");

		node.setAttribute("pixelsPerUnitXAxis", metadata._pHYS[0].toString());
		node.setAttribute("pixelsPerUnitYAxis", metadata._pHYS[1].toString());
		node.setAttribute("unitSpecifier", 
				metadata._pHYS[2].intValue() == 0 ? "unknown" : "meter");

		root.appendChild(node);
	}

	private void add_tIME(IIOMetadataNode root)
	{
		if (metadata._tIME == null)
			return;

		Calendar cal = metadata._tIME;

		IIOMetadataNode node = new IIOMetadataNode("tIME");

		node.setAttribute("year", Integer.toString(cal.get(Calendar.YEAR)));
		node.setAttribute("month", Integer.toString(cal.get(Calendar.MONTH) + 1));
		node.setAttribute("day", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
		node.setAttribute("hour", Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
		node.setAttribute("minute", Integer.toString(cal.get(Calendar.MINUTE)));
		node.setAttribute("second", Integer.toString(cal.get(Calendar.SECOND)));

		root.appendChild(node);
	}

	private class Metadata
	{
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

		IHDR _IHDR = null;
		int[] _bKGD = null;
		int[] _hIST = null;
		byte[] _sBIT = null;
		byte[] _PLTE = null;
		float[] _cHRM = null;
		Integer _sRGB = null;
		Integer[] _pHYS = null;
		String[] _iCCP = null;
		Float _gAMA = null;
		Object _tRNS = null;
		List _TEXT = null;
		List _sPLT = null;
		Map _unknownChunks = null;
		Calendar _tIME = null;

		public Metadata(PngImage png, Map unknownChunks)
		{
			read_IHDR(png);
			read_PLTE(png);
			read_gAMA(png);
			read_TEXT(png);
			read_pHYS(png);
			read_tIME(png);
			read_tRNS(png);
			read_bKGD(png);
			read_sRGB(png);
			read_sBIT(png);
			read_cHRM(png);
			read_iCCP(png);
			read_hIST(png);
			read_sPLT(png);
			_unknownChunks = unknownChunks;
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
			_sPLT = (List) png.getProperty(PngConstants.SUGGESTED_PALETTES);
		}

		private void read_hIST(PngImage png)
		{
			_hIST = (int[]) png.getProperty(PngConstants.HISTOGRAM);
		}

		private void read_iCCP(PngImage png)
		{
			Object prof = png.getProperty(PngConstants.ICC_PROFILE);
			if (prof == null)
				return;

			_iCCP = new String [] {
				new String((byte[]) prof),
				(String) png.getProperty(PngConstants.ICC_PROFILE_NAME)
			};
		}

		private void read_cHRM(PngImage png)
		{
			_cHRM = (float []) png.getProperty(PngConstants.CHROMATICITY);
		}

		private void read_sBIT(PngImage png)
		{
			_sBIT = (byte []) png.getProperty(PngConstants.SIGNIFICANT_BITS);
		}

		private void read_sRGB(PngImage png)
		{
			_sRGB = (Integer) png.getProperty(PngConstants.RENDERING_INTENT);
		}

		private void read_bKGD(PngImage png)
		{
			_bKGD = (int []) png.getProperty(PngConstants.BACKGROUND);
		}

		private void read_PLTE(PngImage png)
		{
			_PLTE = (byte []) png.getProperty(PngConstants.PALETTE);
		}

		private void read_tRNS(PngImage png)
		{
			switch (_IHDR.colorType)
			{
				case PngConstants.COLOR_TYPE_RGB:
				case PngConstants.COLOR_TYPE_GRAY:
					_tRNS = png.getProperty(PngConstants.TRANSPARENCY);
					break;

				case PngConstants.COLOR_TYPE_PALETTE:
					_tRNS = png.getProperty(PngConstants.PALETTE_ALPHA);
					break;
			}
		}

		private void read_TEXT(PngImage png)
		{
			_TEXT = (List) png.getProperty(PngConstants.TEXT_CHUNKS);
		}

		private void read_gAMA(PngImage png)
		{
			_gAMA = (Float) png.getProperty(PngConstants.GAMMA);
		}

		private void read_pHYS(PngImage png)
		{
			Object p = png.getProperty(PngConstants.PIXELS_PER_UNIT_X);
			if (p == null)
				return;

			_pHYS = new Integer[] {
				(Integer) png.getProperty(PngConstants.PIXELS_PER_UNIT_X),
				(Integer) png.getProperty(PngConstants.PIXELS_PER_UNIT_Y),
				(Integer) png.getProperty(PngConstants.UNIT)
			};
		}

		private void read_tIME(PngImage png)
		{
			Date date = (Date) png.getProperty(PngConstants.TIME);

			if (date == null)
				return;

			TimeZone tz = TimeZone.getTimeZone("UTC");
			_tIME = Calendar.getInstance(tz);
			_tIME.setTime(date);
		}
	}
}
