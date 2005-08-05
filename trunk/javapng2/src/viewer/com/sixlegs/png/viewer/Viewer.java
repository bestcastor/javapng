/*
com.sixlegs.png - Java package to read and display PNG images
Copyright (C) 1998-2005 Chris Nokleberg

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

package com.sixlegs.png.viewer;

import com.sixlegs.png.PngConfig;
import com.sixlegs.png.PngImage;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class Viewer
{
    private PngConfig config = new PngConfig();
    private int progressiveDelay = 0; // TODO
    private ImagePanel imagePanel;
    private Paint checker;

    public static void main(final String[] args)
    throws Exception
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Viewer(args);
            }
        });
    }

    private Viewer(String[] args)
    {
        config.setProgressive(true);
        
        BufferedImage image = readPngResource("checker.png");
        Rectangle rect = new Rectangle(0, 0, image.getWidth(), image.getHeight());
        checker = new TexturePaint(image, rect);

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("PNG Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imagePanel = new ImagePanel();
        Dimension size = new Dimension(400, 300);
        File file = (args.length > 0) ? new File(args[0]) : null;
        if (file != null) {
            try {
                PngImage png = readHeader(file);        
                size.setSize(png.getWidth(), png.getHeight());
            } catch (IOException e) {
                // ignore here, open will re-throw
            }
        }
        imagePanel.setStretchMode(ImagePanel.STRETCH_PRESERVE);
        imagePanel.setBackgroundPaint(checker);
        imagePanel.setPreferredSize(size);
//         JScrollPane scrollPane = new JScrollPane(imagePanel);
//         frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(imagePanel, BorderLayout.CENTER);
        // TODO: toolbar, menus
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        if (file != null)
            open(file);
    }

    private void open(File file)
    {
        new Thread(new ReadPngAction(new PngImage(config){
            protected void handleFrame(final BufferedImage image, int framesLeft) {
                SwingUtilities.invokeLater(new UpdateImageAction(imagePanel, image));
                if (progressiveDelay > 0 && framesLeft > 0) {
                    try {
                        Thread.sleep(progressiveDelay);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        }, file)).start();
    }

    private static PngImage readHeader(File file)
    throws IOException
    {
        PngConfig config = new PngConfig();
        config.setReadLimit(PngConfig.READ_HEADER);
        PngImage png = new PngImage(config);
        png.read(file);
        return png;
    }

    private BufferedImage readPngResource(String path)
    {
        try {
            return new PngImage().read(getClass().getResourceAsStream(path), true);
        } catch (IOException e) {
            throw new Error(e.getMessage());
        }
    }

    private static class ReadPngAction
    implements Runnable
    {
        private PngImage png;
        private File file;

        public ReadPngAction(PngImage png, File file)
        {
            this.png = png;
            this.file = file;
        }
            
        public void run()
        {
            try {
                // TODO: disable loading
                png.read(file);
            } catch (IOException e) {
                // TODO: error dialog
                e.printStackTrace(System.err);
            } finally {
                // TODO: re-enable loading
            }
        }
    }

    private static class UpdateImageAction
    implements Runnable
    {
        private ImagePanel panel;
        private BufferedImage image;

        public UpdateImageAction(ImagePanel panel, BufferedImage image)
        {
            this.panel = panel;
            this.image = image;
        }

        public void run()
        {
            Dimension size = new Dimension(image.getWidth(), image.getHeight());
            panel.setPreferredSize(size);
            panel.setImage(image);
        }
    }
}
