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

/**
 * A suggested palette. Suggested palettes can be useful
 * when the display device is not capable of displaying
 * the full range of colors present in the image. 
 * @see PngImage#getProperty
 * @see PngConstants#SUGGESTED_PALETTES
 */
public interface SuggestedPalette
{
    /**
     * Returns palette name. This is any convenient name for
     * referring to the palette. The name will be unique across all
     * suggested palettes in the same image.
     */
    String getName();

    /**
     * Returns the number of samples.
     */
    int getSampleCount();

    /**
     * Returns the sample depth. This specifies the width of each color and alpha component
     * of each sample in this palette.
     * @return 8 or 16
     */
    int getSampleDepth();

    /**
     * Retrieve a sample value. The red, green, blue, and alpha components of the sample
     * at the given index are stored into the short array. Each component is of the depth
     * specified by {@link #getSampleDepth getSampleDepth}. The color samples are not
     * premultiplied by alpha. An alpha value of 0 means fully transparent.
     * @throws IndexOutOfBoundsException if index < 0, index >= {@link #getSampleCount getSampleCount}, or
     * <code>pixel.length</code> is less than 4
     * @throws NullPointerException if <code>pixel</code> is null
     * @param index the sample index
     * @param pixel the array in which to store the sample components
     */
    void getSample(int index, short[] pixel);

    /**
     * Retrieve a sample frequency value. The frequency value is proportional to the
     * fraction of pixels in the image that are closest to that palette entry in RGBA
     * space. The range of individual values will reasonably fill 0 to 65535.
     * Entries apear in decreasing order of frequency.
     * @param index the sample index
     */
    int getFrequency(int index);
}
