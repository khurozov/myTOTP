package uz.khurozov.mytotp.fx;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uz.khurozov.mytotp.App;
import uz.khurozov.mytotp.crypto.CryptoUtil;
import uz.khurozov.mytotp.fx.dialog.StoreDialog;
import uz.khurozov.mytotp.fx.dialog.TotpDataDialog;
import uz.khurozov.mytotp.fx.totp.TotpView;
import uz.khurozov.mytotp.store.Store;
import uz.khurozov.mytotp.store.TotpData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AppPane extends BorderPane {
    public AppPane() {
        MenuItem miNew = new MenuItem("New");
        miNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        miNew.setOnAction(e -> new StoreDialog(true).showAndWait().ifPresent(data -> {
            try {
                updateStore(Store.create(
                        data.path(),
                        CryptoUtil.getSecretKey(
                                data.password().toCharArray(),
                                data.username().getBytes(StandardCharsets.UTF_8)
                        )
                ));
            } catch (Exception ex) {
                App.showNotification(ex.getMessage());
            }
        }));

        MenuItem miOpen = new MenuItem("Open");
        miOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        miOpen.setOnAction(e -> new StoreDialog(false).showAndWait().ifPresent(data -> {
            try {
                updateStore(Store.open(
                        data.path(),
                        CryptoUtil.getSecretKey(
                                data.password().toCharArray(),
                                data.username().getBytes(StandardCharsets.UTF_8)
                        )
                ));
            } catch (Exception ex) {
                App.showNotification(ex.getMessage());
            }
        }));

        setTop(new MenuBar(new Menu("Store", null, miNew, miOpen)));
        setCenter(new Text("Create new store or open"));

        setPrefWidth(360);
        setPrefHeight(480);
    }

    private void updateStore(Store store) {
        VBox list = new VBox();
        ScrollPane scrollPane = new ScrollPane(list);

        list.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

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
        scrollPane.setOnContextMenuRequested(e -> {
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
        scrollPane.setContextMenu(contextMenu);

        for (TotpData data : store.getAllData()) {
            list.getChildren().add(new TotpView(data));
        }

        setCenter(scrollPane);
        checkTrayIconExists();
    }

    private void checkTrayIconExists() {
        if (SystemTray.isSupported() && SystemTray.getSystemTray().getTrayIcons().length == 0) {
            try {
                Platform.setImplicitExit(false);

                PopupMenu popupMenu = new PopupMenu();

                java.awt.MenuItem miShowHide = new java.awt.MenuItem("Show/Hide");
                miShowHide.addActionListener(e -> Platform.runLater(() -> {
                    if (App.stage.isShowing()) {
                        App.stage.toBack();
                        App.stage.hide();
                    } else {
                        App.stage.show();
                        App.stage.toFront();
                    }
                }));
                popupMenu.add(miShowHide);

                java.awt.MenuItem miExit = new java.awt.MenuItem("Exit");
                miExit.addActionListener(e -> {
                    Platform.exit();
                    System.exit(0);
                });
                popupMenu.add(miExit);

                TrayIcon trayIcon = new TrayIcon(
                        ImageIO.read(App.getResourceAsStream("/images/logo_16.png")),
                        App.TITLE,
                        popupMenu
                );

                trayIcon.addActionListener(e -> Platform.runLater(() -> {
                    App.stage.show();
                    App.stage.toFront();
                }));

                SystemTray.getSystemTray().add(trayIcon);
            } catch (IOException | AWTException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
