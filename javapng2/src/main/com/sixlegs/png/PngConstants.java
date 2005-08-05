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

/**
 * This interface defines the keys used in the property map
 * of a decoded {@link PngImage}. Where applicable, the enumerated values
 * of properties are also defined here.
 * @see PngImage#getProperty
 * @see PngImage#getProperties
 */
public interface PngConstants
{
    /** {@link PngChunk#IHDR IHDR}: Bit depth */
    static final String BIT_DEPTH = "bit_depth";
    /** {@link PngChunk#IHDR IHDR}: Color type */
    static final String COLOR_TYPE = "color_type";
    /** {@link PngChunk#IHDR IHDR}: Compression method */
    static final String COMPRESSION = "compression";
    /** {@link PngChunk#IHDR IHDR}: Filter method */
    static final String FILTER = "filter";
    /** {@link PngChunk#gAMA gAMA}: Gamma */
    static final String GAMMA = "gamma";
    /** {@link PngChunk#IHDR IHDR}: Width */
    static final String WIDTH = "width";
    /** {@link PngChunk#IHDR IHDR}: Height */
    static final String HEIGHT = "height";
    /** {@link PngChunk#IHDR IHDR}: Interlace method */
    static final String INTERLACE = "interlace";
    /** {@link PngChunk#PLTE PLTE}: Palette entries */
    static final String PALETTE = "palette";
    /** {@link PngChunk#PLTE PLTE}: Palette alpha */
    static final String PALETTE_ALPHA = "palette_alpha";
    /** {@link PngChunk#tRNS tRNS}: Transparency samples */
    static final String TRANSPARENCY = "transparency";
    /** {@link PngChunk#bKGD bKGD}: Background samples */
    static final String BACKGROUND = "background_rgb";
    /** {@link PngChunk#pHYs pHYs}: Pixels per unit, X axis */
    static final String PIXELS_PER_UNIT_X = "pixels_per_unit_x";
    /** {@link PngChunk#pHYs pHYs}: Pixels per unit, Y axis */
    static final String PIXELS_PER_UNIT_Y = "pixels_per_unit_y";
    /** {@link PngChunk#sRGB sRGB}: Rendering intent */
    static final String RENDERING_INTENT = "rendering_intent";
    /** {@link PngChunk#sBIT sBIT}: Significant bits */
    static final String SIGNIFICANT_BITS = "significant_bits";
    /** {@link PngChunk#tEXt tEXt}/{@link PngChunk#zTXt zTXt}/{@link PngChunk#iTXt iTXt}: List of {@linkplain TextChunk text chunks} */
    static final String TEXT_CHUNKS = "text_chunks";
    /** {@link PngChunk#tIME tIME}: Image last-modification time */
    static final String TIME = "time";
    /** {@link PngChunk#pHYs pHYs}: Unit specifier */
    static final String UNIT = "unit";
    /** {@link PngChunk#cHRM cHRM}: Chromaticity */
    static final String CHROMATICITY = "chromaticity";
    /** {@link PngChunk#iCCP iCCP}: ICC profile */
    static final String ICC_PROFILE = "icc_profile";
    /** {@link PngChunk#iCCP iCCP}: ICC profile name */
    static final String ICC_PROFILE_NAME = "icc_profile_name";
    /** {@link PngChunk#hIST hIST}: Palette histogram */
    static final String HISTOGRAM = "histogram";
    /** {@link PngChunk#sPLT sPLT}: List of {@linkplain SuggestedPalette suggested palettes} */
    static final String SUGGESTED_PALETTES = "suggested_palettes";

    /** {@link PngChunk#IHDR IHDR}: Grayscale color type */
    static final int COLOR_TYPE_GRAY = 0;
    /** {@link PngChunk#IHDR IHDR}: Grayscale+alpha color type */
    static final int COLOR_TYPE_GRAY_ALPHA = 4;
    /** {@link PngChunk#IHDR IHDR}: Palette color type */
    static final int COLOR_TYPE_PALETTE = 3;
    /** {@link PngChunk#IHDR IHDR}: RGB color type */
    static final int COLOR_TYPE_RGB = 2;
    /** {@link PngChunk#IHDR IHDR}: RGBA color type */
    static final int COLOR_TYPE_RGB_ALPHA = 6;

    /** {@link PngChunk#IHDR IHDR}: No interlace */
    static final int INTERLACE_NONE = 0;
    /** {@link PngChunk#IHDR IHDR}: Adam7 interlace */
    static final int INTERLACE_ADAM7 = 1;

    /** {@link PngChunk#IHDR IHDR}: Adaptive filtering */
    static final int FILTER_BASE = 0;

    /** {@link PngChunk#IHDR IHDR}: Deflate/inflate compression */
    static final int COMPRESSION_BASE = 0;  

    /** {@link PngChunk#pHYs pHYs}: Unit is unknown */
    static final int UNIT_UNKNOWN = 0;
    /** {@link PngChunk#pHYs pHYs}: Unit is the meter */
    static final int UNIT_METER = 1;

    /** {@link PngChunk#sRGB sRGB}: Perceptual rendering intent */
    static final int SRGB_PERCEPTUAL = 0;
    /** {@link PngChunk#sRGB sRGB}: Relative colorimetric rendering intent */
    static final int SRGB_RELATIVE_COLORIMETRIC = 1;
    /** {@link PngChunk#sRGB sRGB}: Saturation rendering intent */
    static final int SRGB_SATURATION_PRESERVING = 2;
    /** {@link PngChunk#sRGB sRGB}: Absolute colormetric rendering intent */
    static final int SRGB_ABSOLUTE_COLORIMETRIC = 3;
}
