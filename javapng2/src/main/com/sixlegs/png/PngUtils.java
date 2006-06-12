/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2006 Chris Nokleberg

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version.
*/

package com.sixlegs.png;

import java.io.*;
import java.util.zip.*;

class PngUtils
{
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String US_ASCII = "US-ASCII";
    public static final String UTF_8 = "UTF-8";

    private PngUtils()
    {
    }

    public static void readFully(InputStream in, byte[] b, int off, int len)
    throws IOException
    {
        int total = 0;
        while (total < len) {
            int result = in.read(b, off + total, len - total);
            if (result == -1)
                throw new EOFException();
            total += result;
        }
    }

    public static void skipFully(InputStream in, long skip)
    throws IOException
    {
        long total = 0;
        while (total < skip)
            total += in.skip(skip - total);
    }

    public static byte[] readCompressed(PngInputStream in, int length)
    throws IOException
    {
        byte[] data = new byte[length];
        in.readFully(data);
        if (data[0] != 0)
            throw new PngException("Unrecognized compression method: " + data[0], false);
        byte[] tmp = new byte[0x1000];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Inflater inf = new Inflater();
        inf.reset();
        inf.setInput(data, 1, length - 1);
        try {
            while (!inf.needsInput()) {
                out.write(tmp, 0, inf.inflate(tmp));
            }
        } catch (DataFormatException e) {
            throw new PngException(e.getMessage(), false);
        }
        return out.toByteArray();
    }

    public static String readString(PngInputStream in, String enc)
    throws IOException
    {
        return new String(readToNull(in), enc);
    }

    public static String readKeyword(PngInputStream in)
    throws IOException
    {
        String keyword = readString(in, ISO_8859_1);
        if (keyword.length() == 0 || keyword.length() > 79)
            throw new PngException("Invalid keyword length: " + keyword.length(), false);
        return keyword;
    }

    private static byte[] readToNull(PngInputStream in)
    throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int remaining = in.getRemaining();
        for (int i = 0; i < remaining; i++) {
            int c = in.readUnsignedByte();
            if (c == 0)
                return out.toByteArray();
            out.write(c);
        }
        return out.toByteArray();
    }

    public static double readFloatingPoint(PngInputStream in)
    throws IOException
    {
        String s = readString(in, "US-ASCII");
        int e = Math.max(s.indexOf('e'), s.indexOf('E'));
        double d = Double.valueOf(s.substring(0, (e < 0 ? s.length() : e))).doubleValue();
        if (e > 0)
            d *= Math.pow(10d, Double.valueOf(s.substring(e + 1)).doubleValue());
        return d;
    }
}
