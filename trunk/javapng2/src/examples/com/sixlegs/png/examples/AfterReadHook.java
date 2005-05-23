package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.io.IOException;

abstract public class AfterReadHook
extends BasicPngConfig
{
    abstract public void process(PngImage png) throws IOException;
    
    public PngChunk getChunk(int type)
    {
        if (type == PngChunk.IEND)
            return new HookChunk();
        return super.getChunk(type);
    }

    private class HookChunk
    extends PngChunk
    {
        public void read(PngInputStream in, PngImage png)
        throws IOException
        {
            process(png);
        }
    }
}
