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

package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;

public class ExtractFrames
{
    public static void main(String[] args)
    throws Exception
    {
        PngConfig config = new PngConfig.Builder().warningsFatal(true).build();
        AnimatedPngImage apng = new AnimatedPngImage(config);
        DecimalFormat fmt = new DecimalFormat("000");
        for (int i = 0; i < args.length; i++) {
            File in = new File(args[i]);
            BufferedImage[] images = apng.readAllFrames(in);
            for (int frame = 0; frame < images.length; frame++) {
                File out = new File(fmt.format(frame) + "-" + in.getName());
                if (out.exists()) {
                    System.err.println("File exists, skipping: " + out);
                    break;
                }
                System.err.println("Writing " + out);
                ImageIO.write(images[frame], "PNG", out);
            }
        }
    }
}
