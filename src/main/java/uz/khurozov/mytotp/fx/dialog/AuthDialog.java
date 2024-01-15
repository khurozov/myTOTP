package uz.khurozov.mytotp.fx.dialog;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uz.khurozov.mytotp.App;

public class AuthDialog extends Dialog<AuthDialog.Data> {
    public AuthDialog() {
        setTitle(App.TITLE);

        TextField username = new TextField();
        Label usernameLabel = new Label("Username:");
        usernameLabel.setLabelFor(username);

        PasswordField password = new PasswordField();
        Label passwordLabel = new Label("Password:");
        passwordLabel.setLabelFor(password);

        VBox vBox = new VBox(usernameLabel, username, passwordLabel, password);
        vBox.setSpacing(10);

        getDialogPane().setContent(vBox);
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
                return new Data(
                        username.getText(),
                        password.getText()
                );
            }
            return null;
        });

        setOnShowing(e -> username.requestFocus());
    }

    public record Data(String username, String password) {}
}
