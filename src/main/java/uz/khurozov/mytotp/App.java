package uz.khurozov.mytotp;

import javafx.application.Application;
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

import java.util.Optional;

public class App extends Application {
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
        stage.setTitle("myTOTP");
        stage.setScene(scene);
        stage.getIcons().add(GuiUtil.getFXImage("logo.png"));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}