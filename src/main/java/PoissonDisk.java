import triangulation.DelaunayTriangulator;
import triangulation.NotEnoughPointsException;
import triangulation.Triangle2D;
import triangulation.Vector2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class PoissonDisk {
    private static final int K = 100;

    protected double width;
    protected double height;
    protected double minR;
    protected Random rand;

    protected List<Point2D.Double> points = null;
    protected Point2D.Double[][] cache;

    private List<Triangle2D> triangles = null;

    public PoissonDisk(int width, int height, double minR) {
        this.width = width;
        this.height = height;
        this.minR = minR;
        this.rand = new Random();
    }

    private void generatePoints() {
        if (points != null) {
            return;
        }
        points = new ArrayList<>();
        cache = new Point2D.Double[1+getPos(width + minR)][1+getPos(height + minR)];

        double firstX = rand.nextDouble()*width;
        double firstY = rand.nextDouble()*height;

        List<Point2D.Double> input = new ArrayList<>();

        input.add(new Point2D.Double(firstX, firstY));
        cache(input.get(0));

        while (input.size() > 0) {
            int lstPos = rand.nextInt(input.size());
            Point2D.Double center = input.get(lstPos);

            boolean found = false;
            for(int i = 0; i<K && !found; i++) {
                Point2D.Double newPoint = newPoint(center);
                if (inBounds(newPoint) && isAcceptable(newPoint)) {
                    cache(newPoint);
                    found = true;
                    input.add(newPoint);
                }
            }
            if (!found) {
                input.remove(lstPos);
                points.add(center);
            }
        }
    }

    private int getPos(double pos) {
        pos += minR;
        double r = minR/Math.sqrt(2);
        return (int)Math.ceil(pos/r);
    }

    private void cache(Point2D.Double point) {
        int cx = getPos(point.getX());
        int cy = getPos(point.getY());
        cache[cx][cy] = point;
    }

    private boolean inBounds(Point2D.Double point) {
        return point.getX()>-minR && point.getX()<width+minR &&
                point.getY()>-minR && point.getY()<height+minR;
    }

    private boolean isAcceptable(Point2D.Double newPoint) {
        int cx = getPos(newPoint.getX());
        int lowX = Math.max(0, cx-2);
        int highX = Math.min(cx+2, cache.length-1);

        int cy = getPos(newPoint.getY());
        int lowY = Math.max(0, cy-2);
        int highY = Math.min(cy+2, cache[0].length-1);

        for(int x = lowX; x<=highX; x++) {
            for(int y = lowY; y<=highY; y++) {
                if (cache[x][y] != null &&newPoint.distance(cache[x][y]) <= minR) {
                    return false;
                }
            }
        }
        return true;
    }

    private Point2D.Double newPoint(Point2D.Double center) {
        double newR = minR*(rand.nextDouble()+1);
        double theta = 2*Math.PI*rand.nextDouble();
        double x = newR*Math.cos(theta) + center.getX();
        double y = newR*Math.sin(theta) + center.getY();
        return new Point2D.Double(x,y);
    }

    public List<Point2D.Double> getPoints() {
        generatePoints();
        return points;
    }
}
