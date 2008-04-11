package com.sixlegs.examples;

import com.sixlegs.image.png.PngImage;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.io.IOException;

public class PixelGrabberExample
{
    static public void main(String[] args)
    throws IOException
    {
        PngImage png = new PngImage(args[0]);

        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int w = png.getWidth();

        int[] pixels = new int[1];
        PixelGrabber pg =
          new PixelGrabber(png, x, y, 1, 1, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) { }

        int a = 0xff & (pixels[0] >> 24);
        int r = 0xff & (pixels[0] >> 16);
        int g = 0xff & (pixels[0] >> 8);
        int b = 0xff & (pixels[0] >> 0);

        System.out.println("pixel: 0x" + Integer.toHexString(pixels[0]) +
                           " a: " + a + " r: " + r + " g: " + g + " b: " + b);
    }
}
