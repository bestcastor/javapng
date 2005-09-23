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

package com.sixlegs.png.iio;

import com.sixlegs.png.PngImage;
import com.sixlegs.png.PngWarning;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public class PngImageReader
extends ImageReader
{
    private PngImage png;
    private boolean readFlag;
    
    public PngImageReader(PngImageReaderSpi provider)
    {
        super(provider);
        png = new IIOPngImage();
    }

    public void dispose()
    {
        png = null;
    }

    public int getWidth(int imageIndex)
    throws IOException
    {
        if (readFlag) {
            return png.getWidth();
        } else {
            return readHeaderInt(16);
        }
    }

    public int getHeight(int imageIndex)
    throws IOException
    {
        if (readFlag) {
            return png.getHeight();
        } else {
            return readHeaderInt(20);
        }
    }

    private int readHeaderInt(int offset)
    throws IOException
    {
        ImageInputStream in = (ImageInputStream)input;
        in.mark();
        in.seek(offset);
        int result = in.readInt();
        in.reset();
        return result;
    }

    public int getNumImages(boolean allowSearch)
    {
        return 1;
    }

    public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata)
    {
        super.setInput(input, seekForwardOnly, ignoreMetadata);
        readFlag = false;
    }

    public IIOMetadata getImageMetadata(int imageIndex)
    {
        return null;
    }

    public IIOMetadata getStreamMetadata()
    {
        return null;
    }

    public Iterator getImageTypes(int imageIndex)
    throws IOException
    {
        // TODO: get color/sample models from PngImage!
        return Arrays.asList(new ImageTypeSpecifier[]{
            ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB),
        }).iterator();
    }

    public BufferedImage read(int imageIndex, ImageReadParam param)
    throws IOException
    {
        if (imageIndex != 0)
            throw new IndexOutOfBoundsException("Requested image " + imageIndex);
        processImageStarted(0);
        readFlag = true;
        return png.read(new StreamWrapper((ImageInputStream)input), false);
    }

    private class IIOPngImage
    extends PngImage
    {
        protected void handleWarning(PngWarning e)
        throws PngWarning
        {
            processWarningOccurred(e.getMessage());
        }

        protected boolean handleFrame(BufferedImage image, int framesLeft)
        {
            // TODO: processPassXXX
            if (framesLeft == 0) {
                processImageComplete();
            } else if (abortRequested()) {
                processReadAborted();
                return false;
            }
            return true;
        }

        protected boolean handleProgress(BufferedImage image, float pct)
        {
            processImageProgress(pct);
            if (abortRequested()) {
                processReadAborted();
                return false;
            }
            return true;
        }
    }
}

