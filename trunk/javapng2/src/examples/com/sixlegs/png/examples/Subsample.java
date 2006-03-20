package com.sixlegs.png.examples;

import com.sixlegs.png.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.*;

public class Subsample
{
    public static void main(String[] args)
    throws IOException
    {
        File in = new File(args[0]);
        File out = new File(args[1]);
        int xsub = Integer.parseInt(args[2]);
        int ysub = Integer.parseInt(args[3]);
        int xoff = Integer.parseInt(args[4]);
        int yoff = Integer.parseInt(args[5]);

        PngConfig config = new PngConfig();
        config.setSourceSubsampling(xsub, ysub, xoff, yoff);
        PngImage png = new PngImage(config);
        BufferedImage image = png.read(in);
        Graphics2D g = image.createGraphics();
        g.setComposite(AlphaComposite.DstOver);
        g.setPaint(png.getBackground());
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        javax.imageio.ImageIO.write(image, "PNG", out);
    }
}
