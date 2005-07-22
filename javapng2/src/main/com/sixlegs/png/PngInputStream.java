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

public class PngInputStream
extends InputStream
implements DataInput
{
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String US_ASCII = "US-ASCII";
    public static final String UTF_8 = "UTF-8";

    private CRCInputStream in;
    private DataInputStream data;
    private byte[] tmp = new byte[512];
    private long count;
    private int length;
    
    PngInputStream(InputStream in)
    {
        this.in = new CRCInputStream(in, new byte[0x1000]);
        data = new DataInputStream(this);
    }

    int startChunk(int length)
    throws IOException
    {
        if (length < 0)
            throw new PngError("Bad chunk length: " + length);
        in.reset();
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
        long calcChecksum = in.getValue();
        long fileChecksum = readUnsignedInt();
        if (calcChecksum != fileChecksum)
            throw new PngError("Bad CRC value for " + PngChunk.getName(type) + " chunk");
    }

    ////////// counted InputStream methods //////////

    public int read()
    throws IOException
    {
        int result = in.read();
        if (result != -1)
            count++;
        return result;
    }
    
    public int read(byte[] b, int off, int len)
    throws IOException
    {
        int result = in.read(b, off, len);
        if (result > 0)
            count += result;
        return result;
    }

    public long skip(long n)
    throws IOException
    {
        long result = in.skip(n);
        count += result;
        return result;
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
        return (readUnsignedInt() << 32) | readUnsignedInt();
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
        
    ////////// missing DataInput methods //////////

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

    ////////// PNG-specific methods //////////
    
    public int getRemaining()
    {
        return (int)(length - count);
    }

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

    public String readString(String enc)
    throws IOException
    {
        return new String(readToNull(), enc);
    }

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
