package uz.khurozov.mytotp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uz.khurozov.mytotp.crypto.CryptoUtil;
import uz.khurozov.mytotp.fx.MainPane;
import uz.khurozov.mytotp.fx.dialog.StoreFileDataDialog;
import uz.khurozov.mytotp.fx.notification.Notifications;
import uz.khurozov.mytotp.store.Store;
import uz.khurozov.mytotp.store.StoreFileData;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

public class App extends Application {

    public static final String TITLE = "myTOTP";
    public static Stage stage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        App.stage = stage;
        Store store = openStore();

        if (store == null) return;

        MainPane mainPane = new MainPane(store);
        Scene scene = new Scene(mainPane);
        stage.setTitle(App.TITLE);
        stage.setScene(scene);
        stage.getIcons().addAll(
                new Image(App.getResourceAsExternal("/images/logo_32.png")),
                new Image(App.getResourceAsExternal("/images/logo_128.png"))
        );
        stage.setResizable(false);
        addTrayIcon(stage);

        stage.show();
    }

    private static Store openStore() {
        StoreFileDataDialog storeFileDataDialog = new StoreFileDataDialog();

        while (true) {
            Optional<StoreFileData> authDataOpt = storeFileDataDialog.showAndWait();

            if (authDataOpt.isEmpty()) {
                return null;
            }

            try {
                StoreFileData data = authDataOpt.get();
                Path path = data.file().toPath();
                SecretKey secretKey = CryptoUtil.getSecretKey(
                        data.password().toCharArray(),
                        data.username().getBytes(StandardCharsets.UTF_8)
                );

                if (data.file().exists()) {
                    return Store.open(path, secretKey);
                } else {
                    return Store.create(path, secretKey);
                }
            } catch (Exception e){
                storeFileDataDialog.setError(e.getMessage());
            }
        }
    }

    private static void addTrayIcon(Stage stage) {
        if (SystemTray.isSupported()) {
            try {
                Platform.setImplicitExit(false);

                TrayIcon trayIcon = new TrayIcon(
                        ImageIO.read(App.getResourceAsStream("/images/logo_16.png")),
                        App.TITLE,
                        trayPopupMenu(stage)
                );

                trayIcon.addActionListener(e -> Platform.runLater(() -> {
                    stage.show();
                    stage.toFront();
                }));

                SystemTray.getSystemTray().add(trayIcon);
            } catch (IOException | AWTException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static PopupMenu trayPopupMenu(Stage stage) {
        PopupMenu popupMenu = new PopupMenu();

        MenuItem miShowHide = new MenuItem("Show/Hide");
        miShowHide.addActionListener(e -> Platform.runLater(() -> {
            if (stage.isShowing()) {
                stage.toBack();
                stage.hide();
            } else {
                stage.show();
                stage.toFront();
            }
        }));
        popupMenu.add(miShowHide);

        MenuItem miExit = new MenuItem("Exit");
        miExit.addActionListener(e -> {
            Platform.exit();
            System.exit(0);
        });
        popupMenu.add(miExit);

        return popupMenu;
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