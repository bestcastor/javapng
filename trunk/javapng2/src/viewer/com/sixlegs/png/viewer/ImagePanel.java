/*
 * $Id$
 * Copyright (c) 2001-2004, Tonic Systems, Inc.
 */

package com.sixlegs.png.viewer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

class ImagePanel
extends JPanel
{
    public static final int STRETCH_NONE = 0;
    public static final int STRETCH_FULL = 1;
    public static final int STRETCH_PRESERVE = 2;

    private int stretchMode = STRETCH_NONE;
    private BufferedImage image;
    private Paint background;

    public ImagePanel()
    {
        this(null);
    }

    public ImagePanel(BufferedImage image)
    {
        this.image = image;
    }

    public int getStretchMode()
    {
        return stretchMode;
    }
    
    public void setStretchMode(int stretchMode)
    {
        this.stretchMode = stretchMode;
        repaint();
    }
    
    public BufferedImage getImage()
    {
        return image;
    }

    public void setImage(BufferedImage image)
    {
        this.image = image;
        repaint();
    }

    public boolean isOpaque()
    {
        return background != null && background.getTransparency() == Transparency.OPAQUE;
    }

    public Paint getBackgroundPaint()
    {
        return background;
    }
    
    public void setBackgroundPaint(Paint background)
    {
        this.background = background;
    }

    public void paintComponent(Graphics g)
    {
        if (background != null) {
            if (background.getTransparency() != Transparency.OPAQUE)
                super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            Paint save = g2.getPaint();
            g2.setPaint(background);
            g2.fill(g2.getClipBounds());
            g2.setPaint(save);
        } else {
            super.paintComponent(g);
        }
        if (image != null) {
            Dimension size = getSize();
            boolean skipStretch = true;
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            switch (stretchMode) {
            case STRETCH_PRESERVE:
                skipStretch = size.width == imageWidth || size.height == imageHeight;
                break;
            case STRETCH_FULL:
                skipStretch = size.width == imageWidth && size.height == imageHeight;
            }
            if (skipStretch) {
                int x = (size.width - imageWidth) / 2;
                int y = (size.height - imageHeight) / 2;
                g.drawImage(image, x, y, this);
            } else if (stretchMode == STRETCH_PRESERVE) {
                double scale = Math.min((double)size.width / imageWidth, (double)size.height / imageHeight);
                int w = (int)(imageWidth * scale);
                int h = (int)(imageHeight * scale);
                int x = (size.width - w) / 2;
                int y = (size.height - h) / 2;
                g.drawImage(image, x, y, x + w, y + h, 0, 0, imageWidth, imageHeight, this);
            } else {
                g.drawImage(image, 0, 0, size.width, size.height, this);
            }
        }
    }
}
