package com.sixlegs.image.png;

import java.io.*;
import java.security.MessageDigest;
import junit.framework.*;
import java.awt.image.ColorModel;

public class DataTest
extends TestCase
{
    public void testImages()
    throws Exception
    {
        int[] buffer = new int[800 * 600]; // big enough to handle biggest image
        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        BufferedReader r = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/images.txt")));
        String line;
        boolean fail = false;
        while ((line = r.readLine()) != null) {
            int space = line.indexOf(' ');
            String image = line.substring(0, space).trim();
            String result = line.substring(space + 1).trim();
            try {
                InputStream in = getClass().getResourceAsStream(image);
                if (in == null)
                    fail("Cannot find image \"" + image + "\"");
                PngImage png = new PngImage(in);
                png.setBuffer(buffer);
                png.getEverything(true);
                byte[] pixbuf = new byte[4];
                for (int i = 0, size = png.getWidth() * png.getHeight(); i < size; i++) {
                    int pixel = buffer[i];
                    pixbuf[3] = (byte)(0xFF & pixel);
                    pixbuf[2] = (byte)(0xFF & (pixel >>> 8));
                    pixbuf[1] = (byte)(0xFF & (pixel >>> 16));
                    pixbuf[0] = (byte)(0xFF & (pixel >>> 24));
                    md5.update(pixbuf);
                }
                String hash = toHexString(md5.digest());
                if (!result.equals(hash)) {
                    System.err.println("Expected digest 0x" + result + " for image " + image + ", got 0x" + hash);
                    fail = true;
                }
             } catch (Exception e) {
                 if (!result.equals(e.getMessage())) {
                     System.err.println("Caught exception while processing image " + image + ":");
                     e.printStackTrace(System.err);
                     fail = true;
                 }
             }
        }
        if (fail)
            fail("Failures detected.");
    }

    private static String toHexString(byte[] b)
    {
       StringBuffer hex = new StringBuffer(2 * b.length);
       for (int i = 0; i < b.length; i++) {
           byte n = b[i];
           if (n >= 0 && n <= 15)
               hex.append("0");
           hex.append(Integer.toHexString(0xFF & n));
       }
       return hex.toString().toUpperCase();
    }

    public DataTest(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(DataTest.class);
    }

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }
}
