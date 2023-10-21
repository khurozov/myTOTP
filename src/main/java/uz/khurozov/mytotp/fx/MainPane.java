package uz.khurozov.mytotp.fx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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
import uz.khurozov.mytotp.fx.notification.Notifications;
import uz.khurozov.mytotp.fx.totp.TotpView;
import uz.khurozov.mytotp.store.TotpData;

import java.util.Map;

public class MainPane extends ScrollPane {
    private final VBox list;
    private EventHandler<ActionEvent> onItemAdded;
    private EventHandler<ActionEvent> onItemDeleted;

    public MainPane(TotpData ... initialData) {
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


        for (TotpData data : initialData) {
            addTotpView(data);
        }
    }

    private void addTotpView(TotpData totpData) {
        list.getChildren().add(new TotpView(totpData));

        if (onItemAdded != null) {
            onItemAdded.handle(new ActionEvent(totpData, this));
        }
    }

    public void setOnItemAdded(EventHandler<ActionEvent> onItemAdded) {
        this.onItemAdded = onItemAdded;
    }

    public void setOnItemDeleted(EventHandler<ActionEvent> onItemDeleted) {
        this.onItemDeleted = onItemDeleted;
    }

    public void add() {
        new TotpDataDialog().showAndWait().ifPresent(this::addTotpView);
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

        if (onItemDeleted != null) {
            onItemDeleted.handle(new ActionEvent(totpView.getTotpData(), this));
        }

        App.showNotification("Deleted");
    }
}
