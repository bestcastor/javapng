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
    public static final int acTl = 0x6163546C;
    public static final int fcTl = 0x6663546C;
    public static final int fdAt = 0x66644174;
        
    private static final int APNG_RENDER_OP_BLEND_FLAG = 8;
    private static final int APNG_RENDER_OP_SKIP_FRAME = 16;

    private final List chunks = new ArrayList();
    private final List frames = new ArrayList();
    private final Map frameData = new HashMap();

    private boolean animated;
    private int numFrames;
    private int numIterations;

    private int headerChecksum;
    private int paletteChecksum;

    public AnimatedPngImage()
    {
    }

    public AnimatedPngImage(PngConfig config)
    {
        super(config);
    }
    
    public boolean isAnimated()
    {
        return animated;
    }

    public int getNumFrames()
    {
        return animated ? numFrames : 1;
    }

    public int getNumIterations()
    {
        return numIterations;
    }

    public FrameControl getFrame(int index)
    {
        if (index < 0 || index >= numFrames)
            throw new IndexOutOfBoundsException("bad frame " + index);
        return (FrameControl)frames.get(index);
    }

    public BufferedImage[] readAllFrames(File file)
    throws IOException
    {
        BufferedImage first = read(file);
        BufferedImage[] images = new BufferedImage[getNumFrames()];
        images[0] = first;
        for (int i = 1; i < images.length; i++)
            images[i] = readFrame(file, getFrame(i));
        return images;
    }

    public BufferedImage readFrame(File file, FrameControl frame)
    throws IOException
    {
        List data = (List)frameData.get(frame);
        if (data == null)
            throw new IllegalArgumentException("Cannot read data for first APNG frame");
        return ImageFactory.createImage(this,
                                        new FrameDataInputStream(file, data),
                                        frame.getBounds().getSize());
    }

    protected boolean readChunk(int type, DataInput in, long offset, int length)
    throws IOException
    {
        int seq;
        switch (type) {
        case acTl:
            RegisteredChunks.checkLength(type, length, 16);
            numFrames = in.readInt();
            if (numFrames <= 0)
                throw new PngException("APNG has zero frames", false);
            numIterations = in.readInt();
            headerChecksum = in.readInt();
            paletteChecksum = in.readInt();
            return true;
        case fcTl:
            RegisteredChunks.checkLength(type, length, 25);
            seq = in.readInt();
            int w = in.readInt();
            int h = in.readInt();
            Rectangle bounds = new Rectangle(in.readInt(), in.readInt(), w, h);
            int delayNum = in.readUnsignedShort();
            int delayDen = in.readUnsignedShort();
            if (delayDen == 0)
                delayDen = 100;
            int renderOp = in.readByte();

            boolean skip = (renderOp & APNG_RENDER_OP_SKIP_FRAME) != 0;
            boolean blend = (renderOp & APNG_RENDER_OP_BLEND_FLAG) != 0;
            if (blend) {
                switch (getColorType()) {
                case PngConstants.COLOR_TYPE_GRAY:
                case PngConstants.COLOR_TYPE_RGB:
                    throw new PngException("APNG blend not valid for color type " + getColorType(), false);
                }
            }
            int dispose = renderOp & 7;
            if (dispose < 0 || dispose > 2)
                throw new PngException("Unknown APNG dispose op " + dispose, false);
            add(seq, new FrameControl(bounds, (float)delayNum / delayDen, dispose, blend, skip));
            return true;
        case fdAt:
            seq = in.readInt();
            add(seq, new FrameData(offset + 4, length - 4));
            return false; // let PngImage skip it
        default:
            return super.readChunk(type, in, offset, length);
        case PngConstants.IEND:
            validate();
            return true;
        }
    }

    protected BufferedImage createImage(InputStream in)
    throws IOException
    {
        animated = numFrames > 0 &&
            !chunks.isEmpty() &&
            chunks.get(0) instanceof FrameControl;
        return super.createImage(in);
    }

    private void add(int seq, Object chunk)
    {
        // TODO: put limit on sequence number to prevent OOM
        while (chunks.size() <= seq)
            chunks.add(null);
        chunks.set(seq, chunk);
    }

    private void validate()
    throws IOException
    {
        if (!animated)
            return;
        try {
            validateChecksum(headerChecksum, "ihdr_crc", "header");
            if (getColorType() == PngConstants.COLOR_TYPE_PALETTE)
                validateChecksum(paletteChecksum, "plte_crc", "palette");

            FrameControl first = (FrameControl)chunks.get(0);
            Rectangle r = first.getBounds();
            if (r.width != getWidth() || r.height != getHeight())
                throw new PngException("First APNG frame size " + r.width + "x" + r.height +
                                       " should be " + getWidth() + "x" + getHeight(), false);
            if (r.x != 0 || r.y != 0)
                throw new PngException("First APNG frame position " + r.x + "," + r.y +
                                       " should be 0,0", false);
            if (chunks.size() > 1 && !(chunks.get(1) instanceof FrameControl))
                throw new PngException("First APNG frame cannot have frame data", false);

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

            for (int i = 1; i < frames.size(); i++) {
                if (((List)frameData.get(frames.get(i))).isEmpty())
                    throw new PngException("Missing data for frame " + i, false);
            }
        } catch (IOException e) {
            animated = false;
        }
    }

    private void validateChecksum(int apng, String key, String desc)
    throws IOException
    {
        if (apng != ((Number)getProperty(key)).intValue())
            throw new PngException("Bad APNG " + desc + " checksum: 0x" + Integer.toHexString(apng), false);
    }
}
