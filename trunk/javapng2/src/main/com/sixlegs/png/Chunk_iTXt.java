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

class Chunk_iTXt
extends AbstractTextChunk
{
    public void read(PngInputStream in, PngImage png)
    throws IOException
    {
        String keyword = in.readKeyword();
        int flag = in.readByte();
        int method = in.readByte();
        boolean compressed = false;
        if (flag == 1) {
            compressed = true;
            if (method != 0)
                throw new PngWarning("Unrecognized " + this + " compression method: " + method);
        } else if (flag != 0) {
            throw new PngWarning("Illegal " + this + " compression flag: " + flag);
        }
        read(in, png, compressed, PngInputStream.UTF_8, keyword,
             in.readString(PngInputStream.US_ASCII),
             in.readString(PngInputStream.UTF_8));
    }
}
