import triangulation.DelaunayTriangulator;
import triangulation.NotEnoughPointsException;
import triangulation.Triangle2D;
import triangulation.Vector2D;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Created by dkluver on 11/9/17.
 */
public class PoissonDiskDelaneyTriangularization extends PoissonDisk {
    protected List<Triangle2D> triangles = null;
    protected  Map<Point2D, Set<Point2D>> graph = null;

    public PoissonDiskDelaneyTriangularization(int width, int height, double minR) {
        super(width, height, minR);
    }

    private void generateTriangles() {
        if(triangles != null) {
            return;
        }
        List<Point2D.Double> points = getPoints();

        List<Vector2D> pointSet = new ArrayList<>();
        for(Point2D.Double p : points) {
            pointSet.add(new Vector2D(p.getX(), p.getY()));
        }

        DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(pointSet);
        try {
            delaunayTriangulator.triangulate();
        } catch (NotEnoughPointsException e) {
            e.printStackTrace();
        }
        triangles = delaunayTriangulator.getTriangles();
    }

    public List<Triangle2D> getTriangles() {
        generateTriangles();
        return triangles;
    }

    private void generateGraph() {
        if (graph != null) {
            return;
        }
        generateTriangles();
        graph = new HashMap<>();
        for (Triangle2D tri: triangles) {
            Point2D a = new Point2D.Double(tri.a.x, tri.a.y);
            Point2D b = new Point2D.Double(tri.b.x, tri.b.y);
            Point2D c = new Point2D.Double(tri.c.x, tri.c.y);

            if(! graph.containsKey(a)) {
                graph.put(a, new HashSet<>());
            }
            graph.get(a).add(b);
            graph.get(a).add(c);

            if(! graph.containsKey(b)) {
                graph.put(b, new HashSet<>());
            }
            graph.get(b).add(a);
            graph.get(b).add(c);

            if(! graph.containsKey(c)) {
                graph.put(c, new HashSet<>());
            }
            graph.get(c).add(a);
            graph.get(c).add(b);
        }
    }


    public Map<Point2D, Set<Point2D>> getGraph() {
        generateGraph();
        return graph;
    }

}
