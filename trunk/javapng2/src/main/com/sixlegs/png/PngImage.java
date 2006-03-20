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

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * A class to decode PNG images.
 * The simplest use is if only a decoded {@link BufferedImage} is required:
 * <pre>BufferedImage image = new PngImage().read(new java.io.File("test.png"));</pre>
 * The {@code PngImage} instance used to read the image also stores all of the
 * image metadata. For customized PNG decoding, a {@link PngConfig} object
 * may be passed to the {@linkplain #PngImage(PngConfig) constructor}.
 * <p>
 * For more information visit <a href="http://www.sixlegs.com/">http://www.sixlegs.com/</a>
 * @author Chris Nokleberg <a href="mailto:chris@sixlegs.com">&lt;chris@sixlegs.com&gt;</a>
 * @see PngConfig
 */
public class PngImage
implements Transparency
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
     * Returns the configuration used by this object.
     * @return the {@code PngConfig} instance used by this object
     */
    public PngConfig getConfig()
    {
        return config;
    }
    
    /**
     * Reads a PNG image from the specified file. Image metadata will
     * be stored in the property map of this {@code PngImage} instance,
     * for retrieval via the various helper methods ({@link #getWidth}, {@link #getHeight}, etc.)
     * and {@link #getProperty}. The decoded image itself is returned by this
     * method but not cached.
     * <p>
     * If {@link PngConfig#getReadLimit} is anything but {@link PngConfig#READ_ALL},
     * then this method will return null instead of the decoded image.
     * <p>
     * Multiple images can be read using the same {@code PngImage} instance.
     * The property map is cleared each time this method is called.
     * This method is not thread-safe.
     * @param file the file to read
     * @return the decoded image, or null if no image was decoded
     * @throws IOException if any error occurred while reading the image
     * @see #read(java.io.InputStream, boolean)
     * @see #createImage
     * @see #handlePass
     */
    public BufferedImage read(File file)
    throws IOException
    {
        return read(new BufferedInputStream(new FileInputStream(file)), true);
    }

    /**
     * Reads a PNG image from the specified input stream. Image metadata will
     * be stored in the property map of this {@code PngImage} instance,
     * for retrieval via the various helper methods ({@link #getWidth}, {@link #getHeight}, etc.)
     * and {@link #getProperty}. The decoded image itself is returned by this
     * method but not cached.
     * <p>
     * If {@link PngConfig#getReadLimit} is anything but {@link PngConfig#READ_ALL},
     * then this method will return null instead of the decoded image.
     * <p>
     * Multiple images can be read using the same {@code PngImage} instance.
     * The property map is cleared each time this method is called.
     * This method is not thread-safe.
     * @param in the input stream to read
     * @param close whether to close the input stream after reading
     * @return the decoded image, or null if no image was decoded
     * @throws IOException if any error occurred while reading the image
     * @see #read(java.io.File)
     * @see #createImage
     * @see #handlePass
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
                        new DataInputStream(data).skipBytes(pin.getRemaining());
                    type = machine.getType();
                }
                PngChunk chunk = getChunk(type);
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
                        handleWarning(warning);
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
     * A hook by which subclasses can access or manipulate the raw image data.
     * All of the raw, compressed image data contained in the {@code IDAT} chunks
     * of the PNG image being read is concatenated and passed to this method
     * as a single input stream. The returned image will become the return value
     * of the calling {@link #read(java.io.File)} or {@link #read(java.io.InputStream, boolean)}
     * method.
     * <p>
     * The default implementation is to decode the image into a {@link java.awt.image.BufferedImage}
     * as long as {@link PngConfig#getReadLimit} does not equal {@link PngConfig#READ_EXCEPT_DATA}.
     * <p>
     * Unlike {@link PngChunk} implementations, subclasses do not have to read exactly
     * the correct amount from this stream.
     * @param in the input stream of raw, compressed image data
     * @return the decoded image, or null
     * @throws IOException if any error occurred while processing the image data
     */
    protected BufferedImage createImage(InputStream in)
    throws IOException
    {
        if (config.getReadLimit() == PngConfig.READ_EXCEPT_DATA)
            return null;
        return ImageFactory.createImage(this, in);
    }

    /**
     * A method which subclasses may override to take some action
     * after each pass has been decoded. An interlaced image has seven
     * passes, and non-interlaced image only one. The {@code pass}
     * arguments indicates the index of the completed
     * pass, starting with zero.
     * <p>
     * For interlaced images, the state of the image data before the last
     * pass is affected by the value of {@link PngConfig#getProgressive}.
     * <p>
     * Image decoding can be aborted by returning false. The default
     * implementation returns false if the {@code pass} parameter
     * is greater than the value of {@link PngConfig#getSourceMaxProgressivePass}.
     * @param image the partially or fully decoded image
     * @param pass the index of the completed pass
     * @return false to abort image decoding
     */
    protected boolean handlePass(BufferedImage image, int pass)
    {
        return pass <= config.getSourceMaxProgressivePass();
    }

    /**
     * Reports the approximate degree of completion of the current read
     * call. This method is called periodically during
     * image decoding. The degree of completion is expressed as a percentage
     * varying from 0.0F to 100.0F, and is calculated using the number
     * of pixels decoded. 
     * <p>
     * Image decoding can be aborted by returning false. The default
     * implementation returns true.
     * @param image the partially or fully decoded image
     * @param pct the approximate percentage of decoding that has been completed
     * @return false to abort image decoding
     */
    protected boolean handleProgress(BufferedImage image, float pct)
    {
        return true;
    }

    /**
     * Callback for customized handling of warnings. Whenever a
     * non-fatal error is found, an instance of {@link PngWarning} is
     * created and passed to this method. To signal that the exception
     * should be treated as a fatal exception (and abort image
     * processing), an implementation should re-throw the exception.
     * <p>
     * By default, this method will re-throw the warning if the
     * {@link PngConfig#setWarningsFatal warningsFatal} property is true.
     * @throws PngWarning if the warning should be treated as fatal
     */
    protected void handleWarning(PngWarning e)
    throws PngWarning
    {
        if (config.getWarningsFatal())
            throw e;
    }
    
    /** 
     * Returns the image width in pixels.
     * @throws IllegalStateException if an image has not been read
     */
    public int getWidth()
    {
        return getInt(PngConstants.WIDTH);
    }

    /** 
     * Returns the image height in pixels.
     * @throws IllegalStateException if an image has not been read
     */
    public int getHeight()
    {
        return getInt(PngConstants.HEIGHT);
    }

    /** 
     * Returns the image bit depth.
     * @return 1, 2, 4, 8, or 16
     * @throws IllegalStateException if an image has not been read
     */
    public int getBitDepth()
    {
        return getInt(PngConstants.BIT_DEPTH);
    }

    /**
     * Returns true if the image interlace type ({@link PngConstants#INTERLACE})
     * is something other than {@link PngConstants#INTERLACE_NONE INTERLACE_NONE}.
     * @return true if the image is interlaced
     * @throws IllegalStateException if an image has not been read
     */
    public boolean isInterlaced()
    {
        return getInt(PngConstants.INTERLACE) != PngConstants.INTERLACE_NONE;
    }

    /**
     * Returns the image color type.
     * @return 
     *    {@link PngConstants#COLOR_TYPE_GRAY COLOR_TYPE_GRAY},<br>
     *    {@link PngConstants#COLOR_TYPE_GRAY_ALPHA COLOR_TYPE_GRAY_ALPHA},<br>
     *    {@link PngConstants#COLOR_TYPE_PALETTE COLOR_TYPE_PALETTE},<br>
     *    {@link PngConstants#COLOR_TYPE_RGB COLOR_TYPE_RGB},<br>
     *    or {@link PngConstants#COLOR_TYPE_RGB_ALPHA COLOR_TYPE_RGB_ALPHA}
     * @throws IllegalStateException if an image has not been read
     */
    public int getColorType()
    {
        return getInt(PngConstants.COLOR_TYPE);
    }

    /**
     * Returns the type of this Transparency.
     * @return the field type of this Transparency, which is either OPAQUE, BITMASK or TRANSLUCENT.
     * @throws IllegalStateException if an image has not been read
     */
    public int getTransparency()
    {
        int colorType = getColorType();
        return (colorType == PngConstants.COLOR_TYPE_RGB_ALPHA ||
                colorType == PngConstants.COLOR_TYPE_GRAY_ALPHA ||
                props.containsKey(PngConstants.TRANSPARENCY) ||
                props.containsKey(PngConstants.PALETTE_ALPHA)) ?
            TRANSLUCENT : OPAQUE;
    }

    /**
     * Returns the number of samples per pixel. Gray and paletted
     * images use one sample, gray+alpha uses two, RGB uses three,
     * and RGB+alpha uses four.
     * @return 1, 2, 3 or 4
     * @throws IllegalStateException if an image has not been read
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
     * Returns the gamma exponent that was explicitly encoded in the image,
     * if there was one, or the value of {@link PngConfig#getDefaultGamma} otherwise.
     * @return the gamma exponent
     * @throws IllegalStateException if an image has not been read
     */
    public float getGamma()
    {
        assertRead();
        if (props.containsKey(PngConstants.GAMMA))
            return ((Number)props.get(PngConstants.GAMMA)).floatValue();
        return config.getDefaultGamma();
    }

    /**
     * Returns a gamma table which can be used for custom gamma correction.
     * The size of the table is 2 to the power of {@link #getBitDepth}, unless
     * the bit depth is 16 and {@link PngConfig#getReduce16} is true, in which
     * case the table is 256 entries.
     * <p>
     * The values in the table take into account {@link #getGamma},
     * {@link PngConfig#getDisplayExponent}, and {@link PngConfig#getUserExponent}.
     * @return a table of component values to be used in gamma correction
     * @throws IllegalStateException if an image has not been read
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
     * Returns the background color explicitly encoded in the image.
     * For 16-bit images the components are reduced to 8-bit by shifting.
     * @return the background color, or null
     * @throws IllegalStateException if an image has not been read
     */
    public Color getBackground()
    {
        assertRead();
        int[] background = (int[])props.get(PngConstants.BACKGROUND);
        if (background == null)
            return null;
        switch (getColorType()) {
        case PngConstants.COLOR_TYPE_PALETTE:
            byte[] palette = (byte[])props.get(PngConstants.PALETTE);
            int index = background[0] * 3;
            return new Color(0xFF & palette[index + 0], 
                             0xFF & palette[index + 1], 
                             0xFF & palette[index + 2]);
        case PngConstants.COLOR_TYPE_GRAY:
        case PngConstants.COLOR_TYPE_GRAY_ALPHA:
            int gray = background[0] * 255 / ((1 << getBitDepth()) - 1);
            return new Color(gray, gray, gray);
        default:
            if (getBitDepth() == 16) {
                return new Color(background[0] >> 8, background[1] >> 8, background[2] >> 8);
            } else {
                return new Color(background[0], background[1], background[2]);
            }
        }
    }

    /**
     * Returns a per-image property by name. All common property names are defined in
     * {@link PngConstants}; their types are listed in the following table.
     * The use of the various helper methods defined in this class, such as {@link #getBackground},
     * is normally preferrable to working with the raw property values.
     * <p>
     * <center><table border=1 cellspacing=0 cellpadding=4 width="80%">
     * <tr bgcolor="#E0E0E0"><td nowrap><b>Property</b></td><td nowrap><b>Type</b></td>
     * <td><b>Description</b></td></tr>
     * <tr><td>{@link PngConstants#BIT_DEPTH BIT_DEPTH}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Bit depth</td></tr>
     * <tr><td>{@link PngConstants#COLOR_TYPE COLOR_TYPE}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Color type</td></tr>
     * <tr><td>{@link PngConstants#COMPRESSION COMPRESSION}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Compression method</td></tr>
     * <tr><td>{@link PngConstants#FILTER FILTER}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Filter method</td></tr>
     * <tr><td>{@link PngConstants#GAMMA GAMMA}</td>
     * <td>{@link Float Float}</td>
     * <td>Gamma</td></tr>
     * <tr><td>{@link PngConstants#WIDTH WIDTH}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Width</td></tr>
     * <tr><td>{@link PngConstants#HEIGHT HEIGHT}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Height</td></tr>
     * <tr><td>{@link PngConstants#INTERLACE INTERLACE}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Interlace method</td></tr>
     * <tr><td>{@link PngConstants#PALETTE PALETTE}</td>
     * <td>{@code byte[]}</td>
     * <td>Palette entries</td></tr>
     * <tr><td>{@link PngConstants#PALETTE_ALPHA PALETTE_ALPHA}</td>
     * <td>{@code byte[]}</td>
     * <td>Palette alpha</td></tr>
     * <tr><td>{@link PngConstants#TRANSPARENCY TRANSPARENCY}</td>
     * <td>{@code int[]}</td>
     * <td>Transparency samples</td></tr>
     * <tr><td>{@link PngConstants#BACKGROUND BACKGROUND}</td>
     * <td>{@code int[]}</td>
     * <td>Background samples</td></tr>
     * <tr><td>{@link PngConstants#PIXELS_PER_UNIT_X PIXELS_PER_UNIT_X}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Pixels per unit, X axis</td></tr>
     * <tr><td>{@link PngConstants#PIXELS_PER_UNIT_Y PIXELS_PER_UNIT_Y}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Pixels per unit, Y axis</td></tr>
     * <tr><td>{@link PngConstants#UNIT UNIT}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Unit specifier</td></tr>
     * <tr><td>{@link PngConstants#RENDERING_INTENT RENDERING_INTENT}</td>
     * <td>{@link Integer Integer}</td>
     * <td>Rendering intent</td></tr>
     * <tr><td>{@link PngConstants#SIGNIFICANT_BITS SIGNIFICANT_BITS}</td>
     * <td>{@code byte[]}</td>
     * <td>Significant bits</td></tr>
     * <tr><td>{@link PngConstants#TEXT_CHUNKS TEXT_CHUNKS}</td>
     * <td>{@link java.util.List List}</td>
     * <td>List of {@linkplain TextChunk text chunks}</td></tr>
     * <tr><td>{@link PngConstants#TIME TIME}</td>
     * <td>{@link java.util.Date Date}</td>
     * <td>Image last-modification time</td></tr>
     * <tr><td>{@link PngConstants#CHROMATICITY CHROMATICITY}</td>
     * <td>{@code float[]}</td>
     * <td>Chromaticity</td></tr>
     * <tr><td>{@link PngConstants#ICC_PROFILE ICC_PROFILE}</td>
     * <td>{@code byte[]}</td>
     * <td>ICC profile</td></tr>
     * <tr><td>{@link PngConstants#ICC_PROFILE_NAME ICC_PROFILE_NAME}</td>
     * <td>{@link String String}</td>
     * <td>ICC profile name</td></tr>
     * <tr><td>{@link PngConstants#HISTOGRAM HISTOGRAM}</td>
     * <td>{@code int[]}</td>
     * <td>Palette histogram</td></tr>
     * <tr><td>{@link PngConstants#SUGGESTED_PALETTES SUGGESTED_PALETTES}</td>
     * <td>{@link java.util.List List}</td>
     * <td>List of {@linkplain SuggestedPalette suggested palettes}</td></tr>
     * </table></center>
     * @param name a property name
     * @return the property value, or null if no such property exists
     * @throws IllegalStateException if an image has not been read
     */
    public Object getProperty(String name)
    {
        assertRead();
        return props.get(name);
    }

    /**
     * Returns the map which stores all of this image's property values.
     * The map is mutable, and storing a value with the wrong type may
     * result in other methods in this class throwing a {@code ClassCastException}.
     * This method is primarily meant for {@link PngChunk} implementations
     * to store the properties they are responsible for reading.
     * @return the mutable map of image properties
     * @throws IllegalStateException if an image has not been read
     */
    public Map getProperties()
    {
        assertRead();
        return props;
    }

    /**
     * Returns a text chunk that uses the given keyword, if one exists.
     * If multiple text chunks share the same keyword, this method
     * will return the first one that was read. The full list of text
     * chunks may be accessed by calling
     * <pre>{@linkplain #getProperty getProperty}({@linkplain PngConstants#TEXT_CHUNKS})</pre>
     * @param key the text chunk keyword
     * @return a {@link TextChunk} implementation, or null
     * @throws IllegalStateException if an image has not been read
     */
    public TextChunk getTextChunk(String key)
    {
        assertRead();
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

    private static final PngChunk IHDR = loadChunk("com.sixlegs.png.Chunk_IHDR");
    private static final PngChunk PLTE = loadChunk("com.sixlegs.png.Chunk_PLTE");
    private static final PngChunk IEND = loadChunk("com.sixlegs.png.Chunk_IEND");
    private static final PngChunk bKGD = loadChunk("com.sixlegs.png.Chunk_bKGD");
    private static final PngChunk cHRM = loadChunk("com.sixlegs.png.Chunk_cHRM");
    private static final PngChunk gAMA = loadChunk("com.sixlegs.png.Chunk_gAMA");
    private static final PngChunk pHYs = loadChunk("com.sixlegs.png.Chunk_pHYs");
    private static final PngChunk sBIT = loadChunk("com.sixlegs.png.Chunk_sBIT");
    private static final PngChunk sRGB = loadChunk("com.sixlegs.png.Chunk_sRGB");
    private static final PngChunk tIME = loadChunk("com.sixlegs.png.Chunk_tIME");
    private static final PngChunk tRNS = loadChunk("com.sixlegs.png.Chunk_tRNS");
    private static final PngChunk hIST = loadChunk("com.sixlegs.png.Chunk_hIST");
    private static final PngChunk iCCP = loadChunk("com.sixlegs.png.Chunk_iCCP");
    private static final PngChunk sPLT = loadChunk("com.sixlegs.png.Chunk_sPLT");
    private static final PngChunk text = loadChunk("com.sixlegs.png.TextChunkReader");

    private static final PngChunk gIFg = loadChunk("com.sixlegs.png.Chunk_gIFg");
    private static final PngChunk gIFx = loadChunk("com.sixlegs.png.Chunk_gIFx");
    private static final PngChunk oFFs = loadChunk("com.sixlegs.png.Chunk_oFFs");
    private static final PngChunk pCAL = loadChunk("com.sixlegs.png.Chunk_pCAL");
    private static final PngChunk sCAL = loadChunk("com.sixlegs.png.Chunk_sCAL");
    private static final PngChunk sTER = loadChunk("com.sixlegs.png.Chunk_sTER");

    private static PngChunk loadChunk(String className)
    {
        try {
            return (PngChunk)Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new Error(e.getMessage());
        } catch (InstantiationException e) {
            throw new Error(e.getMessage());
        }
    }

    /**
     * Returns a {@link PngChunk} implementation for the given chunk type.
     * The returned chunk object will be responsible for reading the
     * binary chunk data and populating the property map of this {@code PngImage}
     * as appropriate. If {@code null} is returned, the chunk is skipped.
     * Note that skipping certain critical chunks will guarantee an eventual
     * exception.
     * <p>
     * {@code IDAT} chunks are not processed by this method. See {@link #createImage}
     * for custom handling of the raw image data.
     * <p>
     * By default this method will return a {@code PngChunk} implementation
     * for all of the chunk types defined in Version 1.2 of the PNG Specification
     * (except {@code IDAT}).
     * @param type the chunk type
     * @return an instance of {@code PngChunk} which will read the following chunk data, or null
     * @throws IllegalArgumentException if the type is IDAT
     */
    protected PngChunk getChunk(int type)
    {
        switch (type) {
        case PngChunk.IHDR: return IHDR;
        case PngChunk.PLTE: return PLTE;
        case PngChunk.IEND: return IEND;
        case PngChunk.bKGD: return bKGD;
        case PngChunk.cHRM: return cHRM;
        case PngChunk.gAMA: return gAMA;
        case PngChunk.pHYs: return pHYs;
        case PngChunk.sBIT: return sBIT;
        case PngChunk.sRGB: return sRGB;
        case PngChunk.tIME: return tIME;
        case PngChunk.tRNS: return tRNS;
        case PngChunk.hIST: return hIST;
        case PngChunk.iCCP: return iCCP;
        case PngChunk.sPLT: return sPLT;
        case PngChunk.gIFg: return gIFg;
        case PngChunk.gIFx: return gIFx;
        case PngChunk.oFFs: return oFFs;
        case PngChunk.pCAL: return pCAL;
        case PngChunk.sCAL: return sCAL;
        case PngChunk.sTER: return sTER;
        case PngChunk.iTXt:
        case PngChunk.tEXt:
        case PngChunk.zTXt:
            return text;
        case PngChunk.IDAT:
            throw new IllegalArgumentException("Unexpected IDAT chunk");
        }
        return null;
    }
}
