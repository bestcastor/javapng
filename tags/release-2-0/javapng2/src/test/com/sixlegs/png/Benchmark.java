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
        private PngConfig config = new PngConfig.Builder().build();;
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
