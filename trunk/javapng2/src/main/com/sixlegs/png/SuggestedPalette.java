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

public class SuggestedPalette
{
    private String name;
    private int sampleDepth;
    private short[] r;
    private short[] g;
    private short[] b;
    private short[] a;
    private int[] freq;
    
    public SuggestedPalette(String name, int sampleDepth,
                            short[] r, short[] g, short[] b, short[] a, int[] freq)
    {
        this.name = name;
        this.sampleDepth = sampleDepth;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.freq = freq;
    }

    public short[] getRed()
    {
        return r;
    }

    public short[] getGreen()
    {
        return g;
    }

    public short[] getBlue()
    {
        return b;
    }

    public short[] getAlpha()
    {
        return a;
    }

    public int[] getFrequency()
    {
        return freq;
    }

    public int getSampleDepth()
    {
        return sampleDepth;
    }

    public String getName()
    {
        return name;
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public boolean equals(Object o)
    {
        if (o == null)
            return false;
        if (!(o instanceof SuggestedPalette))
            return false;
        return name.equals(((SuggestedPalette)o).name);
    }
}
