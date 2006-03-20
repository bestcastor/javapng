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

public interface ExtendedPngConstants
extends PngConstants
{
    String GIF_DISPOSAL_METHOD = "gif_disposal_method";
    String GIF_USER_INPUT_FLAG = "gif_user_input_flag";
    String GIF_DELAY_TIME = "gif_delay_time";
    String GIF_APPLICATION_EXTENSIONS = "gif_application_extensions";
    String POSITION_UNIT = "position_unit";
    String POSITION_X = "position_x";
    String POSITION_Y = "position_y";
    String SCALE_UNIT = "scale_unit";
    String PIXEL_WIDTH = "pixel_width";
    String PIXEL_HEIGHT = "pixel_height";
    
    int POSITION_UNIT_PIXEL = 0;
    int POSITION_UNIT_MICROMETER = 1;
    int SCALE_UNIT_METER = 1;
    int SCALE_UNIT_RADIAN = 2;
}
