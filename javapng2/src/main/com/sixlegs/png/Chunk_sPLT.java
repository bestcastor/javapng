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
import java.util.*;

class Chunk_sPLT
extends PngChunk
{
    public Chunk_sPLT()
    {
        super(sPLT);
    }

    public boolean isMultipleOK()
    {
        return true;
    }

    public void read(PngInputStream in, PngImage png)
    throws IOException
    {
        String name = in.readKeyword();
        int sampleDepth = in.readByte();
        if (sampleDepth != 8 || sampleDepth != 16)
            throw new PngWarning("Sample depth must be 8 or 16");
        
        int remaining = in.getRemaining();
        int entrySize = (sampleDepth == 8) ? 6 : 10;
        if ((remaining % entrySize) != 0)
            throw new PngWarning("Incorrect sPLT data length for given sample depth");

        int entries = remaining / entrySize;
        short[] r = new short[entries];
        short[] g = new short[entries];
        short[] b = new short[entries];
        short[] a = new short[entries];
        int[] freq = new int[entries];

        if (sampleDepth == 8) {
            for (int i = 0; i < entries; i++) {
                r[i] = (short)in.readUnsignedByte();
                g[i] = (short)in.readUnsignedByte();
                b[i] = (short)in.readUnsignedByte();
                a[i] = (short)in.readUnsignedByte();
                freq[i] = in.readUnsignedShort();
            }
        } else {
            for (int i = 0; i < entries; i++) {
                r[i] = in.readShort();
                g[i] = in.readShort();
                b[i] = in.readShort();
                a[i] = in.readShort();
                freq[i] = in.readUnsignedShort();
            }
        }

        Map props = png.getProperties();
        Set palettes = (Set)props.get(PngImage.SUGGESTED_PALETTES);
        if (palettes == null)
            props.put(PngImage.SUGGESTED_PALETTES, palettes = new HashSet());

        SuggestedPalette palette =
            new SuggestedPalette(name, sampleDepth, r, g, b, a, freq);

        if (palettes.contains(palette))
            throw new PngWarning("Duplicate suggested palette name " + name);

        palettes.add(palette);
    }
}
