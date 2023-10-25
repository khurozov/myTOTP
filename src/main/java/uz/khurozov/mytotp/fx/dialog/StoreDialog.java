package uz.khurozov.mytotp.fx.dialog;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import uz.khurozov.mytotp.App;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;

public class StoreDialog extends Dialog<StoreDialog.Data> {
    public StoreDialog(boolean isNew) {
        setTitle(App.TITLE);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(App.TITLE);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        Supplier<Path> supplier;
        if (isNew) {
            supplier = () -> {
                File file = fileChooser.showSaveDialog(App.stage);
                return file == null ? null : file.toPath();
            };
        } else {
            supplier = () -> {
                File file = fileChooser.showOpenDialog(App.stage);
                return file == null ? null : file.toPath();
            };
        }

        TextField fileName = new TextField();
        fileName.setEditable(false);
        fileName.setFocusTraversable(false);

        Property<Path> pathProperty = new SimpleObjectProperty<>();
        pathProperty.addListener(observable -> fileName.setText(
                pathProperty.getValue() == null ? "" : pathProperty.getValue().toString()
        ));

        Button btnBrowse = new Button(isNew ? "New" : "Open");
        btnBrowse.setOnAction(e -> pathProperty.setValue(supplier.get()));

        TextField username = new TextField();
        Label usernameLabel = new Label("Username:");
        usernameLabel.setLabelFor(username);

        PasswordField password = new PasswordField();
        Label passwordLabel = new Label("Password:");
        passwordLabel.setLabelFor(password);

        GridPane.setConstraints(fileName, 0, 0);
        GridPane.setConstraints(btnBrowse, 1, 0);
        GridPane.setConstraints(usernameLabel, 0, 1);
        GridPane.setConstraints(username, 0, 2);
        GridPane.setConstraints(passwordLabel, 0, 3);
        GridPane.setConstraints(password, 0, 4);

        GridPane.setColumnSpan(usernameLabel, 2);
        GridPane.setColumnSpan(username, 2);
        GridPane.setColumnSpan(passwordLabel, 2);
        GridPane.setColumnSpan(password, 2);

        GridPane content = new GridPane();
        content.setVgap(10);
        content.setHgap(10);
        content.getColumnConstraints().setAll(
                new ColumnConstraints(300, 300, 300, Priority.ALWAYS, HPos.LEFT, true),
                new ColumnConstraints(60, 60, 60, Priority.ALWAYS, HPos.CENTER, true)
        );

        content.getChildren().setAll(
                fileName, btnBrowse,
                usernameLabel, username,
                passwordLabel, password
        );

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                bind(username.textProperty(), password.textProperty(), pathProperty);
            }

            @Override
            protected boolean computeValue() {
                return username.getText().isBlank() || password.getText().isBlank() || pathProperty.getValue() == null;
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new Data(
                        pathProperty.getValue(),
                        username.getText(),
                        password.getText()
                );
            }
            return null;
        });
    }

    public record Data(Path path, String username, String password) {}
}
