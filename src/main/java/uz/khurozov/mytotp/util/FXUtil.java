package uz.khurozov.mytotp.util;

import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;

public class FXUtil {
    public static String cssStringToData(String css) {
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
    }

    public static Region getCopyIcon() {
        SVGPath svg = new SVGPath();
        svg.setContent("""
                M131.07 372.11c.37 1 .57 2.08.57 3.2 0 1.13-.2 2.21-.57 3.21v75.91c0 10.74 4.41 20.53 11.5 27.62s16.87
                11.49 27.62 11.49h239.02c10.75 0 20.53-4.4 27.62-11.49s11.49-16.88
                11.49-27.62V152.42c0-10.55-4.21-20.15-11.02-27.18l-.47-.43c-7.09-7.09-16.87-11.5-27.62-11.5H170.19c-10.75
                0-20.53 4.41-27.62 11.5s-11.5 16.87-11.5 27.61v219.69zm-18.67 12.54H57.23c-15.82
                0-30.1-6.58-40.45-17.11C6.41 356.97 0 342.4 0 326.52V57.79c0-15.86 6.5-30.3 16.97-40.78l.04-.04C27.51
                6.49 41.94 0 57.79 0h243.63c15.87 0 30.3 6.51 40.77 16.98l.03.03c10.48 10.48 16.99 24.93 16.99
                40.78v36.85h50c15.9 0 30.36 6.5 40.82 16.96l.54.58c10.15 10.44 16.43 24.66 16.43 40.24v302.01c0 15.9-6.5
                30.36-16.96 40.82-10.47 10.47-24.93 16.97-40.83 16.97H170.19c-15.9
                0-30.35-6.5-40.82-16.97-10.47-10.46-16.97-24.92-16.97-40.82v-69.78zM340.54
                94.64V57.79c0-10.74-4.41-20.53-11.5-27.63-7.09-7.08-16.86-11.48-27.62-11.48H57.79c-10.78 0-20.56
                4.38-27.62 11.45l-.04.04c-7.06 7.06-11.45 16.84-11.45 27.62v268.73c0 10.86 4.34 20.79 11.38 27.97 6.95
                7.07 16.54 11.49 27.17 11.49h55.17V152.42c0-15.9 6.5-30.35 16.97-40.82 10.47-10.47 24.92-16.96
                40.82-16.96h170.35z
                """);

        return wrapSvgToRegion(svg);
    }
    public static Region getDeleteSvg() {
        SVGPath svg = new SVGPath();
        svg.setContent("""
                M704 128H448c0 0 0-24.057 0-32 0-17.673-14.327-32-32-32s-32 14.327-32 32c0 17.673 0 32 0 32H128c-35.346
                0-64 28.654-64 64v64c0 35.346 28.654 64 64 64v576c0 35.346 28.654 64 64 64h448c35.346 0 64-28.654
                64-64V320c35.346 0 64-28.654 64-64v-64C768 156.654 739.346 128 704 128zM640 864c0 17.673-14.327 32-32
                32H224c-17.673 0-32-14.327-32-32V320h64v480c0 17.673 14.327 32 32 32s32-14.327 32-32l0.387-480H384v480c0
                17.673 14.327 32 32 32s32-14.327 32-32l0.387-480h64L512 800c0 17.673 14.327 32 32 32s32-14.327
                32-32V320h64V864zM704 240c0 8.837-7.163 16-16 16H144c-8.837 0-16-7.163-16-16v-32c0-8.837 7.163-16
                16-16h544c8.837 0 16 7.163 16 16V240z
                """);

        return wrapSvgToRegion(svg);
    }

    private static Region wrapSvgToRegion(SVGPath svg) {
        double h = svg.prefHeight(-1);
        double w = svg.prefWidth(-1);

        double x = 16;

        svg.setScaleX(x / w);
        svg.setScaleY(x / h);

        Region region = new Region();
        region.setShape(svg);
        region.setMaxSize(x, x);
        region.setMinSize(x, x);
        region.setPrefSize(x, x);

        region.setStyle("-fx-background-color: black");
        region.setPadding(new Insets(2));

        return region;
    }

    public static Window getActiveWindow() {
        Iterator<Window> windows = Window.getWindows().iterator();

        Window window = null;
        do {
            if (!windows.hasNext()) {
                return window;
            }

            window = windows.next();
        } while(!window.isFocused() || window instanceof PopupWindow);

        return window;
    }
}
