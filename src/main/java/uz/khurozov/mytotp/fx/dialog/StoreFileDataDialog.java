package uz.khurozov.mytotp.fx.dialog;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import uz.khurozov.mytotp.App;
import uz.khurozov.mytotp.store.StoreFileData;

import java.io.File;

public class StoreFileDataDialog extends Dialog<StoreFileData> {
    private final Text err;

    public StoreFileDataDialog() {
        setTitle(App.TITLE);

        GridPane content = new GridPane();
        content.setVgap(10);
        content.setPrefWidth(350);

        Text messageNode = new Text("Create/Select store file");
        messageNode.setFont(Font.font(null, FontWeight.BOLD, 20));
        messageNode.setWrappingWidth(300);
        messageNode.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(messageNode, HPos.CENTER);
        GridPane.setRowIndex(messageNode, 0);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(App.TITLE+" store file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        Text fileName = new Text();
        fileName.setWrappingWidth(340);
        GridPane.setRowIndex(fileName, 1);
        GridPane.setColumnSpan(fileName, 2);

        Property<File> fileProperty = new SimpleObjectProperty<>();
        fileProperty.addListener(observable -> fileName.setText(
                fileProperty.getValue() == null ? "" : fileProperty.getValue().getAbsolutePath()
        ));

        Button btnCreate = new Button("Create");
        btnCreate.setOnAction(e -> fileProperty.setValue(fileChooser.showSaveDialog(App.getActiveWindow())));
        GridPane.setConstraints(btnCreate, 0, 2);
        GridPane.setHgrow(btnCreate, Priority.ALWAYS);

        Button btnSelect = new Button("Select");
        btnSelect.setOnAction(e -> fileProperty.setValue(fileChooser.showOpenDialog(App.getActiveWindow())));
        GridPane.setConstraints(btnSelect, 1, 2);
        GridPane.setHgrow(btnSelect, Priority.ALWAYS);

        Text usernameText = new Text("Username:");
        GridPane.setRowIndex(usernameText, 3);
        GridPane.setColumnSpan(usernameText, 2);

        TextField username = new TextField();
        GridPane.setRowIndex(username, 4);
        GridPane.setColumnSpan(username, 2);
        GridPane.setHgrow(username, Priority.ALWAYS);

        Text passwordText = new Text("Password:");
        GridPane.setRowIndex(passwordText, 5);
        GridPane.setColumnSpan(passwordText, 2);

        PasswordField password = new PasswordField();
        GridPane.setRowIndex(password, 6);
        GridPane.setColumnSpan(password, 2);
        GridPane.setHgrow(password, Priority.ALWAYS);

        err = new Text();
        err.setFill(Color.RED);
        err.setFont(Font.font(null, FontPosture.ITALIC, 12));
        err.setWrappingWidth(300);
        err.setTextAlignment(TextAlignment.CENTER);
        err.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(err, HPos.CENTER);
        GridPane.setRowIndex(err, 7);
        GridPane.setColumnSpan(err, 2);

        content.getChildren().setAll(
                messageNode,
                fileName, btnCreate, btnSelect,
                usernameText, username,
                passwordText, password,
                err
        );

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                bind(username.textProperty(), password.textProperty(), fileProperty);
            }

            @Override
            protected boolean computeValue() {
                return username.getText().isBlank() || password.getText().isBlank() || fileProperty.getValue() == null;
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new StoreFileData(
                        fileProperty.getValue(),
                        username.getText(),
                        password.getText()
                );
            }
            return null;
        });
        username.requestFocus();
    }

    public void setError(String error) {
        err.setText(error);
    }
}
