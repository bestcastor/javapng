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
class TextChunkImpl
implements TextChunk
{
    private String keyword;
    private String text;
    private String language;
    private String translated;
    
    public TextChunkImpl(String keyword, String text, String language, String translated)
    {
        this.keyword = keyword;
        this.text = text;
        this.language = language;
        this.translated = translated;
    }
    
    public String getKeyword()
    {
        return keyword;
    }

    public String getTranslatedKeyword()
    {
        return translated;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getText()
    {
        return text;
    }
}
