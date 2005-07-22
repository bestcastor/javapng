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

import java.io.IOException;
import java.util.Map;

class Chunk_bKGD
extends PngChunk
{
    public void read(int type, PngInputStream in, PngImage png)
    throws IOException
    {
        int length = in.getRemaining();
        Map props = png.getProperties();
        
        switch (png.getColorType()) {
        case PngConstants.COLOR_TYPE_PALETTE:
            checkLength(length, 1);
            props.put(PngConstants.BACKGROUND_INDEX, Integers.valueOf(in.readUnsignedByte()));
            break;
            
        case PngConstants.COLOR_TYPE_GRAY:
        case PngConstants.COLOR_TYPE_GRAY_ALPHA:
            checkLength(length, 2);
            props.put(PngConstants.BACKGROUND_GRAY, Integers.valueOf(in.readUnsignedShort()));
            break;
            
        default:
            // truecolor
            checkLength(length, 6);
            props.put(PngConstants.BACKGROUND_RED,   Integers.valueOf(in.readUnsignedShort()));
            props.put(PngConstants.BACKGROUND_GREEN, Integers.valueOf(in.readUnsignedShort()));
            props.put(PngConstants.BACKGROUND_BLUE,  Integers.valueOf(in.readUnsignedShort()));
        }
    }
}
