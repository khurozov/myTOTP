package uz.khurozov.mytotp.fx;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.VBox;
import uz.khurozov.mytotp.App;
import uz.khurozov.mytotp.fx.dialog.TotpDataDialog;
import uz.khurozov.mytotp.fx.totp.TotpView;
import uz.khurozov.mytotp.store.Store;
import uz.khurozov.mytotp.store.TotpData;

import java.util.Map;

public class MainPane extends ScrollPane {
    public MainPane(Store store) {

        VBox list = new VBox();
        list.setFillWidth(true);
        list.prefWidthProperty().bind(widthProperty());
        setContent(list);

        setPrefWidth(350);
        setPrefHeight(500);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(true);


        MenuItem miAdd = new MenuItem("Add", new ImageView(App.getResourceAsExternal("/images/add.png")));
        miAdd.setOnAction(e -> new TotpDataDialog().showAndWait().ifPresent(totpData -> {
            if (store.existsByName(totpData.name())) {
                App.showNotification("Name exists");
            } else {
                list.getChildren().add(new TotpView(totpData));
                store.add(totpData);
            }
        }));

        MenuItem miCopy = new MenuItem("Copy", new ImageView(App.getResourceAsExternal("/images/copy.png")));
        miCopy.setOnAction(e -> {
            TotpView totpView = (TotpView) ((MenuItem) e.getSource()).getUserData();
            String code = totpView.getCode();

            Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, code));

            App.showNotification("Code copied");
        });

        MenuItem miDelete = new MenuItem("Delete", new ImageView(App.getResourceAsExternal("/images/delete.png")));
        miDelete.setOnAction(e -> {
            TotpView totpView = (TotpView) ((MenuItem) e.getSource()).getUserData();

            list.getChildren().remove(totpView);
            store.deleteByName(totpView.getName());

            App.showNotification("Deleted");
        });

        ContextMenu contextMenu = new ContextMenu(miAdd, miCopy, miDelete);
        contextMenu.setAutoHide(true);
        setOnContextMenuRequested(e -> {
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
        });
        setContextMenu(contextMenu);


        for (TotpData data : store.getAllData()) {
            list.getChildren().add(new TotpView(data));
        }
    }
}
