package com.sixlegs.png.examples;

import com.sixlegs.png.PngImage;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;

public class Viewer
{

    public static void main(String[] args)
    throws Exception
    {
        new Viewer(args);
    }

    public Viewer(String[] args)
    throws Exception
    {
        PngImage png = new PngImage();
        final BufferedImage image = png.read(new File(args[0]));
        final Color bg = getBackground(png);

        JFrame frame = new JFrame("PNG Viewer");
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JPanel panel = new JPanel(){
            public void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint(bg);
                g2.fill(g2.getClipBounds());
                g2.drawImage(image, 0, 0, null);
            }
        };
        JScrollPane scrollPane = new JScrollPane(panel);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        frame.pack();
        Dimension size = frame.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - size.width) / 2,
                          (screenSize.height - size.height) / 2);
        frame.setVisible(true);
    }

    private static Color getBackground(PngImage png)
    {
        Color bg = png.getBackground();
        return (bg != null) ? bg : Color.white;
    }
}
