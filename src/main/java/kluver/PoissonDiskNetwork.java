package kluver;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class PoissonDiskNetwork extends PoissonDisk {

    public PoissonDiskNetwork(int width, int height, double minR) {
        super(width, height, minR);
    }

    public BufferedImage draw() {
        BufferedImage bi = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
        List<Point2D.Double> points = getPoints();
        for(Point2D p1 : points) {
            for(Point2D p2 : points) {
                if(p1.distance(p2)<minR*1.2) {
                    bi.getGraphics().drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
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
        new ImageDisplay(new PoissonDiskNetwork(400,400,20).draw());
    }
}
