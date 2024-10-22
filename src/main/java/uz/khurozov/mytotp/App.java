package uz.khurozov.mytotp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uz.khurozov.mytotp.fx.AppPane;
import uz.khurozov.mytotp.fx.notification.Notifications;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Timer;

public class App extends Application {

    public static final String TITLE = "myTOTP";
    public static final Timer TIMER = new Timer();
    public static Stage stage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        App.stage = stage;
        Scene scene = new Scene(new AppPane());
        stage.setTitle(App.TITLE);
        stage.setScene(scene);
        stage.getIcons().addAll(
                new Image(App.getResourceAsExternal("/images/logo_32.png")),
                new Image(App.getResourceAsExternal("/images/logo_128.png"))
        );
        stage.setResizable(false);

        stage.show();
    }

    public static String getResourceAsExternal(String name) {
        return Objects.requireNonNull(
                App.class.getResource(name)
        ).toExternalForm();
    }

    public static InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(
                App.class.getResourceAsStream(name)
        );
    }

    public static String getCssAsFile(String css) {
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
    }

    public static void showNotification(String text) {
        Notifications.create().title(App.TITLE).text(text).show();
    }
}