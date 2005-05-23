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
import java.util.Map;

class Chunk_pCAL
extends PngChunk
{
    public void read(int type, PngInputStream in, PngImage png)
    throws IOException
    {
        String calibrationName = in.readKeyword();
        int originalZero = in.readInt();
        int originalMax = in.readInt();
        int equationType = in.readByte();
        String unitName = in.readString(PngInputStream.ISO_8859_1);
        int numParams = in.readByte();
        double[] params = new double[numParams];
        for (int i = 0; i < numParams; i++) {
            params[i] = in.readFloatingPoint();
        }
        
        Map props = png.getProperties();
        // TODO
    }
}
