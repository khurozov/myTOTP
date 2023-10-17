package uz.khurozov.mytotp.util;

import javafx.stage.PopupWindow;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Objects;

public class GuiUtil {
    public static String cssStringToData(String css) {
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
    }

    public static javafx.scene.image.Image getFXImage(String name) {
        return new javafx.scene.image.Image(Objects.requireNonNull(
                GuiUtil.class.getResource("/images/" + name)
        ).toExternalForm());
    }

    public static java.awt.Image getAWTImage(String name) throws IOException {
        return ImageIO.read(Objects.requireNonNull(GuiUtil.class.getResourceAsStream("/images/" + name)));
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
