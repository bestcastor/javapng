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

class Chunk_cHRM
extends PngChunk
{
    public void read(int type, PngInputStream in, PngImage png)
    throws IOException
    {
        checkLength(in.getRemaining(), 32);
        float[] array = new float[8];
        for (int i = 0; i < 8; i++)
            array[i] = in.readInt() / 100000f;
        Map props = png.getProperties();
        if (!props.containsKey(PngConstants.RENDERING_INTENT)) {
            props.put(PngConstants.WHITE_POINT_X, new Float(array[0]));
            props.put(PngConstants.WHITE_POINT_Y, new Float(array[1]));
            props.put(PngConstants.RED_X, new Float(array[2]));
            props.put(PngConstants.RED_Y, new Float(array[3]));
            props.put(PngConstants.GREEN_X, new Float(array[4]));
            props.put(PngConstants.GREEN_Y, new Float(array[5]));
            props.put(PngConstants.BLUE_X, new Float(array[6]));
            props.put(PngConstants.BLUE_Y, new Float(array[7]));
        }
    }
}
