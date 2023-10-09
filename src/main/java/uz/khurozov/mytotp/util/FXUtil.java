package uz.khurozov.mytotp.util;

import javafx.scene.image.Image;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Objects;

public class FXUtil {
    public static String cssStringToData(String css) {
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
    }

    public static Image getImage(String name) {
        return new Image(Objects.requireNonNull(
                FXUtil.class.getResource("/assets/images/"+name)
        ).toExternalForm());
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
