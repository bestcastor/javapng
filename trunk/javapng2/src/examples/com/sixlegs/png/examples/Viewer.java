package com.sixlegs.png.examples;

import com.sixlegs.png.BasicPngConfig;
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
    private static final int PROGRESSIVE_DELAY = 50;

    public static void main(String[] args)
    throws Exception
    {
        new Viewer(args);
    }

    public Viewer(String[] args)
    throws Exception
    {
        File file = new File(args[0]);
        PngImage png = readMetadata(file);
        
        JFrame frame = new JFrame("PNG Viewer");
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        final BufferedImage[] cur = new BufferedImage[1];
        final Color bg = png.getBackground();
        final JPanel panel = new JPanel(){
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint((bg != null) ? bg : Color.white);
                g2.fill(g2.getClipBounds());
                if (cur[0] != null)
                    g2.drawImage(cur[0], 0, 0, null);
            }
        };        
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(png.getWidth(), png.getHeight()));
        frame.pack();
        Dimension size = frame.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - size.width) / 2,
                          (screenSize.height - size.height) / 2);
        frame.setVisible(true);

        BasicPngConfig config = new BasicPngConfig();
        config.setProgressive(true);
        (new PngImage(config){
            public void handleFrame(BufferedImage image, int framesLeft) {
                cur[0] = image;
                panel.repaint();
                if (PROGRESSIVE_DELAY > 0 && framesLeft > 0) {
                    try {
                        Thread.sleep(PROGRESSIVE_DELAY);
                    } catch (InterruptedException e) { }
                }
            }
        }).read(file);
    }

    private static PngImage readMetadata(File file)
    throws IOException
    {
        BasicPngConfig config = new BasicPngConfig();
        config.setMetadataOnly(true);
        PngImage png = new PngImage(config);
        png.read(file);
        return png;
    }
}
