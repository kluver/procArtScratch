package kluver;

import kluver.triangulation.Triangle2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * Created by dkluver on 12/12/17.
 */
public class PoissonDiskBackground extends PoissonDiskDelaneyTriangularization {
    protected BufferedImage bi = null;
    protected Map<Point2D,Color> pointColors = null;
    protected Random rng;

    public PoissonDiskBackground(int width, int height, double minR) {
        super(width, height, minR);
        rng = new Random();
    }

    public BufferedImage getImage() {
        generateImage();
        return bi;
    }

    protected void generateImage() {
        if(bi != null) {
            return;
        }
        generatePointColors();
        bi = new BufferedImage((int)width, (int)height, TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();

        List<Triangle2D> triangles = getTriangles();
        for(Triangle2D tri: triangles) {
            graphics.setColor(getColor(tri));
            graphics.fillPolygon(
                    new int[]{(int)(tri.a.x), (int)(tri.b.x), (int)(tri.c.x)},
                    new int[]{(int)(tri.a.y), (int)(tri.b.y), (int)(tri.c.y)},
                    3);
        }
    }

    protected void generatePointColors() {
        if(pointColors != null) {
            return;
        }
        Map<Point2D, Set<Point2D>> graph = getGraph();
        pointColors = new HashMap<>();
        Queue<Point2D> points = new ArrayDeque<>();
        points.add(getPoints().get(0));

        while(!points.isEmpty()) {
            Point2D point = points.remove();
            Set<Point2D> neighbors = graph.get(point);
            List<Color> neighborColors = new ArrayList<>();
            for (Point2D n: neighbors) {
                if(!pointColors.containsKey(n) && !points.contains(n)) {
                    points.add(n);
                }
                if (pointColors.containsKey(n)) {
                    Color neighborColor = pointColors.get(n);
                    Color newColor = walkColor(neighborColor);
                    neighborColors.add(newColor);
                }
            }
            if (neighborColors.isEmpty()) {
                neighborColors.add(Color.getHSBColor(rng.nextFloat(),0.5f,0.5f));
                //neighborColors.add(new Color(617734));
            }
            pointColors.put(point, averageColor(neighborColors));


        }
    }

    private Color averageColor(List<Color> neighborColors) {
        int rs = 0;
        int gs = 0;
        int bs = 0;
        int count = 0;
        for(Color c : neighborColors) {
            rs += c.getRed();
            gs += c.getGreen();
            bs += c.getBlue();
            count += 1;
        }
        rs = rs/count;
        gs = gs/count;
        bs = bs/count;
        return new Color(rs, gs, bs);
    }

    private Color walkColor(Color c1) {
        float[] hsv = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
        float h = hsv[0];
        float s = hsv[1];
        float v = hsv[2];
        h = (float) (h + rng.nextGaussian()/50);
        s = (float) (Math.log(s) - Math.log(1-s));
        v = (float) (Math.log(v) - Math.log(1-v));
        s = (float) (s + rng.nextGaussian() - 0.2*s);
        v = (float) (v + rng.nextGaussian() - 0.2*v);
        s = (float) (1/(1+Math.exp(-s)));
        v = (float) (1/(1+Math.exp(-v)));
        return Color.getHSBColor(h,s,v);

    }

    private Color getColor(Triangle2D tri) {
        Point2D a = new Point2D.Double(tri.a.x, tri.a.y);
        Point2D b = new Point2D.Double(tri.b.x, tri.b.y);
        Point2D c = new Point2D.Double(tri.c.x, tri.c.y);
        List<Color> colors = new ArrayList<>();
        colors.add(pointColors.get(a));
        colors.add(pointColors.get(b));
        colors.add(pointColors.get(c));
        return averageColor(colors);
    }

    public static void main(String args[]) {
        BufferedImage bi = new PoissonDiskBackground(1000, 600, 5).getImage();
        ImageDisplay id = new ImageDisplay(bi);
    }
}
