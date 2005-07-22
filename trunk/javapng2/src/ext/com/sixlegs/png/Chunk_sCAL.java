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

class Chunk_sCAL
extends PngChunk
{
    public void read(int type, PngInputStream in, PngImage png)
    throws IOException
    {
        int unit = in.readByte();
        double width = PngUtils.readFloatingPoint(in);
        double height = PngUtils.readFloatingPoint(in);

        Map props = png.getProperties();
        props.put(ExtendedPngConstants.SCALE_UNIT, new Integer(unit));
        props.put(ExtendedPngConstants.PIXEL_WIDTH, new Double(width));
        props.put(ExtendedPngConstants.PIXEL_HEIGHT, new Double(height));
    }
}
