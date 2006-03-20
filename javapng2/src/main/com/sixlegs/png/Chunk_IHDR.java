/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2006 Chris Nokleberg

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

package com.sixlegs.png;

import java.io.*;
import java.util.Map;

class Chunk_IHDR
extends PngChunk
{
    public void read(int type, PngInputStream in, PngImage png)
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
            break;
        case PngConstants.COLOR_TYPE_PALETTE: 
            if (bitDepth == 16)
                throw new PngError("Bad bit depth for color type " + colorType + ": " + bitDepth);
            break;
        case PngConstants.COLOR_TYPE_GRAY_ALPHA: 
        case PngConstants.COLOR_TYPE_RGB_ALPHA: 
            if (bitDepth <= 4)
                throw new PngError("Bad bit depth for color type " + colorType + ": " + bitDepth);
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
    }
}
