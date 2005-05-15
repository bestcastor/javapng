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

public interface PngConstants
{
    /** {@link PngChunk#bKGD bKGD}: Background blue sample */
    static final String BACKGROUND_BLUE = "background_blue";
    /** {@link PngChunk#bKGD bKGD}: Background gray sample */
    static final String BACKGROUND_GRAY = "background_gray";
    /** {@link PngChunk#bKGD bKGD}: Background green sample */
    static final String BACKGROUND_GREEN = "background_green";
    /** {@link PngChunk#bKGD bKGD}: Background palette index */
    static final String BACKGROUND_INDEX = "background_index";
    /** {@link PngChunk#bKGD bKGD}: Background red sample */
    static final String BACKGROUND_RED = "background_red";
    /** {@link PngChunk#IHDR IHDR}: Bit depth */
    static final String BIT_DEPTH = "bit_depth";
    /** {@link PngChunk#IHDR IHDR}: Color type */
    static final String COLOR_TYPE = "color_type";
    /** {@link PngChunk#IHDR IHDR}: Compression method */
    static final String COMPRESSION = "compression";
    /** {@link PngChunk#IDAT IDAT}: Image data */
    static final String DATA = "data";
    /** {@link PngChunk#IHDR IHDR}: Filter method */
    static final String FILTER = "filter";
    /** {@link PngChunk#gAMA gAMA}: Gamma */
    static final String GAMMA = "gamma";
    /** {@link PngChunk#IHDR IHDR}: Height */
    static final String HEIGHT = "height";
    /** {@link PngChunk#IHDR IHDR}: Interlace method */
    static final String INTERLACE = "interlace";
    /** {@link PngChunk#PLTE PLTE}: Palette alpha samples */
    static final String PALETTE_ALPHA = "palette_alpha";
    /** {@link PngChunk#PLTE PLTE}: Palette blue samples */
    static final String PALETTE_BLUE = "palette_blue";
    /** {@link PngChunk#PLTE PLTE}: Palette green samples */
    static final String PALETTE_GREEN = "palette_green";
    /** {@link PngChunk#PLTE PLTE}: Palette red samples */
    static final String PALETTE_RED = "palette_red";
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
    /** {@link PngChunk#tRNS tRNS}: Transparency blue sample */
    static final String TRANSPARENCY_BLUE = "transparency_blue";
    /** {@link PngChunk#tRNS tRNS}: Transparency gray sample */
    static final String TRANSPARENCY_GRAY = "transparency_gray";
    /** {@link PngChunk#tRNS tRNS}: Transparency green sample */
    static final String TRANSPARENCY_GREEN = "transparency_green";
    /** {@link PngChunk#tRNS tRNS}: Transparency red sample */
    static final String TRANSPARENCY_RED = "transparency_red";
    /** {@link PngChunk#pHYs pHYs}: Unit specifier */
    static final String UNIT = "unit";
    /** {@link PngChunk#IHDR IHDR}: Width */
    static final String WIDTH = "width";
    /** {@link PngChunk#cHRM cHRM}: White Point x */
    static final String WHITE_POINT_X = "white_point_x";
    /** {@link PngChunk#cHRM cHRM}: White Point y */
    static final String WHITE_POINT_Y = "white_point_y";
    /** {@link PngChunk#cHRM cHRM}: Red x */
    static final String RED_X = "red_x";
    /** {@link PngChunk#cHRM cHRM}: Red y */
    static final String RED_Y = "red_y";
    /** {@link PngChunk#cHRM cHRM}: Blue x */
    static final String BLUE_X = "blue_x";
    /** {@link PngChunk#cHRM cHRM}: Blue y */
    static final String BLUE_Y = "blue_y";
    /** {@link PngChunk#cHRM cHRM}: Green x */
    static final String GREEN_X = "green_x";
    /** {@link PngChunk#cHRM cHRM}: Green y */
    static final String GREEN_Y = "green_y";
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
