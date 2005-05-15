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

class Chunk_tIME
extends PngChunk
{
    private static TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+0");
    
    public void read(PngInputStream in, PngImage png)
    throws IOException
    {
        checkLength(in.getRemaining(), 7);
        int year   = in.readUnsignedShort();
        int month  = check(in.readUnsignedByte(), 1, 12);
        int day    = check(in.readUnsignedByte(), 1, 31);
        int hour   = check(in.readUnsignedByte(), 0, 23);
        int minute = check(in.readUnsignedByte(), 0, 59);
        int second = check(in.readUnsignedByte(), 0, 60);

        Calendar cal = Calendar.getInstance(TIME_ZONE);
        cal.set(year, month - 1, day, hour, minute, second);
        png.getProperties().put(PngImage.TIME, cal.getTime());
    }

    private static int check(int value, int min, int max)
    throws PngWarning
    {
        if (value < min || value > max)
            throw new PngWarning("tIME value out of bounds");
        return value;
    }
}
