import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PoissonDiskBST extends PoissonDisk {

    public PoissonDiskBST(int width, int height, double minR) {
        super(width, height, minR);
    }

    public BufferedImage draw() {
        BufferedImage bi = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
        List<Point2D.Double> points = getPoints();
        List<Point2D.Double> inNetwork = new ArrayList<>(points.size());
        inNetwork.add(points.remove(0));
        while(!points.isEmpty()) {
            Point2D.Double closest = null;
            Point2D.Double closestSource = null;
            double minDist = Double.POSITIVE_INFINITY;
            for(Point2D.Double p1 : inNetwork) {
                for(Point2D.Double p2: points) {
                    double d = p1.distance(p2);
                    if(d < minDist) {
                        closest = p2;
                        closestSource = p1;
                        minDist = d;
                    }
                }
            }
            inNetwork.add(closest);
            points.remove(closest);
            bi.getGraphics().drawLine((int)closest.getX(), (int)closest.getY(), (int)closestSource.getX(), (int)closestSource.getY());
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
        new ImageDisplay(new PoissonDiskBST(400,400,5).draw());
    }
}
