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

package com.sixlegs.png;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import javax.swing.Timer;
import java.util.*;
import java.util.List;

public class Animator
implements ActionListener
{
    private static final int MIN_DELAY = 5;
    
    private final AnimatedPngImage png;
    private final BufferedImage[] frames;
    private final BufferedImage target;
    private final Graphics2D g;
    private final RenderData[] render;
    private final int timerDelay;
    private final Color background;

    private long waitUntil;
    private int index = -1;
    private int iter;
    private boolean done;
    
    private static BufferedImage createCompatibleImage(BufferedImage image, int width, int height)
    {
        return new BufferedImage(image.getColorModel(),
                                 image.getRaster().createCompatibleWritableRaster(width, height),
                                 image.isAlphaPremultiplied(),
                                 null); // TODO: properties
    }

    public Animator(AnimatedPngImage png, BufferedImage[] frames, BufferedImage target)
    {
        if (!png.isAnimated())
            throw new IllegalArgumentException("PNG is not animated");
        if (target == null)
            target = createCompatibleImage(frames[0], png.getWidth(), png.getHeight());
        this.png = png;
        this.frames = frames;
        this.target = target;
        this.g = target.createGraphics();

        Color bkgd = png.getBackground();
        this.background = (bkgd != null) ? bkgd : Color.black;
        
        List renderList = new ArrayList();
        int minDelay = Integer.MAX_VALUE;
        for (int i = 0; i < png.getNumFrames(); i++) {
            FrameControl frame = png.getFrame(i);
            if (frame.isSkip())
                continue;
            RenderData rd = new RenderData();
            rd.image = frames[i];
            rd.bounds = frame.getBounds();
            rd.dispose = frame.getDispose();
            rd.blend = frame.isBlend() ? AlphaComposite.SrcOver : AlphaComposite.Src;
            if (frame.getDispose() == AnimatedPngImage.APNG_RENDER_OP_DISPOSE_PREVIOUS) {
                rd.prev = createCompatibleImage(rd.image, rd.bounds.width, rd.bounds.height);
                rd.prev_g = rd.prev.createGraphics();
                rd.prev_g.setComposite(AlphaComposite.Src);
            }
            rd.delay = (int)(frame.getDelay() * 1000);
            minDelay = Math.min(minDelay, Math.max(rd.delay, MIN_DELAY));
            renderList.add(rd);
        }
        this.timerDelay = minDelay;
        this.render = (RenderData[])renderList.toArray(new RenderData[renderList.size()]);
    }

    public void reset()
    {
        waitUntil = iter = 0;
        index = -1;
        done = false;
    }

    public BufferedImage getTarget()
    {
        return target;
    }

    public int getTimerDelay()
    {
        return timerDelay;
    }

    public void actionPerformed(ActionEvent e)
    {
        long now = System.currentTimeMillis();
        if (done)
            return;
        if (now < waitUntil)
            return;
        if (index >= 0)
            dispose(render[index]);
        if (++index == render.length) {
            int maxIter = png.getNumIterations();
            if (++iter == maxIter && maxIter != 0) {
                done = true;
                return;
            }
            index = 0;
        }
        // System.err.println("drawing " + index);
        draw(render[index]);
        waitUntil = now + render[index].delay;
    }

    private void dispose(RenderData rd)
    {
        Rectangle bounds = rd.bounds;
        switch (rd.dispose) {
        case AnimatedPngImage.APNG_RENDER_OP_DISPOSE_BACKGROUND:
            g.setColor(background);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            break;
        case AnimatedPngImage.APNG_RENDER_OP_DISPOSE_PREVIOUS:
            g.setComposite(AlphaComposite.Src);
            g.drawImage(rd.prev, bounds.x, bounds.y, bounds.width, bounds.height, null, null);
            break;
        }
    }

    private void draw(RenderData rd)
    {
        Rectangle bounds = rd.bounds;
        if (rd.prev != null)
            rd.prev_g.drawImage(target, bounds.x, bounds.y, bounds.width, bounds.height, null, null);
        g.setComposite(rd.blend);
        g.drawImage(rd.image,  bounds.x, bounds.y, bounds.width, bounds.height, null, null);
    }

    private static class RenderData
    {
        int delay;
        int dispose;
        Rectangle bounds;
        BufferedImage image;
        BufferedImage prev;
        Graphics2D prev_g;
        Composite blend;
    }
}
