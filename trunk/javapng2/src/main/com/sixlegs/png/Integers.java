/*
com.sixlegs.image.png - Java package to read and display PNG images
Copyright (C) 1998-2004 Chris Nokleberg

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

class Integers
{
    private Integers()
    {
    }
    
    public static Integer valueOf(int i)
    {
        switch (i) {
        case 0: return INT_0;
        case 1: return INT_1;
        case 2: return INT_2;
        case 3: return INT_3;
        case 4: return INT_4;
        case 5: return INT_5;
        case 6: return INT_6;
        case 7: return INT_7;
        default:
            return new Integer(i);
        }
    }

    private static final Integer INT_0 = new Integer(0);
    private static final Integer INT_1 = new Integer(1);
    private static final Integer INT_2 = new Integer(2);
    private static final Integer INT_3 = new Integer(3);
    private static final Integer INT_4 = new Integer(4);
    private static final Integer INT_5 = new Integer(5);
    private static final Integer INT_6 = new Integer(6);
    private static final Integer INT_7 = new Integer(7);
}
