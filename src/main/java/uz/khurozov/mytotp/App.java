package uz.khurozov.mytotp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import uz.khurozov.mytotp.fx.MainPane;
import uz.khurozov.mytotp.fx.dialog.AuthDataDialog;
import uz.khurozov.mytotp.model.AuthData;
import uz.khurozov.mytotp.model.TotpData;
import uz.khurozov.mytotp.util.GuiUtil;
import uz.khurozov.mytotp.util.TotpDB;

import java.awt.*;
import java.io.IOException;
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
        stage.getIcons().addAll(GuiUtil.getFXImage("logo_32.png"), GuiUtil.getFXImage("logo_128.png"));
        stage.setResizable(false);
        addTrayIcon(stage);

        stage.show();
    }

    private static void addTrayIcon(Stage stage) {
        if (SystemTray.isSupported()) {
            try {
                Platform.setImplicitExit(false);

                TrayIcon trayIcon = new TrayIcon(
                        GuiUtil.getAWTImage("logo_16.png"),
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
}