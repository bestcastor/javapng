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

class MultiByteArrayInputStream
extends InputStream
{
    private Iterator it;
    private ByteArrayInputStream in; 

    public MultiByteArrayInputStream(List list)
    {
        it = new ArrayList(list).iterator();
        advance();
    }

    private void advance()
    {
        if ((in == null || in.available() == 0) && it.hasNext()) {
            in = new ByteArrayInputStream((byte[])it.next());
            advance();
        }
    }

    public int available()
    {
        return in.available();
    }

    public int read()
    {
        int result = in.read();
        advance();
        return result;
    }

    public int read(byte[] b, int off, int len)
    {
        int result = in.read(b, off, len);
        advance();
        return result;
    }

    public long skip(long n)
    {
        long result = in.skip(n);
        advance();
        return result;
    }

    public boolean markSupported()
    {
        return false;
    }

    public void reset()
    {
    }
}
