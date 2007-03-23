/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2006 Chris Nokleberg

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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class AnimatedPngImage
extends PngImage
{
    public static final int acTL = 0x6163544C;
    public static final int fcTL = 0x6663544C;
    public static final int fdAT = 0x66644154;
        
    private static final int APNG_RENDER_OP_BLEND_FLAG = 8;
    private static final PngConfig DEFAULT_CONFIG =
      new PngConfig.Builder().readLimit(PngConfig.READ_EXCEPT_DATA).build();
      
    private final List chunks = new ArrayList();
    private final List frames = new ArrayList();
    private final Map frameData = new HashMap();
    private final List firstFrameData = new ArrayList();

    private boolean animated;
    private boolean sawData;
    private int numIterations;

    public AnimatedPngImage()
    {
        super(DEFAULT_CONFIG);
    }

    public AnimatedPngImage(PngConfig config)
    {
        super(new PngConfig.Builder(config).readLimit(PngConfig.READ_EXCEPT_DATA).build());
    }

    private void reset()
    {
        animated = sawData = false;
        numIterations = 0;
        chunks.clear();
        frames.clear();
        frameData.clear();
        firstFrameData.clear();
    }

    public boolean isAnimated()
    {
        return animated;
    }

    public int getNumFrames()
    {
        return animated ? frames.size() : 1;
    }

    public int getNumIterations()
    {
        return numIterations;
    }

    public FrameControl getFrame(int index)
    {
        return (FrameControl)frames.get(index);
    }

    public boolean isResetRequired()
    {
        FrameControl first = getFrame(0);
        return (first.getBlend() == FrameControl.BLEND_OVER) ||
            (first.getDispose() == FrameControl.DISPOSE_PREVIOUS) ||
            !first.getBounds().equals(new Rectangle(getWidth(), getHeight()));
    }

    public BufferedImage[] readAllFrames(File file)
    throws IOException
    {
        read(file);
        BufferedImage[] images = new BufferedImage[getNumFrames()];
        for (int i = 0; i < images.length; i++)
            images[i] = readFrame(file, getFrame(i));
        return images;
    }

    public BufferedImage readFrame(File file, FrameControl frame)
    throws IOException
    {
        List data = (List)frameData.get(frame);
        if (data == null)
            throw new IllegalArgumentException("Cannot read data for first APNG frame");
        FrameDataInputStream in = new FrameDataInputStream(file, data);
        try {
            return ImageFactory.createImage(this, in, frame.getBounds().getSize());
        } finally {
            in.close();
        }
    }

    protected boolean readChunk(int type, DataInput in, long offset, int length)
    throws IOException
    {
        int seq;
        switch (type) {
        case PngConstants.IEND:
            validate();
            return super.readChunk(type, in, offset, length);

        case PngConstants.IHDR:
            reset();
            return super.readChunk(type, in, offset, length);

        case acTL:
            if (animated)
                throw new PngException("Multiple acTL chunks are not allowed", false);
            if (sawData)
                throw new PngException("acTL cannot appear after IDAT", false);
            RegisteredChunks.checkLength(type, length, 8);
            animated = true;
            in.readInt(); // ignore numFrames for now
            numIterations = in.readInt();
            return true;

        case fcTL:
            RegisteredChunks.checkLength(type, length, 26);
            seq = in.readInt();
            int w = in.readInt();
            int h = in.readInt();
            Rectangle bounds = new Rectangle(in.readInt(), in.readInt(), w, h);

            if (!sawData) {
                if (!chunks.isEmpty())
                    throw new PngException("Multiple fcTL chunks are not allowed before IDAT", false);
                if (bounds.width != getWidth() || bounds.height != getHeight())
                    throw new PngException("Size of first frame " + bounds.width + "x" + bounds.height +
                                           " should be " + getWidth() + "x" + getHeight(), false);
                if (bounds.x != 0 || bounds.y != 0)
                    throw new PngException("Offset of first frame " + bounds.x + "," + bounds.y +
                                           " should be 0,0", false);
            }
            int delayNum = in.readUnsignedShort();
            int delayDen = in.readUnsignedShort();
            if (delayDen == 0)
                delayDen = 100;

            int disposeOp = in.readByte();
            switch (disposeOp) {
            case FrameControl.DISPOSE_NONE:
            case FrameControl.DISPOSE_BACKGROUND:
                break;
            case FrameControl.DISPOSE_PREVIOUS:
                if (!sawData)
                    throw new PngException("Previous dispose op not valid for the default image", false);
                break;
            default:
                throw new PngException("Unknown APNG dispose op " + disposeOp, false);
            }

            int blendOp = in.readByte();
            if (blendOp == FrameControl.BLEND_OVER) {
                if (!sawData)
                    throw new PngException("Over blend op not valid for the default image", false);
            } else if (blendOp != FrameControl.BLEND_SOURCE) {
                throw new PngException("Unknown APNG blend op " + blendOp, false);
            }
            
            add(seq, new FrameControl(bounds, (float)delayNum / delayDen, disposeOp, blendOp));
            return true;

        case fdAT:
            if (!sawData)
                throw new PngException("fdAT chunks cannot appear before IDAT", false);
            seq = in.readInt();
            add(seq, new FrameData(offset + 4, length - 4));
            return false; // let PngImage skip it

        case PngConstants.IDAT:
            sawData = true;
            if (!chunks.isEmpty())
                firstFrameData.add(new FrameData(offset, length));
            return false;

        default:
            return super.readChunk(type, in, offset, length);
        }
    }

    private void add(int seq, Object chunk)
    throws PngException
    {
        if (chunks.size() != seq)
            throw new PngException("APNG chunks out of order", false);
        chunks.add(chunk);
    }

    private void validate()
    throws IOException
    {
        if (!animated)
            return;
        try {
            List list = null;
            for (int i = 0; i < chunks.size(); i++) {
                Object chunk = chunks.get(i);
                if (chunk == null)
                    throw new PngException("Missing APNG sequence " + i, false);
                if (chunk instanceof FrameControl) {
                    // System.err.println(chunk);
                    frames.add(chunk);
                    frameData.put(chunk, list = new ArrayList());
                } else {
                    list.add(chunk);
                }
            }

            if (!firstFrameData.isEmpty())
                ((List)frameData.get(frames.get(0))).addAll(firstFrameData);

            for (int i = 0; i < frames.size(); i++) {
                if (((List)frameData.get(frames.get(i))).isEmpty())
                    throw new PngException("Missing data for frame", false);
            }
            chunks.clear();
            
        } catch (IOException e) {
            animated = false;
            throw e;
        }
    }
}
