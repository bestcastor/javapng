package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.io.IOException;

abstract public class BeforeDataHook
extends BasicPngConfig
{
    abstract public void process(PngImage png);
    
    public PngChunk getChunk(PngImage png, int type)
    {
        if (type == PngChunk.IDAT)
            process(png);
        return super.getChunk(png, type);
    }
}
