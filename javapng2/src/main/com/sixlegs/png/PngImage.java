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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * TODO
 */
public class PngImage
{
    private PngConfig config;
    private Map props = new HashMap();
    private boolean read = false;

    private static final long SIGNATURE = 0x89504E470D0A1A0AL;

    /**
     * Constructor which uses a new instance of {@link PngConfig}.
     */
    public PngImage()
    {
        this(new PngConfig());
    }

    /**
     * Constructor which uses the specified configuration.
     */
    public PngImage(PngConfig config)
    {
        this.config = config;
    }

    /**
     * TODO
     */
    public PngConfig getConfig()
    {
        return config;
    }
    
    /**
     * TODO
     */
    public BufferedImage read(File file)
    throws IOException
    {
        return read(new BufferedInputStream(new FileInputStream(file)), true);
    }

    /**
     * TODO
     */
    public BufferedImage read(InputStream in, boolean close)
    throws IOException
    {
        BufferedImage image = null;
        StateMachine machine = new StateMachine(this);
        try {
            read = true;
            props.clear();
            PngInputStream pin = new PngInputStream(in);
            long sig = pin.readLong();
            if (sig != SIGNATURE) {
                throw new PngError("Improper signature, expected 0x" +
                                   Long.toHexString(SIGNATURE).toUpperCase() + ", got 0x" +
                                   Long.toHexString(sig).toUpperCase());
            }
            Set seen = new HashSet();
            while (machine.getState() != StateMachine.STATE_END) {
                int type = pin.startChunk(pin.readInt());
                machine.nextState(type);
                if (type == PngChunk.IDAT) {
                    if (config.getReadLimit() == PngConfig.READ_UNTIL_DATA)
                        return null;
                    ImageDataInputStream data = new ImageDataInputStream(pin, machine);
                    image = createImage(data);
                    if (data.read() != -1)
                        pin.skipBytes(pin.getRemaining());
                    type = machine.getType();
                }
                PngChunk chunk = config.getChunk(this, type);
                if (chunk == null) {
                    if (!PngChunk.isAncillary(type))
                        throw new PngError("Critical chunk " + PngChunk.getName(type) + " cannot be skipped");
                    pin.skipBytes(pin.getRemaining());
                } else {
                    try {
                        Integer key = Integers.valueOf(type);
                        if (!chunk.isMultipleOK(type)) {
                            if (seen.contains(key)) {
                                String msg = "Multiple " + PngChunk.getName(type) + " chunks are not allowed";
                                if (PngChunk.isAncillary(type))
                                    throw new PngWarning(msg);
                                throw new PngError(msg);
                            } else {
                                seen.add(key);
                            }
                        }
                        chunk.read(type, pin, this);
                        if (type == PngChunk.IHDR && config.getReadLimit() == PngConfig.READ_HEADER)
                            return null;
                    } catch (PngWarning warning) {
                        pin.skipBytes(pin.getRemaining());
                        config.handleWarning(warning);
                    }
                }
                pin.endChunk(type);
            }
            return image;
        } catch (PngError e) {
            throw e;
        } finally {
            if (close)
                in.close();
        }
    }

    /**
     * TODO
     */
    protected BufferedImage createImage(InputStream in)
    throws IOException
    {
        if (config.getReadLimit() == PngConfig.READ_EXCEPT_DATA)
            return null;
        return ImageFactory.createImage(this, in);
    }

    /**
     * TODO
     */
    protected void handleFrame(BufferedImage image, int framesLeft)
    {
    }

    /** 
     * Returns the image width in pixels.
     */
    public int getWidth()
    {
        return getInt(PngConstants.WIDTH);
    }

    /** 
     * Returns the image height in pixels.
     */
    public int getHeight()
    {
        return getInt(PngConstants.HEIGHT);
    }

    /** 
     * Returns the image bit depth.
     * @return 1, 2, 4, 8, or 16.
     */
    public int getBitDepth()
    {
        return getInt(PngConstants.BIT_DEPTH);
    }

    /**
     * Returns the image interlace type.
     * @return {@link PngConstants#INTERLACE_NONE INTERLACE_NONE}
     *    or {@link PngConstants#INTERLACE_ADAM7 INTERLACE_ADAM7}
     */
    public int getInterlace()
    {
        return getInt(PngConstants.INTERLACE);
    }

    /**
     * Returns the image color type.
     * @return 
     *    {@link PngConstants#COLOR_TYPE_GRAY COLOR_TYPE_GRAY},<br>
     *    {@link PngConstants#COLOR_TYPE_GRAY_ALPHA COLOR_TYPE_GRAY_ALPHA},<br>
     *    {@link PngConstants#COLOR_TYPE_PALETTE COLOR_TYPE_PALETTE},<br>
     *    {@link PngConstants#COLOR_TYPE_RGB COLOR_TYPE_RGB},<br>
     *    or {@link PngConstants#COLOR_TYPE_RGB_ALPHA COLOR_TYPE_RGB_ALPHA}
     */
    public int getColorType()
    {
        return getInt(PngConstants.COLOR_TYPE);
    }

    /**
     * TODO
     */
    public int getSamples()
    {
        switch (getColorType()) {
        case PngConstants.COLOR_TYPE_GRAY_ALPHA: return 2;
        case PngConstants.COLOR_TYPE_RGB:        return 3;
        case PngConstants.COLOR_TYPE_RGB_ALPHA:  return 4;
        }
        return 1;
    }

    /**
     * TODO
     */
    public float getGamma()
    {
        assertRead();
        if (props.containsKey(PngConstants.GAMMA))
            return ((Number)props.get(PngConstants.GAMMA)).floatValue();
        return config.getDefaultGamma();
    }

    /**
     * TODO
     */
    public short[] getGammaTable()
    {
        assertRead();
        double gamma = getGamma();
        int bitDepth = getBitDepth();
        int size = 1 << ((bitDepth == 16 && !config.getReduce16()) ? 16 : 8);
        short[] gammaTable = new short[size];
        double decodingExponent =
            (double)config.getUserExponent() / (gamma * (double)config.getDisplayExponent());
        for (int i = 0; i < size; i++)
            gammaTable[i] = (short)(Math.pow((double)i / (size - 1), decodingExponent) * (size - 1));
        return gammaTable;
    }

    // TODO: gamma-correct background?
    /**
     * TODO
     */
    public Color getBackground()
    {
        assertRead();
        switch (getColorType()) {
        case PngConstants.COLOR_TYPE_PALETTE:
            if (!props.containsKey(PngConstants.BACKGROUND_INDEX))
                return null;
            int index = getInt(PngConstants.BACKGROUND_INDEX);
            byte[] palette = (byte[])props.get(PngConstants.PALETTE);
            return new Color(0xFF & palette[index * 3 + 0], 
                             0xFF & palette[index * 3 + 1], 
                             0xFF & palette[index * 3 + 2]);

        case PngConstants.COLOR_TYPE_GRAY:
        case PngConstants.COLOR_TYPE_GRAY_ALPHA:
            if (!props.containsKey(PngConstants.BACKGROUND_GRAY))
                return null;
            int gray = getInt(PngConstants.BACKGROUND_GRAY) * 255 / ((1 << getBitDepth()) - 1);
            return new Color(gray, gray, gray);
            
        default:
            if (!props.containsKey(PngConstants.BACKGROUND_RGB))
                return null;
            int[] rgb = (int[])props.get(PngConstants.BACKGROUND_RGB);
            if (getBitDepth() == 16) {
                return new Color(rgb[0] >> 8, rgb[1] >> 8, rgb[2] >> 8);
            } else {
                return new Color(rgb[0], rgb[1], rgb[2]);
            }
        }
    }

    /**
     * TODO
     */
    public Object getProperty(String name)
    {
        assertRead();
        return props.get(name);
    }

    /**
     * TODO
     */
    public Map getProperties()
    {
        assertRead();
        return props;
    }

    /**
     * TODO
     */
    public TextChunk getTextChunk(String key)
    {
        List list = (List)getProperty(PngConstants.TEXT_CHUNKS);
        if (key != null && list != null) {
            for (Iterator it = list.iterator(); it.hasNext();) {
                TextChunk chunk = (TextChunk)it.next();
                if (chunk.getKeyword().equals(key))
                    return chunk;
            }
        }
        return null;
    }

    // package-protected
    int getInt(String name)
    {
        assertRead();
        return ((Number)props.get(name)).intValue();
    }

    private void assertRead()
    {
        if (!read)
            throw new IllegalStateException("Image has not been read");
    }
}
