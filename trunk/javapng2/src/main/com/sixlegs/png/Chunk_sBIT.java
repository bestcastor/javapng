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

class Chunk_sBIT
extends PngChunk
{
    public Chunk_sBIT()
    {
        super(sBIT);
    }

    public void read(PngInputStream in, int length, PngImage png)
    throws IOException
    {
        boolean paletted = png.getColorType() == PngImage.COLOR_TYPE_PALETTE;
        int count = paletted ? 3 : png.getSamples();
        checkLength(length, count);

        int depth = paletted ? 8 : png.getBitDepth();
        int[] array = new int[count];
        for (int i = 0; i < count; i++) {
            int bits = in.readByte();
            if (bits <= 0 || bits > depth)
                throw new PngWarning("Illegal sBIT sample depth");
            array[i] = bits;
        }

        png.getProperties().put(PngImage.SIGNIFICANT_BITS, array);
    }
}
