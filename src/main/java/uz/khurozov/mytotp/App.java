package uz.khurozov.mytotp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import uz.khurozov.mytotp.fx.MainPane;
import uz.khurozov.mytotp.fx.dialog.AuthDataDialog;
import uz.khurozov.mytotp.db.AuthData;
import uz.khurozov.mytotp.db.TotpData;
import uz.khurozov.mytotp.db.TotpDB;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class App extends Application {

    public static final String title = "myTOTP";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        AuthDataDialog authDataDialog = new AuthDataDialog("Login");

        boolean isAuthCompleted = false;
        do {
            Optional<AuthData> authDataOpt = authDataDialog.showAndWait();

            if (authDataOpt.isEmpty()) {
                return;
            }

            AuthData authData = authDataOpt.get();

            try {
                TotpDB.init(authData.username(), authData.password());

                isAuthCompleted = true;
            } catch (IllegalAccessException e) {
                authDataDialog.setError(e.getMessage());
            }

        } while (!isAuthCompleted);

        TotpDB db = TotpDB.getInstance();
        TotpData[] all = db.getAll();

        MainPane mainPane = new MainPane(all);
        mainPane.setOnItemAdded(e -> db.add((TotpData) e.getSource()));
        mainPane.setOnItemDeleted(e -> db.remove((TotpData) e.getSource()));

        Scene scene = new Scene(mainPane);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), mainPane::add);
        stage.setTitle(App.title);
        stage.setScene(scene);
        stage.getIcons().addAll(
                new Image(App.getResourceAsExternal("/images/logo_32.png")),
                new Image(App.getResourceAsExternal("/images/logo_128.png"))
        );
        stage.setResizable(false);
        addTrayIcon(stage);

        stage.show();
    }

    private static void addTrayIcon(Stage stage) {
        if (SystemTray.isSupported()) {
            try {
                Platform.setImplicitExit(false);

                TrayIcon trayIcon = new TrayIcon(
                        ImageIO.read(App.getResourceAsStream("/images/logo_16.png")),
                        App.title,
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