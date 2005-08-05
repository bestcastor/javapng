/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2005 Chris Nokleberg

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
import java.util.*;

class Chunk_tIME
extends PngChunk
{
    private static TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+0");
    
    public void read(int type, PngInputStream in, PngImage png)
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
        png.getProperties().put(PngConstants.TIME, cal.getTime());
    }

    private static int check(int value, int min, int max)
    throws PngWarning
    {
        if (value < min || value > max)
            throw new PngWarning("tIME value out of bounds");
        return value;
    }
}
