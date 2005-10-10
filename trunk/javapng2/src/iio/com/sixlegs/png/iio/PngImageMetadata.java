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
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class PngImageMetadata 
extends IIOMetadata 
{
	static final String nativeMetadataFormatName = 
		"com.sixlegs.png.iio.PngImageMetadata_v1";

	private Metadata metadata;
	private NativePngImageMetadata nativeMetadata = null;
	private StandardPngImageMetadata standardMetadata = null;

	public PngImageMetadata(PngImage png) 
	{
		super(true, nativeMetadataFormatName,
				"com.sixlegs.png.iio.PngImageMetadata", null, null);
		metadata = new Metadata(png);
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
	private static final String list_sperator = ",";

	protected IIOMetadataNode getStandardChromaNode()
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


	protected IIOMetadataNode getCompressionNode()
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

	protected IIOMetadataNode getStandardDataNode()
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



	protected IIOMetadataNode getStandardDimensionNode()
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

	protected IIOMetadataNode getStandardDocumentNode()
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
	protected IIOMetadataNode getStandardTextNode()
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

	// Could not find specification for this element in DTD
	//protected IIOMetadataNode getStandardTileNode()
	//{
	//}

	protected IIOMetadataNode getStandardTransparencyNode()
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

	// ----------- Methods needed to support the Native metadata format
	//
	// The format is described by PngImageMetadataFormat
	//

	private IIOMetadataNode getNativeTree()
	{
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
					Integer.toString(metadata._sBIT[0]));
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
				metadata._sRGB.toString());

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
		if (metadata._tRNS == null)
			return;

		IIOMetadataNode node = new
			IIOMetadataNode(PngImageMetadataFormat.n_tRNS);

		switch (metadata._IHDR.colorType)
		{
			case PngConstants.COLOR_TYPE_GRAY:
				int[] g = (int[]) metadata._tRNS;
				node.setAttribute(PngImageMetadataFormat.n_tRNS_gs,
						Integer.toString(g[0]));
				break;

			case PngConstants.COLOR_TYPE_RGB:
				int[] trans = (int[]) metadata._tRNS;
				node.setAttribute(PngImageMetadataFormat.n_tRNS_r,
						Integer.toString(trans[0]));
				node.setAttribute(PngImageMetadataFormat.n_tRNS_g,
						Integer.toString(trans[1]));
				node.setAttribute(PngImageMetadataFormat.n_tRNS_b,
						Integer.toString(trans[2]));
				break;

			case PngConstants.COLOR_TYPE_PALETTE:
				byte [] t = (byte[]) metadata._tRNS;
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
				metadata._gAMA.toString());

		root.appendChild(node);
	}

	private void add_TEXT(IIOMetadataNode root)
	{
		if (metadata._TEXT == null)
			return;

		Iterator textChunks = metadata._TEXT.iterator();
		while (textChunks.hasNext()) 
		{
			Metadata.TEXT txt = (Metadata.TEXT) textChunks.next();

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
				metadata._pHYs[0].toString());
		node.setAttribute(PngImageMetadataFormat.n_pHYs_ppuy,
				metadata._pHYs[1].toString());
		node.setAttribute(PngImageMetadataFormat.n_pHYs_unit,
				metadata._pHYs[2].toString());

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

		class TEXT
		{
			String keyword;
			String text;
		}

		class tIME
		{
			int year;
			int month;
			int day;
			int hour;
			int minute;
			int second;
		}

		IHDR _IHDR = null;
		tIME _tIME = null;
		int[] _bKGD = null;
		int[] _hIST = null;
		byte[] _sBIT = null;
		byte[] _PLTE = null;
		float[] _cHRM = null;
		Integer _sRGB = null;
		Integer[] _pHYs = null;
		String[] _iCCP = null;
		Float _gAMA = null;
		Object _tRNS = null;
		List _TEXT = null;
		List _sPLT = null;

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

			//FIXME what is an iCCP profile? text? assuming it is text here.
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
			_gAMA = (Float) png.getProperty(PngConstants.GAMMA);
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
}
