package uz.khurozov.mytotp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uz.khurozov.mytotp.component.TotpListPane;
import uz.khurozov.mytotp.util.FXUtil;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new TotpListPane());
        stage.setTitle("myTOTP");
        stage.setScene(scene);
        stage.getIcons().add(FXUtil.getImage("logo.png"));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}