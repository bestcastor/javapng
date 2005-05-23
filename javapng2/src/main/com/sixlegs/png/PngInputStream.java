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
import java.util.zip.*;

/**
 * TODO
 */
public class PngInputStream
extends DataInputStream
{
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String US_ASCII = "US-ASCII";
    public static final String UTF_8 = "UTF-8";

    private byte[] tmp = new byte[512];
    private int length;
    
    PngInputStream(CountingInputStream in)
    {
        super(in);
    }

    void setLength(int length)
    {
        this.length = length;
    }

    /**
     * TODO
     */
    public int getRemaining()
    {
        return length - (int)((CountingInputStream)in).getCount();
    }

    /**
     * TODO
     */
    public long readUnsignedInt()
    throws IOException
    {
        return 0xFFFFFFFFL & readInt();
    }

    /**
     * TODO
     */
    public byte[] readCompressed(int length)
    throws IOException
    {
        byte[] data = new byte[length];
        readFully(data);
        if (data[0] != 0)
            throw new PngWarning("Unrecognized compression method: " + data[0]);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Inflater inf = new Inflater();
        inf.reset();
        inf.setInput(data, 1, length - 1);
        try {
            while (!inf.needsInput()) {
                out.write(tmp, 0, inf.inflate(tmp));
            }
        } catch (DataFormatException e) {
            throw new PngWarning(e.getMessage());
        }
        return out.toByteArray();
    }

    /**
     * TODO
     */
    public String readString(String enc)
    throws IOException
    {
        return new String(readToNull(), enc);
    }

    /**
     * TODO
     */
    public String readKeyword()
    throws IOException
    {
        String keyword = readString(ISO_8859_1);
        if (keyword.length() == 0 || keyword.length() > 79)
            throw new PngWarning("Invalid keyword length: " + keyword.length());
        return keyword;
    }

    private byte[] readToNull()
    throws IOException
    {
        int remaining = getRemaining();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < remaining; i++) {
            int c = read();
            switch (c) {
            case 0:
                return out.toByteArray();
            case -1:
                throw new EOFException();
            default:
                out.write(c);
            }
        }
        return out.toByteArray();
    }

    /**
     * TODO
     */
    public void skipFully(long n)
    throws IOException
    {
        while (n > 0) {
            long amt = skip(n);
            if (amt == 0) {
                if (read() == -1)
                    throw new EOFException();
                n--;
            } else {
                n -= amt;
            }
        }
    }

    /**
     * TODO
     */
    public double readFloatingPoint()
    throws IOException
    {
        String s = readString("US-ASCII");
        int e = Math.max(s.indexOf('e'), s.indexOf('E'));
        double d = Double.valueOf(s.substring(0, (e < 0 ? s.length() : e))).doubleValue();
        if (e > 0)
            d *= Math.pow(10d, Double.valueOf(s.substring(e + 1)).doubleValue());
        return d;
    }
}
