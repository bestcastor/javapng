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

import java.awt.Rectangle;

/**
 * Customizable parameters
 * used by {@link PngImage} when decoding an image.
 */
public class PngConfig
{
    /** Read the entire image */
    public static final int READ_ALL = 0;
    /** Read only the header chunk */
    public static final int READ_HEADER = 1;
    /** Read all the metadata up to the image data */
    public static final int READ_UNTIL_DATA = 2;
    /** Read the entire image, skipping over the image data */
    public static final int READ_EXCEPT_DATA = 3;
    /** Read the entire image, skipping over all non-critical chunks except tRNS and gAMA */
    public static final int READ_EXCEPT_METADATA = 4;

    private int readLimit = READ_ALL;
    private float defaultGamma = 0.45455f;
    private float displayExponent = 2.2f;
    private float userExponent = 1.0f;
    private boolean warningsFatal;
    private boolean progressive;
    private boolean reduce16 = true;
    private boolean gammaCorrect = true;
    private int sourceXSubsampling = 1;
    private int sourceYSubsampling = 1;
    private int subsamplingXOffset = 0;
    private int subsamplingYOffset = 0;
    private int minPass = 0;
    private int numPasses = Integer.MAX_VALUE;
    private Rectangle sourceRegion;

    /**
     * Create a new instance with default parameter values.
     */
    public PngConfig()
    {
    }

    /**
     * Create a copy of the given instance.
     * @param copy the configuration object to copy
     */
    public PngConfig(PngConfig copy)
    {
        this.readLimit = copy.readLimit;
        this.defaultGamma = copy.defaultGamma;
        this.displayExponent = copy.displayExponent;
        this.userExponent = copy.userExponent;
        this.warningsFatal = copy.warningsFatal;
        this.progressive = copy.progressive;
        this.reduce16 = copy.reduce16;
        this.gammaCorrect = copy.gammaCorrect;

        this.sourceXSubsampling = copy.sourceXSubsampling;
        this.sourceYSubsampling = copy.sourceYSubsampling;
        this.subsamplingXOffset = copy.subsamplingXOffset;
        this.subsamplingYOffset = copy.subsamplingYOffset;
        this.minPass = copy.minPass;
        this.numPasses = copy.numPasses;
    }

    /**
     * @see javax.imageio.IIOParam#setSourceRegion
     */
    public void setSourceRegion(Rectangle sourceRegion)
    {
        if (sourceRegion == null) {
            this.sourceRegion = null;
        } else {
            this.sourceRegion = new Rectangle(sourceRegion);
        }
    }

    /**
     * <i>(Documentation copied from {@link javax.imageio.IIOParam#getSourceRegion})</i>
     * Returns the source region to be used.  The returned value is
     * that set by the most recent call to
     * <code>setSourceRegion</code>, and will be <code>null</code> if
     * there is no region set.
     * 
     * @return the source region of interest as a
     * <code>Rectangle</code>, or <code>null</code>.
     *
     * @see #setSourceRegion
     */
    public Rectangle getSourceRegion() {
        return (sourceRegion != null) ? new Rectangle(sourceRegion) : null;
    }

    /**
     * Returns the current 16-bit reduction setting.
     * @see #setReduce16
     */
    public boolean getReduce16()
    {
        return reduce16;
    }

    /**
     * Enables or disables 16-bit reduction. If enabled, 16-bit samples are reduced to 8-bit samples by
     * shifting to the right by 8 bits. Default is <i>true</i>.
     * @param reduce16 enable 16-bit reduction
     */
    public void setReduce16(boolean reduce16)
    {
        this.reduce16 = reduce16;
    }

    /**
     * Returns the current default gamma value.
     * @see #setDefaultGamma
     */
    public float getDefaultGamma()
    {
        return defaultGamma;
    }

    /**
     * Sets the default gamma value. This value is used unless the image
     * contains an explicit gamma value. Initial value is <i>1/45455</i>.
     * @param defaultGamma the default gamma value
     */
    public void setDefaultGamma(float defaultGamma)
    {
        this.defaultGamma = defaultGamma;
    }
    
    /**
     * Returns the current gamma correction setting.
     * @see #setGammaCorrect
     */
    public boolean getGammaCorrect()
    {
        return gammaCorrect;
    }

    /**
     * Enables or disables gamma correction. If enabled, decoded images will be gamma corrected.
     * Sets to false if your application will perform gamma correctly manually.
     * Default is <i>true</i>.
     * @param gammaCorrect use gamma correction
     * @see PngImage#getGamma
     * @see PngImage#getGammaTable
     */
    public void setGammaCorrect(boolean gammaCorrect)
    {
        this.gammaCorrect = gammaCorrect;
    }

    /**
     * Returns the current progressive display setting.
     * @see #setProgressive
     */
    public boolean getProgressive()
    {
        return progressive;
    }

    /**
     * Enables or disables progressive display for interlaced images.
     * If enabled, each received pixel is expanded (replicated) to fill a rectangle
     * covering the yet-to-be-transmitted pixel positions below and to the right
     * of the received pixel. This produces a "fade-in" effect as the new image
     * gradually replaces the old, at the cost of some additional processing time.
     * Default is <i>false</i>.
     * @param progressive use progressive display
     * @see PngImage#handlePass
     */
    public void setProgressive(boolean progressive)
    {
        this.progressive = progressive;
    }

    /**
     * Returns the current display exponent.
     * @see #setDisplayExponent
     */
    public float getDisplayExponent()
    {
        return displayExponent;
    }

    /**
     * Sets the default display exponent. The proper setting depends on monitor and OS gamma lookup
     * table settings, if any. The default value of <i>2.2</i> should
     * work well with most PC displays. If the operating system has
     * a gamma lookup table (e.g. Macintosh) the display exponent should be lower.
     * @param displayExponent the display exponent
     */
    public void setDisplayExponent(float displayExponent)
    {
        this.displayExponent = displayExponent;
    }
    
    /**
     * Returns the current user exponent.
     * @see #setUserExponent
     */
    public float getUserExponent()
    {
        return userExponent;
    }

    /**
     * Sets the user gamma exponent. The proper setting depends on the user's
     * particular viewing conditions. Use an exponent greater than 1.0 to darken the mid-level
     * tones, or less than 1.0 to lighten them. Default is <i>1.0</i>.
     * @param userExponent the user exponent
     */
    public void setUserExponent(float userExponent)
    {
        this.userExponent = userExponent;
    }

    /**
     * Returns the current read limit setting.
     * @see #setReadLimit
     */
    public int getReadLimit()
    {
        return readLimit;
    }

    /**
     * Configures how much of the image to read. Useful when one is interested
     * in only a portion of the image metadata, and would like to avoid
     * reading and/or decoding the actual image data.
     * @param readLimit
     *    {@link #READ_ALL READ_ALL},<br>
     *    {@link #READ_HEADER READ_HEADER},<br>
     *    {@link #READ_UNTIL_DATA READ_UNTIL_DATA},<br>
     *    {@link #READ_EXCEPT_DATA READ_EXCEPT_DATA},<br>
     *    or {@link #READ_EXCEPT_METADATA READ_EXCEPT_METADATA}
     */
    public void setReadLimit(int readLimit)
    {
        this.readLimit = readLimit;
    }

    /**
     * Returns whether warnings are treated as fatal errors.
     * @see #setWarningsFatal
     */
    public boolean getWarningsFatal()
    {
        return warningsFatal;
    }

    /**
     * Configures whether warnings should be treated as fatal errors.
     * All {@link PngWarning} exceptions are caught and passed to the {@link PngImage#handleWarning}
     * method. If warnings are configured as fatal, that method will re-throw the
     * exception, which will abort image processing. Default is <i>false</i>.
     * @param warningsFatal true if warnings should be treated as fatal errors
     * @see PngImage#handleWarning
     */
    public void setWarningsFatal(boolean warningsFatal)
    {
        this.warningsFatal = warningsFatal;
    }

    /**
     * <i>(Documentation copied from {@link javax.imageio.IIOParam#getSourceXSubsampling})</i>
     * Returns the number of source columns to advance for each pixel.
     *
     * <p>If {@code setSourceSubsampling} has not been called, 1
     * is returned (which is the correct value).
     *
     * @return the source subsampling X period.
     *
     * @see #setSourceSubsampling
     * @see #getSourceYSubsampling
     */
    public int getSourceXSubsampling()
    {
        return sourceXSubsampling;
    }
    
    /**
     * <i>(Documentation copied from {@link javax.imageio.IIOParam#getSourceYSubsampling})</i>
     * Returns the number of rows to advance for each pixel.
     *
     * <p>If {@code setSourceSubsampling} has not been called, 1
     * is returned (which is the correct value).
     *
     * @return the source subsampling Y period.
     *
     * @see #setSourceSubsampling
     * @see #getSourceXSubsampling
     */
    public int getSourceYSubsampling()
    {
        return sourceYSubsampling;
    }
    
    /**
     * <i>(Documentation copied from {@link javax.imageio.IIOParam#getSubsamplingXOffset})</i>
     * Returns the horizontal offset of the subsampling grid.
     *
     * <p>If {@code setSourceSubsampling} has not been called, 0
     * is returned (which is the correct value).
     *
     * @return the source subsampling grid X offset.
     *
     * @see #setSourceSubsampling
     * @see #getSubsamplingYOffset
     */
    public int getSubsamplingXOffset()
    {
        return subsamplingXOffset;
    }
    
    /**
     * <i>(Documentation copied from {@link javax.imageio.IIOParam#getSubsamplingYOffset})</i>
     * Returns the vertical offset of the subsampling grid.
     *
     * <p>If {@code setSourceSubsampling} has not been called, 0
     * is returned (which is the correct value).
     *
     * @return the source subsampling grid Y offset.
     *
     * @see #setSourceSubsampling
     * @see #getSubsamplingXOffset
     */
    public int getSubsamplingYOffset()
    {
        return subsamplingYOffset;
    }
    
    /**
     * <i>(Documentation copied from {@link javax.imageio.IIOParam#setSourceSubsampling})</i>
     * Specifies a decimation subsampling to apply on I/O.  The
     * {@code sourceXSubsampling} and
     * {@code sourceYSubsampling} parameters specify the
     * subsampling period (<i>i.e.</i>, the number of rows and columns
     * to advance after every source pixel).  Specifically, a period of
     * 1 will use every row or column; a period of 2 will use every
     * other row or column.  The {@code subsamplingXOffset} and
     * {@code subsamplingYOffset} parameters specify an offset
     * from the image origin for the first subsampled pixel.
     * Adjusting the origin of the subsample grid is useful for avoiding
     * seams when subsampling a very large source image into destination
     * regions that will be assembled into a complete subsampled image.
     * Most users will want to simply leave these parameters at 0.
     *
     * <p> The number of pixels and scanlines to be used are calculated
     * as follows.
     * <p>
     * The number of subsampled pixels in a scanline is given by
     * <p>
     * {@code truncate[(width - subsamplingXOffset + sourceXSubsampling - 1)
     * / sourceXSubsampling]}.
     * <p>
     * The number of scanlines to be used can be computed similarly.
     *
     * <p> There is no {@code unsetSourceSubsampling} method;
     * simply call {@code setSourceSubsampling(1, 1, 0, 0)} to
     * restore default values.
     *
     * @param sourceXSubsampling the number of columns to advance
     * between pixels.
     * @param sourceYSubsampling the number of rows to advance between
     * pixels.
     * @param subsamplingXOffset the horizontal offset of the first subsample
     * within the region, or within the image if no region is set.
     * @param subsamplingYOffset the horizontal offset of the first subsample
     * within the region, or within the image if no region is set.
     * @throws IllegalArgumentException - if either period is negative
     * or 0, or if either grid offset is negative or greater than
     * or equal to the corresponding period.
     */
    public void setSourceSubsampling(int sourceXSubsampling, int sourceYSubsampling, int subsamplingXOffset, int subsamplingYOffset)
    {
        if (sourceXSubsampling <= 0 || sourceYSubsampling <= 0)
            throw new IllegalArgumentException("Periods must be positive");
        if (subsamplingXOffset < 0 || subsamplingXOffset >= sourceXSubsampling)
            throw new IllegalArgumentException("X offset out of range");
        if (subsamplingYOffset < 0 || subsamplingYOffset >= sourceYSubsampling)
            throw new IllegalArgumentException("Y offset out of range");
        this.sourceXSubsampling = sourceXSubsampling;
        this.sourceYSubsampling = sourceYSubsampling;
        this.subsamplingXOffset = subsamplingXOffset;
        this.subsamplingYOffset = subsamplingYOffset;
    }

    /**
     * <i>(Documentation copied from {@link javax.imageio.ImageReadParam#setSourceProgressivePasses})</i>
     * Sets the range of progressive passes that will be decoded.
     * Passes outside of this range will be ignored.
     *
     * <p> In the PNG format, images may be interlaced using the Adam7 algorithm,
     * which results in seven progressive passes. Thus if {@code minPass + numPasses - 1} is
     * larger than the index of the last available passes, decoding
     * will end with that pass.
     *
     * <p> A value of {@code numPasses} of
     * {@code Integer.MAX_VALUE} indicates that all passes from
     * {@code minPass} forward should be read.  Otherwise, the
     * index of the last pass (<i>i.e.</i>, {@code minPass + numPasses
     * - 1}) must not exceed {@code Integer.MAX_VALUE}.
     *
     * <p> There is no {@code unsetSourceProgressivePasses}
     * method; the same effect may be obtained by calling
     * {@code setSourceProgressivePasses(0, Integer.MAX_VALUE)}.
     *
     * @param minPass the index of the first pass to be decoded.
     * @param numPasses the maximum number of passes to be decoded.
     *
     * @exception IllegalArgumentException if {@code minPass} is
     * negative, {@code numPasses} is negative or 0, or
     * {@code numPasses} is smaller than
     * {@code Integer.MAX_VALUE} but {@code minPass +
     * numPasses - 1} is greater than
     * {@code INTEGER.MAX_VALUE}.
     *
     * @see #getSourceMinProgressivePass
     * @see #getSourceMaxProgressivePass
     */
    public void setSourceProgressivePasses(int minPass, int numPasses)
    {
        if (minPass < 0)
            throw new IllegalArgumentException("minPass < 0");
        if (numPasses <= 0)
            throw new IllegalArgumentException("numPasses <= 0");
        if ((numPasses != Integer.MAX_VALUE) && (((minPass + numPasses - 1) & 0x80000000) != 0))
            throw new IllegalArgumentException("minPass + numPasses - 1 > INTEGER.MAX_VALUE!");
        this.minPass = minPass;
        this.numPasses = numPasses;
    }
    
    /**
     * <i>(Documentation copied from {@link javax.imageio.ImageReadParam#getSourceMinProgressivePass})</i>
     * Returns the index of the first progressive pass that will be
     * decoded. If no value has been set, 0 will be returned (which is
     * the correct value).
     *
     * @return the index of the first pass that will be decoded.
     *
     * @see #setSourceProgressivePasses
     * @see #getSourceNumProgressivePasses
     */
    public int getSourceMinProgressivePass()
    {
        return minPass;
    }
    
    /**
     * <i>(Documentation copied from {@link javax.imageio.ImageReadParam#getSourceNumProgressivePasses})</i>
     * Returns the number of the progressive passes that will be
     * decoded. If no value has been set,
     * {@code Integer.MAX_VALUE} will be returned (which is the
     * correct value).
     *
     * @return the number of the passes that will be decoded.
     *
     * @see #setSourceProgressivePasses
     * @see #getSourceMinProgressivePass
     */
    public int getSourceNumProgressivePasses()
    {
        return numPasses;
    }

    /**
     * <i>(Documentation copied from {@link javax.imageio.ImageReadParam#getSourceMaxProgressivePass})</i>
     * If {@code getSourceNumProgressivePasses} is equal to
     * {@code Integer.MAX_VALUE}, returns
     * {@code Integer.MAX_VALUE}.  Otherwise, returns
     * {@code getSourceMinProgressivePass() +
     * getSourceNumProgressivePasses() - 1}.
     *
     * @return the index of the last pass to be read, or
     * {@code Integer.MAX_VALUE}.
     */
    public int getSourceMaxProgressivePass()
    {
        if (numPasses == Integer.MAX_VALUE)
            return numPasses;
        return minPass + numPasses - 1;
    }
}
