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
    static final String BACKGROUND_BLUE = "background_blue";
    static final String BACKGROUND_GRAY = "background_gray";
    static final String BACKGROUND_GREEN = "background_green";
    static final String BACKGROUND_INDEX = "background_index";
    static final String BACKGROUND_RED = "background_red";
    static final String BIT_DEPTH = "bit_depth";
    static final String COLOR_TYPE = "color_type";
    static final String COMPRESSION = "compression";
    static final String DATA = "data";
    static final String FILTER = "filter";
    static final String GAMMA = "gamma";
    static final String HEIGHT = "height";
    static final String INTERLACE = "interlace";
    static final String PALETTE_ALPHA = "palette_alpha";
    static final String PALETTE_BLUE = "palette_blue";
    static final String PALETTE_GREEN = "palette_green";
    static final String PALETTE_RED = "palette_red";
    static final String PIXELS_PER_UNIT_X = "pixels_per_unit_x";
    static final String PIXELS_PER_UNIT_Y = "pixels_per_unit_y";
    static final String RENDERING_INTENT = "rendering_intent";
    static final String SIGNIFICANT_BITS = "significant_bits";
    static final String TEXT_CHUNKS = "text_chunks";
    static final String TIME = "time";
    static final String TRANSPARENCY_BLUE = "transparency_blue";
    static final String TRANSPARENCY_GRAY = "transparency_gray";
    static final String TRANSPARENCY_GREEN = "transparency_green";
    static final String TRANSPARENCY_RED = "transparency_red";
    static final String UNIT = "unit";
    static final String WIDTH = "width";
    static final String WHITE_POINT_X = "white_point_x";
    static final String WHITE_POINT_Y = "white_point_y";
    static final String RED_X = "red_x";
    static final String RED_Y = "red_y";
    static final String BLUE_X = "blue_x";
    static final String BLUE_Y = "blue_y";
    static final String GREEN_X = "green_x";
    static final String GREEN_Y = "green_y";
    static final String ICC_PROFILE = "icc_profile";
    static final String ICC_PROFILE_NAME = "icc_profile_name";
    static final String HISTOGRAM = "histogram";
    static final String SUGGESTED_PALETTES = "suggested_palettes";

    static final int COLOR_TYPE_GRAY = 0;
    static final int COLOR_TYPE_GRAY_ALPHA = 4;
    static final int COLOR_TYPE_PALETTE = 3;
    static final int COLOR_TYPE_RGB = 2;
    static final int COLOR_TYPE_RGB_ALPHA = 6;
  
    static final int INTERLACE_NONE = 0;
    static final int INTERLACE_ADAM7 = 1;

    static final int FILTER_BASE = 0;
    static final int FILTER_INTRAPIXEL = 64;

    static final int COMPRESSION_BASE = 0;  

    static final int UNIT_UNKNOWN = 0;
    static final int UNIT_METER = 1;

    static final int SRGB_PERCEPTUAL = 0;
    static final int SRGB_RELATIVE_COLORIMETRIC = 1;
    static final int SRGB_SATURATION_PRESERVING = 2;
    static final int SRGB_ABSOLUTE_COLORIMETRIC = 3;
}
