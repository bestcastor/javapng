/*
com.sixlegs.image.png - Java package to read and display PNG images
Copyright (C) 1998-2005 Chris Nokleberg

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*/

package com.sixlegs.png;

import java.io.*;
import java.util.Map;

class Chunk_IHDR
extends PngChunk
{
    public void read(PngInputStream in, PngImage png)
    throws IOException
    {
        checkLength(in.getRemaining(), 13);

        int width = in.readInt();
        int height = in.readInt();
        if (width <= 0 || height <= 0)
            throw new PngError("Bad image size: " + width + "x" + height);

        byte bitDepth = in.readByte();
        switch (bitDepth) {
        case 1:
        case 2:
        case 4:
        case 8:
        case 16:
            break;
        default:
            throw new PngError("Bad bit depth: " + bitDepth);
        }

        byte[] sbits = null;
        int colorType = in.readUnsignedByte();
        switch (colorType) {
        case PngConstants.COLOR_TYPE_RGB:
        case PngConstants.COLOR_TYPE_GRAY: 
            sbits = new byte[]{ bitDepth, bitDepth, bitDepth };
            break;
        case PngConstants.COLOR_TYPE_PALETTE: 
            if (bitDepth == 16)
                throw new PngError("Bad bit depth for color type " + colorType + ": " + bitDepth);
            sbits = new byte[]{ 8, 8, 8 };
            break;
        case PngConstants.COLOR_TYPE_GRAY_ALPHA: 
        case PngConstants.COLOR_TYPE_RGB_ALPHA: 
            if (bitDepth <= 4)
                throw new PngError("Bad bit depth for color type " + colorType + ": " + bitDepth);
            sbits = new byte[]{ bitDepth, bitDepth, bitDepth, bitDepth };
            break;
        default:
            throw new PngError("Bad color type: " + colorType);
        }

        int compression = in.readUnsignedByte();
        if (compression != PngConstants.COMPRESSION_BASE) 
            throw new PngError("Unrecognized compression method: " + compression);

        int filter = in.readUnsignedByte();
        if (filter != PngConstants.FILTER_BASE)
            throw new PngError("Unrecognized filter method: " + filter);

        int interlace = in.readUnsignedByte();
        switch (interlace) {
        case PngConstants.INTERLACE_NONE:
        case PngConstants.INTERLACE_ADAM7:
            break;
        default:
            throw new PngError("Unrecognized interlace method: " + interlace);
        }

        Map props = png.getProperties();
        props.put(PngConstants.WIDTH, Integers.valueOf(width));
        props.put(PngConstants.HEIGHT, Integers.valueOf(height));
        props.put(PngConstants.BIT_DEPTH, Integers.valueOf(bitDepth));
        props.put(PngConstants.INTERLACE, Integers.valueOf(interlace));
        props.put(PngConstants.COMPRESSION, Integers.valueOf(compression));
        props.put(PngConstants.FILTER, Integers.valueOf(filter));
        props.put(PngConstants.COLOR_TYPE, Integers.valueOf(colorType));
        props.put(PngConstants.SIGNIFICANT_BITS, sbits);

        if (colorType == PngConstants.COLOR_TYPE_GRAY && bitDepth < 16) {
            int size = 1 << bitDepth;
            byte[] palette = new byte[size];
            for (int i = 0; i < size; i++) {
                palette[i] = (byte)(i * 255 / (size - 1));
            }
            props.put(PngConstants.PALETTE_RED, palette);
            props.put(PngConstants.PALETTE_GREEN, palette);
            props.put(PngConstants.PALETTE_BLUE, palette);
        }
    }
}
