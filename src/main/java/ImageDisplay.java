import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Created by dkluver on 11/10/17.
 */
public class ImageDisplay extends JFrame {
    public ImageDisplay(BufferedImage bi) {
        getContentPane().add(new JLabel(new ImageIcon(bi)));
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
