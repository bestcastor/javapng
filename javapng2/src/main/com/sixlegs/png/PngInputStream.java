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

public class PngInputStream
extends DataInputStream
{
    public PngInputStream(InputStream in)
    {
        super(in);
    }

    public long readUnsignedInt()
    throws IOException
    {
        return 0xFFFFFFFFL & readInt();
    }

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

    /*
    static public double parseFloatingPoint(String token)
    {
        int st = 0;
        int e1 = Math.max(token.indexOf('e'),token.indexOf('E'));
        double d = Double.valueOf(token.substring(st, (e1 < 0 ? token.length() : e1))).doubleValue();
        if (e1 > 0) d *= Math.pow(10d, Double.valueOf(token.substring(e1+1)).doubleValue());
        return d;
    }

    public double readFloatingPoint()
    throws IOException
    {
        return parseFloatingPoint(readString());
    }
    */
}
