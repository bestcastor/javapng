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

abstract class AbstractTextChunk
extends PngChunk
{
    protected AbstractTextChunk(int type)
    {
        super(type);
    }

    public boolean isMultipleOK()
    {
        return true;
    }
    
    protected void read(PngInputStream in, PngImage png, boolean compressed)
    throws IOException
    {
        read(in, png, compressed, PngInputStream.ISO_8859_1, in.readKeyword(), null, null);
    }

    protected void read(PngInputStream in, PngImage png,
                        boolean compressed, String enc,
                        String keyword,
                        String language,
                        String translated)
    throws IOException
    {
        byte[] data;
        if (compressed) {
            data = in.readCompressed(in.getRemaining());
        } else {
            data = new byte[in.getRemaining()];
            in.readFully(data);
        }
        String text = new String(data, enc);
        Map props = png.getProperties();
        List chunks = (List)props.get(PngImage.TEXT_CHUNKS);
        if (chunks == null)
            props.put(PngImage.TEXT_CHUNKS, chunks = new ArrayList());
        chunks.add(new TextChunk(keyword, text, language, translated));
    }
}
