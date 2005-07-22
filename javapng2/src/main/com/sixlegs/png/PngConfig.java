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
 * An interface which defines all of the customizable parameters
 * used by {@link PngImage} when decoding an image.
 */
public interface PngConfig
{
    /**
     * The default gamma value to use if the image does not contain
     * an explicit gamma value.
     * {@link BasicPngConfig} defaults to 1/45455.
     */
    float getDefaultGamma();

    /**
     * The default display exponent. Depends on monitor and gamma lookup
     * table settings, if any. A value of 2.2 should
     * work well with most PC displays. If the operating system has
     * a gamma lookup table (e.g. Macintosh) the display exponent should be lower.
     * {@link BasicPngConfig} defaults to 2.2.
     */
    float getDisplayExponent();

    /**
     * The user gamma exponent. The ideal setting depends on the user's
     * particular viewing conditions. Set to greater than 1.0 to darken the mid-level
     * tones, or less than 1.0 to lighten them.
     * {@link BasicPngConfig} defaults to 1.0.
     */
    float getUserExponent();

    /**
     * If true, decoded images will be gamma corrected.
     * Return false if your application will perform the gamma
     * correctly manually.
     * {@link BasicPngConfig} defaults to true.
     * @see PngImage#getGamma
     * @see PngImage#getGammaTable
     */
    boolean getGammaCorrect();

    /**
     * If true, image data will not be decoded.
     * Instead, {@link PngImage#read(java.io.File)} and
     * {@link PngImage#read(java.io.InputStream, boolean)} will return
     * null after reading all of the image metadata.
     * {@link BasicPngConfig} defaults to false.
     */
    boolean getMetadataOnly();

    /**
     * If true, enables progressive display for interlaced images.
     * Each received pixel is expanded (replicated) to fill a rectangle
     * covering the yet-to-be-transmitted pixel positions below and to the right
     * of the received pixel. This produces a "fade-in" effect as the new image
     * gradually replaces the old, at the cost of some additional processing time.
     * {@link BasicPngConfig} defaults to false.
     * @see PngImage#handleFrame
     */
    boolean getProgressive();

    /**
     * If true, 16-bit samples are reduced to 8-bit samples by
     * shifting to the right by 8 bits.
     * {@link BasicPngConfig} defaults to true.
     */
    boolean getReduce16();

    /**
     * Returns a {@link PngChunk} implementation for the given chunk type.
     * The returned chunk object will be responsible for reading the
     * binary chunk data and populating the property map of the {@link PngImage}
     * as appropriate. If <code>null</code> is returned, the chunk is skipped. Any chunk
     * Note that skipping certain critical chunks will guarantee an eventual
     * exception.
     * <p>
     * {@link BasicPngConfig} has a default implementation for all of the chunk
     * types defined in Version 1.2 of the PNG Specification except
     * {@link PngChunk#hIST hIST}, {@link PngChunk#iCCP iCCP}, and
     * {@link PngChunk#sPLT sPLT}. Those three are added by {@link CompletePngConfig}.
     * @param png the image requesting the chunk
     * @param type the chunk type
     */
    PngChunk getChunk(PngImage png, int type);

    /**
     * Callback for customized handling of warnings. Whenever a non-fatal
     * error is found, an instance of {@link PngWarning} is created and
     * passed to this method. To signal that the exception should be treated
     * as a fatal exception, an implementation should re-throw the exception.
     * @throws PngWarning if the warning should be treated as fatal
     */
    void handleWarning(PngWarning e) throws PngWarning;
}
