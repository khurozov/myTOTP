package uz.khurozov.mytotp.fx;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.HPos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import uz.khurozov.mytotp.model.AuthData;

public class AuthDataDialog extends Dialog<AuthData> {
    private final Text err;

    public AuthDataDialog(String message) {
        setTitle("myTOTP");

        GridPane content = new GridPane();
        content.setVgap(10);

        int row = 0;
        if (message != null && !message.isBlank()) {
            Text messageNode = new Text(message);
            messageNode.setFont(Font.font(null, FontWeight.BOLD, 20));
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
        GridPane.setRowIndex(passwordText, row++);
        GridPane.setRowIndex(password, row++);

        content.getChildren().addAll(usernameText, username, passwordText, password);

        err = new Text();
        err.setFill(Color.RED);
        err.setFont(Font.font(null, FontPosture.ITALIC, -1));
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
