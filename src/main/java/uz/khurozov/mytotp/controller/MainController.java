package uz.khurozov.mytotp.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import uz.khurozov.mytotp.component.TotpView;
import uz.khurozov.mytotp.dialog.TotpDataDialog;

public class MainController {
    private final TotpDataDialog addDialog = new TotpDataDialog();

    @FXML
    private VBox list;

    @FXML
    void newTotp() {
        addDialog.showAndWait().ifPresent(
                totpData -> list.getChildren().add(new TotpView(totpData.name(), totpData.totp()))
        );
    }

}
