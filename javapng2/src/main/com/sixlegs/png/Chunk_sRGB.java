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
    public Chunk_sRGB()
    {
        super(sRGB);
    }

    public void read(PngInputStream in, int length, PngImage png)
    throws IOException
    {
        checkLength(length, 1);
        int intent = in.readByte();
        Map props = png.getProperties();
        if (props.containsKey(PngImage.ICC_PROFILE_NAME))
            throw new PngWarning("Conflicting iCCP and sRGB chunks found");
        props.put(PngImage.RENDERING_INTENT, Integers.valueOf(intent));
        props.put(PngImage.GAMMA, Integers.valueOf(45455));
        /*
          cHRM:
               White Point x: 31270
               White Point y: 32900
               Red x:         64000
               Red y:         33000
               Green x:       30000
               Green y:       60000
               Blue x:        15000
               Blue y:         6000
        */
    }
}
