// Copyright (C) 1998, 1999, 2001 Chris Nokleberg
// Please see included LICENSE.TXT

package com.sixlegs.image.png;

import java.io.IOException;

class PngException
extends IOException
{
    PngException()
    {
    }

    PngException(String s)
    {
        super(s);
    }

    public String toString()
    {
        return getMessage();
    }
}

