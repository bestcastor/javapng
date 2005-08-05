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
 * An input stream used to read PNG chunk data.
 * @see PngChunk#read
 * @see #getRemaining
 */
public class PngInputStream
extends InputStream
implements DataInput
{
    private CRC32 crc = new CRC32();
    private InputStream in;
    private DataInputStream data;
    private byte[] tmp = new byte[0x1000];
    private long count;
    private int length;
    
    PngInputStream(InputStream in)
    {
        this.in = in;
        data = new DataInputStream(this);
    }

    int startChunk(int length)
    throws IOException
    {
        if (length < 0)
            throw new PngError("Bad chunk length: " + length);
        crc.reset();
        int type = readInt();
        count = 0;
        this.length = length;
        return type;
    }
    
    void endChunk(int type)
    throws IOException
    {
        if (getRemaining() != 0)
            throw new PngError(PngChunk.getName(type) + " read " + count + " bytes, expected " + length);
        if ((int)crc.getValue() != readInt())
            throw new PngError("Bad CRC value for " + PngChunk.getName(type) + " chunk");
    }

    ////////// count/crc InputStream methods //////////

    public int read()
    throws IOException
    {
        int result = in.read();
        if (result != -1) {
            crc.update(result);
            count++;
        }
        return result;
    }
    
    public int read(byte[] b, int off, int len)
    throws IOException
    {
        int result = in.read(b, off, len);
        if (result != -1) {
            crc.update(b, off, result);
            count += result;
        }
        return result;
    }

    public long skip(long n)
    throws IOException
    {
        int result = read(tmp, 0, (int)Math.min(Integer.MAX_VALUE, n));
        return (result < 0) ? 0 : result;
    }

    public void close()
    throws IOException
    {
        in.close();
    }
    
    ////////// DataInput methods we implement directly //////////

    public boolean readBoolean()
    throws IOException
    {
        return readUnsignedByte() != 0;
    }

    public int readUnsignedByte()
    throws IOException
    {
        int a = read();
        if (a < 0)
            throw new EOFException();
        return a;
    }

    public byte readByte()
    throws IOException
    {
        return (byte)readUnsignedByte();
    }

    public int readUnsignedShort()
    throws IOException
    {
        int a = read();
        int b = read();
        if ((a | b) < 0)
            throw new EOFException();
        return (a << 8) + (b << 0);
    }

    public short readShort()
    throws IOException
    {
        return (short)readUnsignedShort();
    }

    public char readChar()
    throws IOException
    {
        return (char)readUnsignedShort();
    }

    public int readInt()
    throws IOException
    {
        int a = read();
        int b = read();
        int c = read();
        int d = read();
        if ((a | b | c | d) < 0)
            throw new EOFException();
        return ((a << 24) + (b << 16) + (c << 8) + (d << 0));
    }

    public long readLong()
    throws IOException
    {
        return ((0xFFFFFFFFL & readInt()) << 32) | (0xFFFFFFFFL & readInt());
    }

    public float readFloat()
    throws IOException
    {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble()
    throws IOException
    {
        return Double.longBitsToDouble(readLong());
    }
    
    ////////// DataInput methods we delegate //////////

    public void readFully(byte[] b)
    throws IOException
    {
        data.readFully(b, 0, b.length);
    }
    
    public void readFully(byte[] b, int off, int len)
    throws IOException
    {
        data.readFully(b, off, len);
    }

    public int skipBytes(int n)
    throws IOException
    {
        return data.skipBytes(n);
    }

    public String readLine()
    throws IOException
    {
        return data.readLine();
    }

    public String readUTF()
    throws IOException
    {
        return data.readUTF();
    }

    ////////// PNG-specific methods //////////

    /**
     * Returns the number of bytes of chunk data that the
     * {@link PngChunk#read} method implementation is required to read.
     * Use {@link #skipBytes} to skip the data.
     * @return the number of bytes in the chunk remaining to be read
     */
    public int getRemaining()
    {
        return (int)(length - count);
    }
}
