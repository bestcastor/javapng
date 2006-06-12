package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class SourceRegion
{
    public static void main(String[] args)
    throws IOException
    {
        File in = new File(args[0]);
        File out = new File(args[1]);
        int x = Integer.parseInt(args[2]);
        int y = Integer.parseInt(args[3]);
        int w = Integer.parseInt(args[4]);
        int h = Integer.parseInt(args[5]);
        Rectangle sourceRegion = new Rectangle(x, y, w, h);
        PngImage png = new PngImage(new PngConfig.Builder().sourceRegion(sourceRegion).build());
        BufferedImage image = png.read(in);
        Graphics2D g = image.createGraphics();
        g.setComposite(AlphaComposite.DstOver);
        g.setPaint(png.getBackground());
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        javax.imageio.ImageIO.write(image, "PNG", out);
    }
}
