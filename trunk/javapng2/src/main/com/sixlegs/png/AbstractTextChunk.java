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
import java.util.zip.*;

abstract class AbstractTextChunk
extends PngChunk
{
    protected static final String ISO_8859_1 = "ISO-8859-1";
    protected static final String US_ASCII = "US-ASCII";
    protected static final String UTF_8 = "UTF-8";

    protected AbstractTextChunk(int type)
    {
        super(type);
    }

    public boolean isMultipleOK()
    {
        return true;
    }
    
    protected void read(PngInputStream in, int length, PngImage png, boolean compressed)
    throws IOException
    {
        byte[] keyword = readToNull(in);
        read(in, length - (keyword.length + 1), png, compressed, ISO_8859_1,
             new String(keyword, ISO_8859_1), null, null);
    }

    protected void read(PngInputStream in, int length, PngImage png,
                        boolean compressed, String enc,
                        String keyword,
                        String language,
                        String translated)
    throws IOException
    {
        if (keyword.length() == 0 || keyword.length() > 79)
            throw new PngWarning("Invalid " + this + " keyword length: " + keyword.length());
        
        byte[] data = new byte[length];
        in.readFully(data);

        String text;
        if (compressed) {
            if (data[0] != 0)
                throw new PngWarning("Unrecognized " + this + " compression method: " + data[0]);
            byte[] tbuf = new byte[512];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Inflater inf = new Inflater();
            inf.reset();
            inf.setInput(data, 1, length - 1);
            try {
                while (!inf.needsInput()) {
                    out.write(tbuf, 0, inf.inflate(tbuf));
                }
            } catch (DataFormatException e) {
                throw new PngWarning("Error inflating " + this + ": " + e.getMessage());
            }
            text = out.toString(enc);
        } else {
            text = new String(data, enc);
        }

        Map props = png.getProperties();
        List chunks = (List)props.get(PngImage.TEXT_CHUNKS);
        if (chunks == null)
            props.put(PngImage.TEXT_CHUNKS, chunks = new ArrayList());
        chunks.add(new TextChunk(keyword, text, language, translated));
    }

    protected byte[] readToNull(InputStream in)
    throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (;;) {
            int c = in.read();
            switch (c) {
            case 0:
                return out.toByteArray();
            case -1:
                throw new EOFException();
            default:
                out.write(c);
            }
        }
    }
}
