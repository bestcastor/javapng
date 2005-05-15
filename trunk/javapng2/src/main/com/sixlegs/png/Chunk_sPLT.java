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
    public boolean isMultipleOK()
    {
        return true;
    }

    public void read(PngInputStream in, PngImage png)
    throws IOException
    {
        String name = in.readKeyword();
        int sampleDepth = in.readByte();
        if (sampleDepth != 8 && sampleDepth != 16)
            throw new PngWarning("Sample depth must be 8 or 16");
        
        int remaining = in.getRemaining();
        if ((remaining % ((sampleDepth == 8) ? 6 : 10)) != 0)
            throw new PngWarning("Incorrect sPLT data length for given sample depth");
        byte[] bytes = new byte[remaining];
        in.readFully(bytes);

        Map props = png.getProperties();
        List palettes = (List)props.get(PngImage.SUGGESTED_PALETTES);
        if (palettes == null)
            props.put(PngImage.SUGGESTED_PALETTES, palettes = new ArrayList());

        for (Iterator it = palettes.iterator(); it.hasNext();) {
            if (name.equals(((SuggestedPalette)it.next()).getName()))
                throw new PngWarning("Duplicate suggested palette name " + name);
        }

        palettes.add(new SuggestedPaletteImpl(name, sampleDepth, bytes));
    }

    private static class SuggestedPaletteImpl
    implements SuggestedPalette
    {
        private String name;
        private int sampleDepth;
        private byte[] bytes;
        private int entrySize;
        private int sampleCount;
        
        public SuggestedPaletteImpl(String name, int sampleDepth, byte[] bytes)
        {
            this.name = name;
            this.sampleDepth = sampleDepth;
            this.bytes = bytes;
            entrySize = (sampleDepth == 8) ? 6 : 10;
            sampleCount = bytes.length / entrySize;
        }

        public String getName()
        {
            return name;
        }
        
        public int getSampleCount()
        {
            return sampleCount;
        }
        
        public int getSampleDepth()
        {
            return sampleDepth;
        }

        public void getSample(int index, short[] pixel)
        {
            int from = index * entrySize;
            if (sampleDepth == 8) {
                for (int j = 0; j < 4; j++) {
                    int a = 0xFF & bytes[from++];
                    pixel[j] = (short)a;
                }
            } else {
                for (int j = 0; j < 4; j++) {
                    int a = 0xFF & bytes[from++];
                    int b = 0xFF & bytes[from++];
                    pixel[j] = (short)((a << 8) | b);
                }
            }
        }
        
        public int getFrequency(int index)
        {
            int from = index * (entrySize + 1) - 2;
            int a = 0xFF & bytes[from++];
            int b = 0xFF & bytes[from++];
            return ((a << 8) | b);
        }
    }
}
