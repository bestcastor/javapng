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

class ImageDataInputStream
extends InputStream
{
    private PngInputStream in;
    private StateMachine machine;
    private byte[] onebyte = new byte[1];
    private boolean done;

    public ImageDataInputStream(PngInputStream in, StateMachine machine)
    {
        this.in = in;
        this.machine = machine;
    }

    public int read()
    throws IOException
    {
        return (read(onebyte, 0, 1) == -1) ? -1 : 0xFF & onebyte[0];
    }

    public int read(byte[] b, int off, int len)
    throws IOException
    {
        if (done)
            return -1;
        int total = 0;
        while ((total != len) && !done) {
            while ((total != len) && in.getRemaining() > 0) {
                int amt = Math.min(len - total, in.getRemaining());
                in.readFully(b, off + total, amt);
                total += amt;
            }
            if (total != len) {
                in.endChunk(machine.getType());
                machine.nextState(in.startChunk(in.readInt()));
                done = machine.getType() != PngChunk.IDAT;
            }
        }
        return total;
    }
}
