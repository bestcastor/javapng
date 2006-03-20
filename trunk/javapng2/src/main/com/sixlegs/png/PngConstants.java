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
    String BIT_DEPTH = "bit_depth";
    /** {@link PngChunk#IHDR IHDR}: Color type */
    String COLOR_TYPE = "color_type";
    /** {@link PngChunk#IHDR IHDR}: Compression method */
    String COMPRESSION = "compression";
    /** {@link PngChunk#IHDR IHDR}: Filter method */
    String FILTER = "filter";
    /** {@link PngChunk#gAMA gAMA}: Gamma */
    String GAMMA = "gamma";
    /** {@link PngChunk#IHDR IHDR}: Width */
    String WIDTH = "width";
    /** {@link PngChunk#IHDR IHDR}: Height */
    String HEIGHT = "height";
    /** {@link PngChunk#IHDR IHDR}: Interlace method */
    String INTERLACE = "interlace";
    /** {@link PngChunk#PLTE PLTE}: Palette entries */
    String PALETTE = "palette";
    /** {@link PngChunk#PLTE PLTE}: Palette alpha */
    String PALETTE_ALPHA = "palette_alpha";
    /** {@link PngChunk#tRNS tRNS}: Transparency samples */
    String TRANSPARENCY = "transparency";
    /** {@link PngChunk#bKGD bKGD}: Background samples */
    String BACKGROUND = "background_rgb";
    /** {@link PngChunk#pHYs pHYs}: Pixels per unit, X axis */
    String PIXELS_PER_UNIT_X = "pixels_per_unit_x";
    /** {@link PngChunk#pHYs pHYs}: Pixels per unit, Y axis */
    String PIXELS_PER_UNIT_Y = "pixels_per_unit_y";
    /** {@link PngChunk#sRGB sRGB}: Rendering intent */
    String RENDERING_INTENT = "rendering_intent";
    /** {@link PngChunk#sBIT sBIT}: Significant bits */
    String SIGNIFICANT_BITS = "significant_bits";
    /** {@link PngChunk#tEXt tEXt}/{@link PngChunk#zTXt zTXt}/{@link PngChunk#iTXt iTXt}: List of {@linkplain TextChunk text chunks} */
    String TEXT_CHUNKS = "text_chunks";
    /** {@link PngChunk#tIME tIME}: Image last-modification time */
    String TIME = "time";
    /** {@link PngChunk#pHYs pHYs}: Unit specifier */
    String UNIT = "unit";
    /** {@link PngChunk#cHRM cHRM}: Chromaticity */
    String CHROMATICITY = "chromaticity";
    /** {@link PngChunk#iCCP iCCP}: ICC profile */
    String ICC_PROFILE = "icc_profile";
    /** {@link PngChunk#iCCP iCCP}: ICC profile name */
    String ICC_PROFILE_NAME = "icc_profile_name";
    /** {@link PngChunk#hIST hIST}: Palette histogram */
    String HISTOGRAM = "histogram";
    /** {@link PngChunk#sPLT sPLT}: List of {@linkplain SuggestedPalette suggested palettes} */
    String SUGGESTED_PALETTES = "suggested_palettes";

    /** {@link PngChunk#IHDR IHDR}: Grayscale color type */
    int COLOR_TYPE_GRAY = 0;
    /** {@link PngChunk#IHDR IHDR}: Grayscale+alpha color type */
    int COLOR_TYPE_GRAY_ALPHA = 4;
    /** {@link PngChunk#IHDR IHDR}: Palette color type */
    int COLOR_TYPE_PALETTE = 3;
    /** {@link PngChunk#IHDR IHDR}: RGB color type */
    int COLOR_TYPE_RGB = 2;
    /** {@link PngChunk#IHDR IHDR}: RGBA color type */
    int COLOR_TYPE_RGB_ALPHA = 6;

    /** {@link PngChunk#IHDR IHDR}: No interlace */
    int INTERLACE_NONE = 0;
    /** {@link PngChunk#IHDR IHDR}: Adam7 interlace */
    int INTERLACE_ADAM7 = 1;

    /** {@link PngChunk#IHDR IHDR}: Adaptive filtering */
    int FILTER_BASE = 0;

    /** {@link PngChunk#IHDR IHDR}: Deflate/inflate compression */
    int COMPRESSION_BASE = 0;  

    /** {@link PngChunk#pHYs pHYs}: Unit is unknown */
    int UNIT_UNKNOWN = 0;
    /** {@link PngChunk#pHYs pHYs}: Unit is the meter */
    int UNIT_METER = 1;

    /** {@link PngChunk#sRGB sRGB}: Perceptual rendering intent */
    int SRGB_PERCEPTUAL = 0;
    /** {@link PngChunk#sRGB sRGB}: Relative colorimetric rendering intent */
    int SRGB_RELATIVE_COLORIMETRIC = 1;
    /** {@link PngChunk#sRGB sRGB}: Saturation rendering intent */
    int SRGB_SATURATION_PRESERVING = 2;
    /** {@link PngChunk#sRGB sRGB}: Absolute colormetric rendering intent */
    int SRGB_ABSOLUTE_COLORIMETRIC = 3;
}
