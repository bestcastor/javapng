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

class Chunk_PLTE
extends PngChunk
{
    public Chunk_PLTE()
    {
        super(PLTE);
    }

    public void read(PngInputStream in, int length, Map props, PngConfig config)
    throws IOException
    {
        if (length % 3 != 0)
            throw new PngError("PLTE chunk length indivisible by 3");
        int size = length / 3;

        int colorType = PngImage.getInt(props, PngImage.COLOR_TYPE);
        switch (colorType) {
        case PngImage.COLOR_TYPE_GRAY:
        case PngImage.COLOR_TYPE_GRAY_ALPHA:
            throw new PngWarning("PLTE chunk found in grayscale image");
        }

        if (colorType == PngImage.COLOR_TYPE_PALETTE) {
            int bitDepth = PngImage.getInt(props, PngImage.BIT_DEPTH);
            if (size > (2 << bitDepth) || size > 256)
                throw new PngError("Too many palette entries");
        }

        int[] palette = new int[size];
        for (int i = 0; i < size; i++) {
            palette[i] =
                in.readUnsignedByte() << 16 |
                in.readUnsignedByte() << 8  |
                in.readUnsignedByte();
        }

        props.put(PngImage.PALETTE, palette);
        props.put(PngImage.PALETTE_SIZE, Integers.valueOf(size));
    }
}
