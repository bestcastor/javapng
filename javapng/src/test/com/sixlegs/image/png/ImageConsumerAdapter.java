package com.sixlegs.image.png;

import java.awt.image.*;
import java.util.Hashtable;

abstract class ImageConsumerAdapter
implements ImageConsumer
{
    protected ImageConsumerAdapter()
    {
    }
    
    public void imageComplete(int status)
    {
    }
    
    public void setColorModel(ColorModel model)
    {
    }
    
    public void setDimensions(int width, int height)
    {
    }
    
    public void setHints(int hintflags)
    {
    }
    
    public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize)
    {
    }
    
    public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize)
    {
    }
    
    public void setProperties(Hashtable props)
    {
    }
}
