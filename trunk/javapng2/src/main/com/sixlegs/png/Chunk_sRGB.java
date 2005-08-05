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

class Chunk_sRGB
extends PngChunk
{
    public void read(int type, PngInputStream in, PngImage png)
    throws IOException
    {
        checkLength(in.getRemaining(), 1);
        int intent = in.readByte();
        Map props = png.getProperties();
        props.put(PngConstants.RENDERING_INTENT, Integers.valueOf(intent));
        props.put(PngConstants.GAMMA, new Float(0.45455));
        props.put(PngConstants.CHROMATICITY, new float[]{
            0.3127f, 0.329f, 0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f,
        });
    }
}
