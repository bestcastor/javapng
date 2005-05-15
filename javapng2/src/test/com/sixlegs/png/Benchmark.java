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

import com.sixlegs.png.ext.*;
import java.io.*;

public class Benchmark
{
    public static void main(String[] args)
    throws Exception
    {
        int loop = (args.length > 0) ? Integer.parseInt(args[0]) : 1;
        ExtendedPngConfig config = new ExtendedPngConfig();
        config.setWarningsFatal(true);
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        int count = 0;
        long t = System.currentTimeMillis();
        String line;
        while ((line = r.readLine()) != null) {
            for (int i = 0; i < loop; i++) {
                count++;
                new PngImage(config).read(new File(line));
            }
        }
        t = System.currentTimeMillis() - t;
        System.err.println("Read " + count + " images in " + t + " ms");
    }
}
