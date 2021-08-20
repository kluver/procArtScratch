package kluver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.win32.*;


public class Desktoper {
    public static void changeDesktop() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension size = toolkit.getScreenSize();
        double resolution = toolkit.getScreenResolution() / 96.0;
        int width = (int) (size.getWidth() * resolution);
        int height = (int) (size.getHeight() * resolution);
        PoissonDiskBackground background = new PoissonDiskBackground(width, height, resolution * 85);
        BufferedImage image = background.getImage();
        File outputFile = new File("Background.png");
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
//supply your own path instead of using this one
        String path = outputFile.getAbsolutePath();

        SPI.INSTANCE.SystemParametersInfo(
                new UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new UINT_PTR(0),
                path,
                new UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));
    }

    public static void main(String[] args) {
        //ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(Desktoper::changeDesktop, 0, 5, TimeUnit.HOURS);
        changeDesktop();
    }

    public interface SPI extends StdCallLibrary {

        //from MSDN article
        long SPI_SETDESKWALLPAPER = 20;
        long SPIF_UPDATEINIFILE = 0x01;
        long SPIF_SENDWININICHANGE = 0x02;

        @SuppressWarnings("serial")
        SPI INSTANCE = (SPI) Native.loadLibrary("user32", SPI.class, new HashMap<String, Object>() {
            {
                put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
                put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
            }
        });

        boolean SystemParametersInfo(
                UINT_PTR uiAction,
                UINT_PTR uiParam,
                String pvParam,
                UINT_PTR fWinIni
        );
    }
}

