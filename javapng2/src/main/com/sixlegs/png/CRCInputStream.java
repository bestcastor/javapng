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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

class CRCInputStream
extends FilterInputStream
{
    private CRC32 crc = new CRC32();
    private int byteCount = 0;
    private byte[] skipBuf;

    public CRCInputStream(InputStream in, byte[] skipBuf)
    {
        super(in);
        this.skipBuf = skipBuf;
    }

    public long getValue()
    {
        return (long)crc.getValue();
    }

    public void reset()
    {
        byteCount = 0;
        crc.reset();
    }

    public int count()
    {
        return byteCount;
    }

    public int read()
    throws IOException
    {
        int x = in.read();
        if (x != -1) {
            crc.update(x);
            byteCount++;
        }
        return x;
    }

    public int read(byte[] b, int off, int len)
    throws IOException
    {
        int x = in.read(b, off, len);
        if (x != -1) {
            crc.update(b, off, x);
            byteCount += x;
        }
        return x;
    }

    private byte[] byteArray = new byte[0];

    public long skip(long n)
    throws IOException
    {
        long total = 0;
        while (n > 0) {
            int amt = read(skipBuf, 0, (int)Math.min(n, skipBuf.length));
            if (amt < 0)
                return total;
            total += amt;
            n -= amt;
        }
        return total;
    }
}
