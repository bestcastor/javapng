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
    public Chunk_iTXt()
    {
        super(iTXt);
    }

    public void read(PngInputStream in, int length, PngImage png)
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
        byte[] language = in.readToNull();
        byte[] translated = in.readToNull();
        length -= (keyword.length() + language.length + translated.length + 5);
        read(in, length, png, compressed, UTF_8, keyword,
             new String(language, US_ASCII),
             new String(translated, UTF_8));
    }
}
