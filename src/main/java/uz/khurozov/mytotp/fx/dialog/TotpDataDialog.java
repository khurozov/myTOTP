package uz.khurozov.mytotp.fx.dialog;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uz.khurozov.mytotp.App;
import uz.khurozov.mytotp.store.TotpData;
import uz.khurozov.totp.HMAC;
import uz.khurozov.totp.TOTP;

public class TotpDataDialog extends Dialog<TotpData> {

    public TotpDataDialog() {
        setTitle(App.TITLE);

        TextField name = new TextField();
        Label nameLabel = new Label("Name:");
        nameLabel.setLabelFor(name);

        TextField secret = new TextField();
        Label secretLabel = new Label("Secret:");
        secretLabel.setLabelFor(secret);

        Spinner<Integer> passLen = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 10)
        );
        Label passLenLabel = new Label("Password length:");
        passLenLabel.setLabelFor(passLen);

        ChoiceBox<HMAC> hmac = new ChoiceBox<>();
        hmac.setItems(FXCollections.observableArrayList(HMAC.values()));
        Label hmacLabel = new Label("HMAC algorithm:");
        hmacLabel.setLabelFor(hmac);

        Spinner<Integer> timeStep = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 600, 30, 5)
        );
        Label timeStepLabel = new Label("Time step (seconds):");
        timeStepLabel.setLabelFor(timeStep);

        TitledPane advanced = new TitledPane("Advanced", new VBox(
                10, hmacLabel, hmac, passLenLabel, passLen, timeStepLabel, timeStep
        ));
        advanced.setExpanded(true);

        VBox vBox = new VBox(10, nameLabel, name, secretLabel, secret, advanced);

        getDialogPane().setContent(vBox);
        getDialogPane().setMinWidth(350);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                super.bind(name.textProperty(), secret.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return name.getText().isEmpty() || secret.getText().isEmpty();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new TotpData(
                        name.getText(),
                        secret.getText(),
                        hmac.getValue(),
                        passLen.getValue(),
                        timeStep.getValue() * 1000
                );
            }
            return null;
        });

        setOnShowing(e -> Platform.runLater(() -> {
            name.clear();
            secret.clear();

            advanced.setExpanded(false);
            passLen.getValueFactory().setValue(6);
            hmac.setValue(TOTP.DEFAULT_HMAC);
            timeStep.getValueFactory().setValue(30);

            name.requestFocus();
        }));
    }
}
