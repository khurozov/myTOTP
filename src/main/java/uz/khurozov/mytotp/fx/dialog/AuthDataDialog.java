package uz.khurozov.mytotp.fx.dialog;

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

    public AuthDataDialog() {
        setTitle(App.TITLE);

        GridPane content = new GridPane();
        content.setVgap(10);
        content.setPrefWidth(350);

        Text messageNode = new Text("Login");
        messageNode.setFont(Font.font(null, FontWeight.BOLD, 20));
        messageNode.setWrappingWidth(300);
        messageNode.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(messageNode, HPos.CENTER);
        GridPane.setRowIndex(messageNode, 0);

        Text usernameText = new Text("Username:");
        GridPane.setRowIndex(usernameText, 1);
        GridPane.setColumnSpan(usernameText, 2);

        TextField username = new TextField();
        GridPane.setRowIndex(username, 2);
        GridPane.setHgrow(username, Priority.ALWAYS);

        Text passwordText = new Text("Password:");
        GridPane.setRowIndex(passwordText, 3);
        GridPane.setColumnSpan(passwordText, 2);

        PasswordField password = new PasswordField();
        GridPane.setRowIndex(password, 4);
        GridPane.setHgrow(password, Priority.ALWAYS);

        err = new Text();
        err.setFill(Color.RED);
        err.setFont(Font.font(null, FontPosture.ITALIC, 12));
        err.setWrappingWidth(300);
        err.setTextAlignment(TextAlignment.CENTER);
        GridPane.setHalignment(err, HPos.CENTER);
        GridPane.setRowIndex(err, 5);

        content.getChildren().setAll(messageNode, usernameText, username, passwordText, password, err);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                bind(username.textProperty(), password.textProperty());
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
        username.requestFocus();
    }

    public void setError(String error) {
        err.setText(error);
    }
}
