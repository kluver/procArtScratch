package kluver;

import kluver.triangulation.Triangle2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 */
public class PoissonDiskDelaneyImage extends PoissonDiskDelaneyTriangularization{
    private BufferedImage bi;

    public PoissonDiskDelaneyImage(String fileName, double minR) throws IOException {
        this(loadImage(fileName), minR);
    }

    public PoissonDiskDelaneyImage(BufferedImage bi, double minR) {
        super(bi.getWidth(), bi.getHeight(), minR);
        this.bi = bi;
    }

    public void doWork() {
        List<Triangle2D> triangles = getTriangles();
        Graphics2D graphics = bi.createGraphics();
        for(Triangle2D tri: triangles) {
            graphics.setColor(getColor(tri));
            graphics.fillPolygon(
                    new int[]{(int)tri.a.x, (int)tri.b.x, (int)tri.c.x},
                    new int[]{(int)tri.a.y, (int)tri.b.y, (int)tri.c.y},
                    3);
        }
    }

    private Color getColor(Triangle2D tri) {
        int x = (int)(tri.a.x + tri.b.x + tri.c.x)/3;
        x = Math.min(bi.getWidth()-1,Math.max(bi.getMinX(),x));
        int y = (int)(tri.a.y + tri.b.y + tri.c.y)/3;
        y = Math.min(bi.getHeight()-1,Math.max(bi.getMinY(),y));
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
        for (int i = 0; i < 10; i++) {
            PoissonDiskDelaneyImage img = new PoissonDiskDelaneyImage("/Users/dkluver/Downloads/IMG_20171118_205558.jpg",80+80*i);
            img.doWork();
            img.save("test"+i+".png");
        }

    }

}
