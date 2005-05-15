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

package com.sixlegs.png.ext;

import com.sixlegs.png.*;
import java.io.*;
import java.util.*;

class Chunk_gIFg
extends PngChunk
{
    public void read(PngInputStream in, PngImage png)
    throws IOException
    {
        checkLength(in.getRemaining(), 4);
        int disposalMethod = in.readUnsignedByte();
        int userInputFlag = in.readUnsignedByte();
        int delayTime = in.readUnsignedShort();

        Map props = png.getProperties();
        props.put(ExtendedPngConstants.GIF_DISPOSAL_METHOD, new Integer(disposalMethod));
        props.put(ExtendedPngConstants.GIF_USER_INPUT_FLAG, new Integer(userInputFlag));
        props.put(ExtendedPngConstants.GIF_DELAY_TIME, new Integer(delayTime));
    }
}
