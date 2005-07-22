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

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class Benchmark
{
    abstract static class PngReader
    {
        abstract public void read(File file) throws IOException;
    }

    private static PngReader SIXLEGS2 = new PngReader(){
        private PngConfig config = new BasicPngConfig();
        public void read(File file) throws IOException {
            new PngImage(config).read(file);
        }
    };

    private static PngReader SIXLEGS1 = new PngReader(){
        public void read(File file) throws IOException {
            new com.sixlegs.image.png.PngImage(file.toURL()).getEverything();
        }
    };
    
    private static PngReader IMAGEIO = new PngReader(){
        public void read(File file) throws IOException {
            ImageIO.read(file);
        }
    };

    private static PngReader TOOLKIT = new PngReader(){
        private MediaTracker tracker = new MediaTracker(new Component(){});
        private Toolkit toolkit = Toolkit.getDefaultToolkit();
        public void read(File file) throws IOException {
            try {
                tracker.addImage(toolkit.createImage(file.toURL()), 0);
                tracker.waitForID(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    };
    
    public static void main(String[] args)
    throws Exception
    {
        int loop = (args.length > 0) ? Integer.parseInt(args[0]) : 1;
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        List list = new ArrayList();
        String line;
        while ((line = r.readLine()) != null)
            list.add(new File(line));
        File[] files = (File[])list.toArray(new File[list.size()]);
        benchmark(files, loop, TOOLKIT, " Toolkit");
        benchmark(files, loop, IMAGEIO, " ImageIO");
        benchmark(files, loop, SIXLEGS1, "Sixlegs1");
        benchmark(files, loop, SIXLEGS2, "Sixlegs2");
    }

    private static void benchmark(File[] files, int loop, PngReader reader, String desc)
    throws IOException
    {
        File cur = null;
        try {
            long t = System.currentTimeMillis();
            for (int i = 0; i < loop; i++) {
                for (int j = 0; j < files.length; j++) {
                    reader.read(cur = files[j]);
                }
            }
            t = System.currentTimeMillis() - t;
            if (desc != null)
                System.err.println(desc + ": read " + (files.length * loop) + " images in " + t + " ms");
        } catch (IOException e) {
            System.err.println("Error reading " + cur);
            throw e;
        }
    }
}
