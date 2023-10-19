package uz.khurozov.mytotp.fx.dialog;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.HPos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import uz.khurozov.mytotp.App;
import uz.khurozov.mytotp.db.AuthData;

public class AuthDataDialog extends Dialog<AuthData> {
    private final Text err;

    public AuthDataDialog(String message) {
        setTitle(App.title);

        GridPane content = new GridPane();
        content.setVgap(10);
        content.setPrefWidth(350);

        int row = 0;
        if (message != null && !message.isBlank()) {
            Text messageNode = new Text(message);
            messageNode.setFont(Font.font(null, FontWeight.BOLD, 20));
            messageNode.setWrappingWidth(300);
            messageNode.setTextAlignment(TextAlignment.CENTER);
            GridPane.setHalignment(messageNode, HPos.CENTER);
            GridPane.setRowIndex(messageNode, row++);

            content.getChildren().add(messageNode);
        }

        Text usernameText = new Text("Username:");
        TextField username = new TextField();

        Text passwordText = new Text("Password:");
        PasswordField password = new PasswordField();

        GridPane.setRowIndex(usernameText, row++);
        GridPane.setRowIndex(username, row++);
        GridPane.setHgrow(username, Priority.ALWAYS);
        GridPane.setRowIndex(passwordText, row++);
        GridPane.setRowIndex(password, row++);
        GridPane.setHgrow(password, Priority.ALWAYS);

        content.getChildren().addAll(usernameText, username, passwordText, password);

        err = new Text();
        err.setFill(Color.RED);
        err.setFont(Font.font(null, FontPosture.ITALIC, -1));
        err.setWrappingWidth(300);
        err.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(err, HPos.CENTER);
        GridPane.setRowIndex(err, row);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                super.bind(username.textProperty(), password.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return username.getText().isEmpty() || password.getText().isEmpty();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new AuthData(
                        username.getText(),
                        password.getText()
                );
            }
            return null;
        });

        setOnShowing(e -> Platform.runLater(() -> {
            username.requestFocus();

            boolean contains = content.getChildren().contains(err);
            boolean isBlankError = err.getText().isBlank();
            if (contains && isBlankError) {
                content.getChildren().remove(err);
            } else if (!contains && !isBlankError) {
                content.getChildren().add(err);
            }
        }));
    }

    public void setError(String error) {
        err.setText(error);
    }
}
