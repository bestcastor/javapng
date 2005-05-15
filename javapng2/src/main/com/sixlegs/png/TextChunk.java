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

/**
 * Common interface to all PNG text chunk data (tEXt, zTXt, iTXt).
 */
public interface TextChunk
{
    /** 
     * Returns the Latin-1 [ISO-8859-1] encoded keyword
     * of this TextChunk.
     */
    String getKeyword();

    /** 
     * Returns a translation of the keyword into the language
     * used by this TextChunk, or null if unspecified.
     */
    String getTranslatedKeyword();

    /** 
     * Returns the language [RFC-1766] used by the translated 
     * keyword and the text, or null if unspecified.
     */
    String getLanguage();

    /**
     * Returns the text of this TextChunk.
     */
    String getText();
}
