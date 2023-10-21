package uz.khurozov.mytotp.fx;

import javafx.event.ActionEvent;
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
    private final Store store;
    private final VBox list;

    public MainPane(Store store) {
        this.store = store;

        list = new VBox();
        list.setFillWidth(true);
        list.prefWidthProperty().bind(widthProperty());
        setContent(list);

        setPrefWidth(350);
        setPrefHeight(500);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(true);


        MenuItem miAdd = new MenuItem("Add", new ImageView(App.getResourceAsExternal("/images/add.png")));
        miAdd.setOnAction(e -> add());

        MenuItem miCopy = new MenuItem("Copy", new ImageView(App.getResourceAsExternal("/images/copy.png")));
        miCopy.setOnAction(this::copyTotpCode);

        MenuItem miDelete = new MenuItem("Delete", new ImageView(App.getResourceAsExternal("/images/delete.png")));
        miDelete.setOnAction(this::deleteTotpView);

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

    public void add() {
        new TotpDataDialog().showAndWait().ifPresent(totpData -> {
            if (store.existsByName(totpData.name())) {
                App.showNotification("Name exists");
            } else {
                list.getChildren().add(new TotpView(totpData));
                store.add(totpData);
            }
        });
    }

    private void copyTotpCode(ActionEvent e) {
        TotpView totpView = (TotpView) ((MenuItem) e.getSource()).getUserData();
        String code = totpView.getCode();

        Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, code));

        App.showNotification("Code copied");
    }

    private void deleteTotpView(ActionEvent e) {
        TotpView totpView = (TotpView) ((MenuItem) e.getSource()).getUserData();

        list.getChildren().remove(totpView);
        store.deleteByName(totpView.getName());

        App.showNotification("Deleted");
    }
}
