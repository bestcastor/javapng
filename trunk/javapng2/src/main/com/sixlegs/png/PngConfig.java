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

    private int readLimit = READ_ALL;
    private float defaultGamma = 0.45455f;
    private float displayExponent = 2.2f;
    private float userExponent = 1.0f;
    private boolean warningsFatal;
    private boolean progressive;
    private boolean reduce16 = true;
    private boolean gammaCorrect = true;
    
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
     * @see PngImage#handleFrame
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
     *    or {@link #READ_EXCEPT_DATA READ_EXCEPT_DATA}
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
}
