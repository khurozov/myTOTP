package uz.khurozov.mytotp.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import uz.khurozov.mytotp.component.notification.Notifications;
import uz.khurozov.mytotp.component.totp.TotpView;
import uz.khurozov.mytotp.dialog.TotpDataDialog;
import uz.khurozov.mytotp.util.FXUtil;

import java.util.Map;

public class MainController {
    private final TotpDataDialog addDialog = new TotpDataDialog();

    @FXML
    private VBox list;

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            list.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), this::newTotp);

            MenuItem miAdd = new MenuItem("Add", new ImageView(FXUtil.getImage("add.png")));
            miAdd.setOnAction(e -> this.newTotp());

            MenuItem miCopy = new MenuItem("Copy", new ImageView(FXUtil.getImage("copy.png")));
            miCopy.setOnAction(this::copyTotpCode);

            MenuItem miDelete = new MenuItem("Delete", new ImageView(FXUtil.getImage("delete.png")));
            miDelete.setOnAction(this::deleteTotpView);

            ContextMenu contextMenu = new ContextMenu(miAdd, miCopy, miDelete);
            contextMenu.setAutoHide(true);

            list.getParent().setOnContextMenuRequested(e -> {
                TotpView totpView = null;
                if (e.getTarget() instanceof TotpView) {
                    totpView = ((TotpView) e.getTarget());
                }
                if (((Node) e.getTarget()).getParent() instanceof TotpView) {
                    totpView = ((TotpView) ((Node) e.getTarget()).getParent());
                }

                miCopy.setDisable(totpView == null);
                miCopy.setUserData(totpView);
                miDelete.setDisable(totpView == null);
                miDelete.setUserData(totpView);

                contextMenu.show(list, e.getScreenX(), e.getScreenY());
            });
        });
    }

    private void newTotp() {
        addDialog.showAndWait().ifPresent(
                totpData -> {
                    list.getChildren().add(new TotpView(totpData.name(), totpData.totp()));
                }
        );
    }

    private void copyTotpCode(ActionEvent e) {
        TotpView totpView = (TotpView) ((MenuItem) e.getSource()).getUserData();
        String code = totpView.getCode();

        Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, code));

        Notifications.create().text("Code copied").show();
    }

    private void deleteTotpView(ActionEvent e) {
        TotpView totpView = (TotpView) ((MenuItem) e.getSource()).getUserData();

        list.getChildren().remove(totpView);

        Notifications.create().text("Deleted").position(Pos.TOP_RIGHT).show();
    }

}
