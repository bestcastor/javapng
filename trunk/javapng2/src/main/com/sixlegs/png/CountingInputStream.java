/*
 * $Id$
 * Copyright (c) 2001-2004, Tonic Systems, Inc.
 */

package com.sixlegs.png;

import java.io.*;

class CountingInputStream
extends FilterInputStream
{
    private long count;
    
    public CountingInputStream(InputStream in)
    {
        super(in);
    }

    public long getCount()
    {
        return count;
    }

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
}
