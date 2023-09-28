package uz.khurozov.mytotp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import uz.khurozov.mytotp.component.totp.TotpView;
import uz.khurozov.mytotp.component.notification.Notification;
import uz.khurozov.mytotp.dialog.TotpDataDialog;
import uz.khurozov.mytotp.util.FXUtil;

import java.util.Map;

public class MainController {
    private final TotpDataDialog addDialog = new TotpDataDialog();

    @FXML
    private VBox list;

    @FXML
    void newTotp() {
        addDialog.showAndWait().ifPresent(
                totpData -> {
                    TotpView totpView = new TotpView(totpData.name(), totpData.totp());

                    MenuItem copy = new MenuItem("Copy", FXUtil.getCopyIcon());
                    copy.setOnAction(e -> {
                        Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, totpView.getCode()));
                        new Notification(null, "Code copied", Duration.seconds(2)).show();
                    });

                    MenuItem delete = new MenuItem("Delete", FXUtil.getDeleteSvg());
                    delete.setOnAction(e -> {
                        list.getChildren().remove(totpView);
                    });

                    ContextMenu contextMenu = new ContextMenu(copy, delete);
                    contextMenu.setAutoHide(true);
                    totpView.setOnContextMenuRequested(e -> contextMenu.show(totpView, e.getScreenX(), e.getScreenY()));

                    list.getChildren().add(totpView);
                }
        );
    }

}
