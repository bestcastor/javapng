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

package com.sixlegs.png.ext;

import com.sixlegs.png.PngConstants;

public interface ExtendedPngConstants
extends PngConstants
{
    static final String GIF_DISPOSAL_METHOD = "gif_disposal_method";
    static final String GIF_USER_INPUT_FLAG = "gif_user_input_flag";
    static final String GIF_DELAY_TIME = "gif_delay_time";
    static final String GIF_APPLICATION_EXTENSIONS = "gif_application_extensions";
    static final String POSITION_UNIT = "position_unit";
    static final String POSITION_X = "position_x";
    static final String POSITION_Y = "position_y";
    static final String SCALE_UNIT = "scale_unit";
    static final String PIXEL_WIDTH = "pixel_width";
    static final String PIXEL_HEIGHT = "pixel_height";
    
    static final int POSITION_UNIT_PIXEL = 0;
    static final int POSITION_UNIT_MICROMETER = 1;
    static final int SCALE_UNIT_METER = 1;
    static final int SCALE_UNIT_RADIAN = 2;
}
