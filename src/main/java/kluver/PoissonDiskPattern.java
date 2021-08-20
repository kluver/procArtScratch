package kluver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class PoissonDiskPattern extends PoissonDisk {

    public PoissonDiskPattern(int width, int height, double minR) {
        super(width, height, minR);
    }

    public BufferedImage draw() {
        BufferedImage bi = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        List<Point2D.Double> points = getPoints();

        for (int i = 0; i<cache.length-1; i++) {
            for (int j = 0; j<cache[i].length-1; j++) {
                Point2D.Double a,b,c,d;
                a = cache[i][j];
                b = cache[i+1][j];
                c = cache[i][j+1];
                d = cache[i+1][j+1];
                if(a != null) {
                    if(b != null) {
                        g.drawLine((int)a.getX(), (int)a.getY(),
                                (int)b.getX(), (int)b.getY());
                    }
                    if(c != null) {
                        g.drawLine((int)a.getX(), (int)a.getY(),
                                (int)c.getX(), (int)c.getY());
                    }
                    if(d != null) {
                        g.drawLine((int)a.getX(), (int)a.getY(),
                                (int)d.getX(), (int)d.getY());
                    }
                }
                if(b != null) {

                    if(c != null) {
                        g.drawLine((int)b.getX(), (int)b.getY(),
                                (int)c.getX(), (int)c.getY());
                    }
                    if(d != null) {
                        g.drawLine((int)b.getX(), (int)b.getY(),
                                (int)d.getX(), (int)d.getY());
                    }
                }
                if(c != null && d != null) {
                    g.drawLine((int)c.getX(), (int)c.getY(),
                            (int)d.getX(), (int)d.getY());
                }
            }
        }
        return bi;
    }

    public void save(String fileName) {
        try {
            if (ImageIO.write(draw(), "png", new File(fileName)))
            {
                System.out.println("-- saved");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new ImageDisplay(new PoissonDiskPattern(400,400,200).draw());
    }
}
