package uz.khurozov.mytotp.fx;

import com.google.zxing.NotFoundException;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.stage.FileChooser;
import uz.khurozov.mytotp.App;
import uz.khurozov.mytotp.fx.dialog.AuthDialog;
import uz.khurozov.mytotp.fx.dialog.totpdata.TotpDataManualDialog;
import uz.khurozov.mytotp.fx.dialog.totpdata.TotpDataUrlDialog;
import uz.khurozov.mytotp.fx.totp.TotpView;
import uz.khurozov.mytotp.store.Store;
import uz.khurozov.mytotp.store.TotpData;
import uz.khurozov.mytotp.util.CryptoUtil;
import uz.khurozov.mytotp.util.QRCodeUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Consumer;

public class AppPane extends BorderPane {
    private final FileChooser fileChooser;
    private final ObjectProperty<Store> storeProp = new SimpleObjectProperty<>(null);
    private Consumer<TotpData> addTotpDataConsumer = null;

    public AppPane() {
        fileChooser = new FileChooser();
        fileChooser.setTitle(App.TITLE);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        MenuItem miNew = new MenuItem("New");
        miNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        miNew.setOnAction(e -> {
            File file = fileChooser.showSaveDialog(App.stage);
            if (file == null) return;

            new AuthDialog().showAndWait().ifPresent(data -> {
                try {
                    updateStore(Store.create(
                            file.toPath(),
                            CryptoUtil.getSecretKey(
                                    data.password().toCharArray(),
                                    data.username().getBytes(StandardCharsets.UTF_8)
                            )
                    ));
                } catch (Exception ex) {
                    App.showNotification(ex.getMessage());
                }
            });
        });

        MenuItem miOpen = new MenuItem("Open");
        miOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        miOpen.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(App.stage);
            if (file == null || !file.exists()) return;

            new AuthDialog().showAndWait().ifPresent(data -> {
                try {
                    updateStore(Store.open(
                            file.toPath(),
                            CryptoUtil.getSecretKey(
                                    data.password().toCharArray(),
                                    data.username().getBytes(StandardCharsets.UTF_8)
                            )
                    ));
                } catch (Exception ex) {
                    App.showNotification(ex.getMessage());
                }
            });
        });

        MenuItem miExport = new MenuItem("Export");
        miExport.disableProperty().bind(storeProp.isNull());
        miExport.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        miExport.setOnAction(e -> {
            File file = fileChooser.showSaveDialog(App.stage);
            if (file == null) return;

            try {
                Files.write(
                        file.toPath(),
                        storeProp.get().getAllData().stream()
                                .map(TotpData::toUrl)
                                .toList()
                );
            } catch (IOException ex) {
                App.showNotification(ex.getMessage());
            }
        });

        MenuItem miImport = new MenuItem("Import");
        miImport.disableProperty().bind(storeProp.isNull());
        miImport.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
        miImport.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(App.stage);
            if (file == null || !file.exists()) return;

            try {
                Files.readAllLines(file.toPath()).stream()
                        .map(TotpData::parseUrl)
                        .forEach(addTotpDataConsumer);
            } catch (IOException ex) {
                App.showNotification(ex.getMessage());
            }
        });

        setTop(new MenuBar(new Menu("Store", null, miNew, miOpen, miExport, miImport)));
        setCenter(new Text("Create new store or open"));

        setPrefWidth(360);
        setPrefHeight(480);
    }

    private void updateStore(Store store) {
        storeProp.set(store);
        VBox list = new VBox();

        addTotpDataConsumer = totpData -> {
            if (store.existsByLabel(totpData.label())) {
                App.showNotification("Label exists");
            } else {
                list.getChildren().add(new TotpView(totpData));
                store.add(totpData);
            }
        };

        ScrollPane scrollPane = new ScrollPane(list);

        list.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        MenuItem addManual = new MenuItem("Manually");
        addManual.setOnAction(e -> new TotpDataManualDialog().showAndWait().ifPresent(addTotpDataConsumer));

        MenuItem addUrl = new MenuItem("From url");
        addUrl.setOnAction(e -> new TotpDataUrlDialog("").showAndWait().ifPresent(addTotpDataConsumer));

        MenuItem addQrCode = new MenuItem("From QR code");
        addQrCode.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(App.stage);
            if (file == null) return;

            String url;
            try {
                url = QRCodeUtil.read(file);
            } catch (NotFoundException | IOException ex) {
                App.showNotification("Error on reading QR code: " + ex.getMessage());
                return;
            }

            new TotpDataUrlDialog(url).showAndWait().ifPresent(addTotpDataConsumer);
        });

        Menu mAdd = new Menu("Add", new ImageView(App.getResourceAsExternal("/images/add.png")), addManual, addUrl, addQrCode);

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
            store.deleteByLabel(totpView.getLabel());

            App.showNotification("Deleted");
        });

        ContextMenu contextMenu = new ContextMenu(mAdd, miCopy, miDelete);
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
