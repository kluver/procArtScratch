package kluver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Math.sqrt;

public class AnimationTest extends JFrame {
    JLabel label;
    public AnimationTest(BufferedImage bi) {
        label = new JLabel(new ImageIcon(bi));
        getContentPane().add(label);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void draw(AnimationTest a) {

        a.repaint();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BufferedImage bi = new BufferedImage(1440,900, BufferedImage.TYPE_INT_ARGB);

        AnimationTest a = new AnimationTest(bi);

        PoissonDisk pd = new PoissonDisk(1440, 900, 10);
        boolean[][] grid = new boolean[1440/5][900/5];
        for(int i = 0; i < 1440/5; i++) {
            grid[i][0] = true;
            for(int j = 1; j < 900/5-1; j++) {
                grid[i][j] = false;
            }
            grid[i][900/5-1] = true;
        }
        for(int j = 0; j < 900/5; j++) {
            grid[0][j] = true;
            grid[1440/5-1][j] = true;
        }
        List<Point2D.Double> points = new ArrayList<>();
        List<Point2D.Double> unlinkedPoints = new ArrayList<>();

        for(Point2D.Double p : pd.getPoints()) {
            int x = (int)(p.x/5);
            int y = (int)(p.y/5);
            if (x >= 1 && x < 1440/5-1 && y >=1 && y < 900/5-1 ) {
                unlinkedPoints.add(p);
            }
        }
        List<List<IntPair>> traces = new ArrayList<>();
        //dodraw(a, bi, points, grid, traces);

        System.out.println("Starting Searchs");

        Random rng = new Random();
        unlinkedPoints.sort((p1, p2) -> Double.compare(p1.getX(), p2.getX()));
        while (unlinkedPoints.size()>2) {
            int choice1 = min(rng.nextInt(unlinkedPoints.size()), rng.nextInt(unlinkedPoints.size()));
            Point2D.Double start = unlinkedPoints.remove(choice1);
            boolean fail = true;
            Collections.shuffle(unlinkedPoints);
            for (int tries = 0; tries < unlinkedPoints.size(); tries++) {
                List<Point2D.Double> options = new ArrayList(unlinkedPoints);
                int choice2 = max(rng.nextInt(options.size()), rng.nextInt(options.size()));
                Point2D.Double end = options.remove(choice2);
                if(start.distance(end) < 40) {
                    continue;
                }

                List<IntPair> path = search(start, end, grid, a, bi, points, traces);
                if (path != null) {
                    unlinkedPoints.remove(end);
                    traces.add(path);
                    for (IntPair p : path) {
                        grid[p.x][p.y] = true;
                        grid[p.x+1][p.y] = true;
                        grid[p.x-1][p.y] = true;
                        grid[p.x][p.y+1] = true;
                        grid[p.x][p.y-1] = true;
                    }
                    for (int x = -1; x <= +1; x++) {
                        for (int y = -1; y<= +1; y++) {
                            grid[(int)(start.getX()/5)+x][(int)(start.getY()/5)+y] = true;
                            grid[(int)(end.getX()/5)+x][(int)(end.getY()/5)+y] = true;
                        }
                    }
                    points.add(start);
                    points.add(end);

                    fail = false;
                    break;
                }
            }
            if (fail) {
                //points.remove(start);
            }

        }

        for(int i = 0; i < 1440/5; i++) {
            grid[i][0] = true;
            for(int j = 1; j < 900/5-1; j++) {
                grid[i][j] = false;
            }
            grid[i][900/5-1] = true;
        }
        for(int j = 0; j < 900/5; j++) {
            grid[0][j] = true;
            grid[1440/5-1][j] = true;
        }

        for (Point2D.Double point : points) {
            for (int x = -1; x <= +1; x++) {
                for (int y = -1; y<= +1; y++) {
                    grid[(int)(point.getX()/5)+x][(int)(point.getY()/5)+y] = true;
                }
            }
        }

        List<List<IntPair>> traces2 = new ArrayList<>();
        //dodraw(a, bi, points, grid, traces);

        System.out.println("Starting Searchs2");

        unlinkedPoints = new ArrayList<>(points);
        unlinkedPoints.sort((p1, p2) -> Double.compare(p1.getY(), p2.getY()));
        while (unlinkedPoints.size()>2) {

            int choice1 = min(rng.nextInt(unlinkedPoints.size()), rng.nextInt(unlinkedPoints.size()));
            Point2D.Double start = unlinkedPoints.remove(choice1);
            boolean fail = true;
            Collections.shuffle(unlinkedPoints);
            for (int x = -1; x <= +1; x++) {
                for (int y = -1; y<= +1; y++) {
                    grid[(int)(start.getX()/5)+x][(int)(start.getY()/5)+y] = false;
                }
            }
            for (int tries = 0; tries < unlinkedPoints.size(); tries++) {
                List<Point2D.Double> options = new ArrayList(unlinkedPoints);
                int choice2 = max(rng.nextInt(options.size()), rng.nextInt(options.size()));
                Point2D.Double end = options.remove(choice2);
                for (int x = -1; x <= +1; x++) {
                    for (int y = -1; y<= +1; y++) {
                        grid[(int)(end.getX()/5)+x][(int)(end.getY()/5)+y] = false;
                    }
                }
                if(start.distance(end) < 30) {
                    continue;
                }

                System.out.println("?");
                List<IntPair> path = search(start, end, grid, a, bi, points, traces);
                if (path != null) {
                    unlinkedPoints.remove(end);
                    traces2.add(path);
                    for (IntPair p : path) {
                        grid[p.x][p.y] = true;
                        grid[p.x+1][p.y] = true;
                        grid[p.x-1][p.y] = true;
                        grid[p.x][p.y+1] = true;
                        grid[p.x][p.y-1] = true;
                    }
                    fail = false;
                    for (int x = -1; x <= +1; x++) {
                        for (int y = -1; y<= +1; y++) {
                            grid[(int)(end.getX()/5)+x][(int)(end.getY()/5)+y] = true;
                        }
                    }
                    break;
                }
            }
            if (fail) {
                for (int x = -1; x <= +1; x++) {
                    for (int y = -1; y<= +1; y++) {
                        grid[(int)(start.getX()/5)+x][(int)(start.getY()/5)+y] = true;
                    }
                }
            }
            for (int x = -1; x <= +1; x++) {
                for (int y = -1; y<= +1; y++) {
                    grid[(int)(start.getX()/5)+x][(int)(start.getY()/5)+y] = true;
                }
            }
        }

        dodraw(a, bi, points, grid, traces, traces2);
        try {
            ImageIO.write(bi, "png", new File("out.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("DONE");
    }

    private static List<IntPair> search(Point2D.Double start, Point2D.Double end, boolean[][] grid,
                                        AnimationTest a, BufferedImage bi, List<Point2D.Double> points, List<List<IntPair>> traces) {

        PriorityQueue<SearchNode> queue = new PriorityQueue<>();
        Set<IntPair> searched = new HashSet<>();
        Set<IntPair> supersearched = new HashSet<>();

        IntPair target = new IntPair((int)(end.getX()/5), (int)(end.getY()/5));
        SearchNode initial = new SearchNode(new IntPair((int)(start.getX()/5), (int)(start.getY()/5)), target);
        searched.add(initial.location);
        queue.add(initial);

        while (! queue.isEmpty()) {
            //dodraw(a, bi, points, grid, traces, searched, supersearched);
            SearchNode sn = queue.remove();
            supersearched.add(sn.location);
            if (sn.location.equals(target)) {
                // foundit

                List<IntPair> path = new LinkedList<>();
                while (sn != null) {
                    path.add(0, sn.location);
                    sn = sn.predecessor;
                }
                return path;
            } else {
                for (SearchNode next : sn.getDescendents(target)) {
                    if (!searched.contains(next.location)
                            && next.location.x >= 0
                            && next.location.y >= 0
                            && next.location.x < 1440/5
                            && next.location.y < 900/5
                            && ! grid[next.location.x][next.location.y]) {
                        searched.add(next.location);
                        queue.add(next);
                    }
                }
            }
        }

        return null;
    }

    static class SearchNode implements Comparable<SearchNode>{
        IntPair location;
        SearchNode predecessor;
        double pathlen;
        double distance;

        public SearchNode(IntPair location, IntPair target) {
            this.location = location;
            this.predecessor = null;
            this.pathlen = 0;
            this.distance = location.distance(target);
        }

        SearchNode(SearchNode predecessor, IntPair location, IntPair target) {
            this.predecessor = predecessor;
            this.location = location;
            this.pathlen = predecessor.pathlen + 1;
            this.distance = location.distance(target);
        }

        List<SearchNode> getDescendents(IntPair target) {
            List<SearchNode> results = new ArrayList<>(8);
            int x = this.location.x;
            int y = this.location.y;
            results.add(new SearchNode(this, new IntPair(x+1, y+1), target));
            results.add(new SearchNode(this, new IntPair(x+1, y-1), target));
            results.add(new SearchNode(this, new IntPair(x-1, y+1), target));
            results.add(new SearchNode(this, new IntPair(x-1, y-1), target));
            results.add(new SearchNode(this, new IntPair(x+1, y), target));
            results.add(new SearchNode(this, new IntPair(x-1, y), target));
            results.add(new SearchNode(this, new IntPair(x, y+1), target));
            results.add(new SearchNode(this, new IntPair(x, y-1), target));
            return results;
        }

        @Override
        public int compareTo(SearchNode o) {
            return Double.compare(pathlen+distance, o.pathlen+o.distance);
        }
    }

    private static void dodraw(AnimationTest a, BufferedImage bi, List<Point2D.Double> points, boolean[][] grid, List<List<IntPair>> traces, List<List<IntPair>> traces2) {
        Graphics2D graphics = bi.createGraphics();
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(Color.BLACK);
        graphics.fillRect(0,0,1440, 900);
//        for (int i = 0; i < 1440/5; i++) {
//            for (int k = 0; k < 900/5; k++) {
//                if (grid[i][k]) {
//                    graphics.setColor(Color.black);
//                } else {
//                    graphics.setColor(Color.white);
//                }
//                graphics.fillRect(i*5, k*5, 5, 5);
//            }
//        }

        for (List<IntPair> trace : traces2) {
            Iterator<IntPair> it = trace.iterator();
            if (it.hasNext()) {
                IntPair start = it.next();
                while (it.hasNext()) {
                    IntPair end = it.next();
                    graphics.setColor(Color.blue);
                    graphics.drawLine(5*start.x+2, 5*start.y+2, 5*end.x+2, 5*end.y+2);
                    start = end;
                }
            }
        }
        for (List<IntPair> trace : traces) {
            Iterator<IntPair> it = trace.iterator();
            if (it.hasNext()) {
                IntPair start = it.next();
                while (it.hasNext()) {
                    IntPair end = it.next();
                    graphics.setColor(Color.green);
                    graphics.drawLine(5*start.x+2, 5*start.y+2, 5*end.x+2, 5*end.y+2);
                    start = end;
                }
            }
        }

        for (Point2D.Double p : points) {
            graphics.setColor(Color.orange);
            graphics.drawOval((int)p.getX()-5, (int)p.getY()-5, 10,10);

            graphics.setColor(Color.BLACK);
            graphics.fillOval((int)p.getX()-4, (int)p.getY()-4, 8,8);
        }

        a.repaint();
    }
}