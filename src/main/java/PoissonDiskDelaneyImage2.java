import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 *
 */
public class PoissonDiskDelaneyImage2 extends PoissonDiskDelaneyTriangularization{
    private BufferedImage bi;

    public PoissonDiskDelaneyImage2(String fileName, double minR) throws IOException {
        this(loadImage(fileName), minR);
    }

    public PoissonDiskDelaneyImage2(BufferedImage bi, double minR) {
        super(bi.getWidth(), bi.getHeight(), minR);
        this.bi = bi;
    }

    public void doWork() {
        Map<Point2D, Set<Point2D>> graph = getGraph();
        Graphics2D graphics = bi.createGraphics();

        for (Map.Entry<Point2D, Set<Point2D>> ent : graph.entrySet()) {
            Point2D centerPoint = ent.getKey();
            List<Point2D> otherPoints = new ArrayList(ent.getValue());


            otherPoints.sort(new Comparator<Point2D>() {
                @Override
                public int compare(Point2D o1, Point2D o2) {
                    double theta1 = getTheta(o1);
                    double theta2 = getTheta(o2);
                    if(theta1 > theta2) {
                        return 1;
                    } else if (theta2 > theta1) {
                        return -1;
                    } else {
                        return 0;
                    }
                }

                public double getTheta(Point2D p) {
                    return Math.atan2(centerPoint.getY()-p.getY(), centerPoint.getX()-p.getX());
                }
            });


            int[] xs = new int[otherPoints.size()];
            int[] ys = new int[otherPoints.size()];
            int index = -1;
            for (Point2D otherPoint : otherPoints) {
                index = index + 1;
                xs[index] = (int) ((centerPoint.getX() + otherPoint.getX())/2);
                ys[index] = (int) ((centerPoint.getY() + otherPoint.getY())/2);
            }

            graphics.setColor(getColor(centerPoint));
            graphics.fillPolygon(xs, ys, xs.length);

        }
    }

    private Color getColor(Point2D p) {
        int x = (int)p.getX();
        int y = (int)p.getY();
        x = Math.min(bi.getWidth()-1,Math.max(0,x));
        y = Math.min(bi.getHeight()-1,Math.max(0,y));
        return new Color(bi.getRGB(x,y));
    }

    public void save(String fileName) {
        try {
            if (ImageIO.write(bi, "png", new File(fileName)))
            {
                System.out.println("-- saved");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage loadImage(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        path = path.toAbsolutePath();
        File file = new File(path.toString());
        if (!file.exists()) {
            throw new IOException(path + " does not exist");
        }
        return ImageIO.read(file);
    }


    public static void main(String[] args) throws IOException {
        PoissonDiskDelaneyImage2 img = new PoissonDiskDelaneyImage2("/Library/Desktop Pictures/Abstract.jpg",50);
        img.doWork();
        ImageDisplay id = new ImageDisplay(img.bi);

    }

}
