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

import com.sixlegs.png.*;

public class ExtendedPngConfig
extends CompletePngConfig
{
    public static final String GIF_DISPOSAL_METHOD = "gif_disposal_method";
    public static final String GIF_USER_INPUT_FLAG = "gif_user_input_flag";
    public static final String GIF_DELAY_TIME = "gif_delay_time";
    public static final String GIF_APPLICATION_EXTENSIONS = "gif_application_extensions";
    public static final String POSITION_UNIT = "position_unit";
    public static final String POSITION_X = "position_x";
    public static final String POSITION_Y = "position_y";
    public static final String SCALE_UNIT = "scale_unit";
    public static final String PIXEL_WIDTH = "pixel_width";
    public static final String PIXEL_HEIGHT = "pixel_height";
    
    public static final int POSITION_UNIT_PIXEL = 0;
    public static final int POSITION_UNIT_MICROMETER = 1;
    public static final int SCALE_UNIT_METER = 1;
    public static final int SCALE_UNIT_RADIAN = 2;

    private static final PngChunk gIFg = new Chunk_gIFg();
    private static final PngChunk gIFx = new Chunk_gIFx();
    private static final PngChunk oFFs = new Chunk_oFFs();
    private static final PngChunk pCAL = new Chunk_pCAL();
    private static final PngChunk sCAL = new Chunk_sCAL();
    
    public PngChunk getChunk(int type)
    {
        switch (type) {
        case PngChunk.gIFg: return gIFg;
        case PngChunk.gIFx: return gIFx;
        case PngChunk.oFFs: return oFFs;
        case PngChunk.pCAL: return pCAL;
        case PngChunk.sCAL: return sCAL;
        }
        return super.getChunk(type);
    }
}
