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
			return NativePngImageMetadataFormat.getDefaultInstance();
		else
		if (formatName.equals(IIOMetadataFormatImpl.standardMetadataFormatName))
			return IIOMetadataFormatImpl.getStandardFormatInstance();
		else
			throw new IllegalArgumentException(formatName);
	}

	public Node getAsTree(String formatName) 
	{
		if (formatName.equals(nativeMetadataFormatName))
		{
			if (nativeMetadata == null)
				nativeMetadata = new NativePngImageMetadata(metadata);

			return nativeMetadata.getAsTree();
		}
		else
		if (formatName.equals(IIOMetadataFormatImpl.standardMetadataFormatName))
		{
			if (standardMetadata == null)
				standardMetadata = new StandardPngImageMetadata(metadata);

			return standardMetadata.getAsTree();
		}
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

	// ----------- Methods needed to support the standard metadata format

	protected IIOMetadataNode getStandardChromaNode()
	{
		return standardMetadata.get_Chroma();
	}

	protected IIOMetadataNode getCompressionNode()
	{
		return standardMetadata.get_Compression();
	}

	protected IIOMetadataNode getStandardDataNode()
	{
		return standardMetadata.get_Data();
	}

	protected IIOMetadataNode getStandardDimensionNode()
	{
		return standardMetadata.get_Dimension();
	}

	protected IIOMetadataNode getStandardDocumentNode()
	{
		return standardMetadata.get_Document();
	}

	protected IIOMetadataNode getStandardTextNode()
	{
		return standardMetadata.get_Text();
	}

	//protected IIOMetadataNode getStandardTileNode()
	//{
	//}

	protected IIOMetadataNode getStandardTransparencyNode()
	{
		return standardMetadata.get_Chroma();
	}
}
