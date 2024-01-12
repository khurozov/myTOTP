package uz.khurozov.mytotp.fx.dialog;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uz.khurozov.mytotp.App;
import uz.khurozov.mytotp.store.TotpData;
import uz.khurozov.totp.Algorithm;
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

        TextField issuer = new TextField();
        Label issuerLabel = new Label("Issuer:");
        issuerLabel.setLabelFor(issuer);

        Spinner<Integer> digits = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 10)
        );
        Label digitsLabel = new Label("Digits:");
        digitsLabel.setLabelFor(digits);

        ChoiceBox<Algorithm> algorithm = new ChoiceBox<>();
        algorithm.setItems(FXCollections.observableArrayList(Algorithm.values()));
        Label algorithmLabel = new Label("Algorithm:");
        algorithmLabel.setLabelFor(algorithm);

        Spinner<Integer> period = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 600, 30, 5)
        );
        Label periodLabel = new Label("Period (seconds):");
        periodLabel.setLabelFor(period);

        TitledPane advanced = new TitledPane("Advanced", new VBox(
                10, algorithmLabel, algorithm, digitsLabel, digits, periodLabel, period
        ));
        advanced.setExpanded(true);

        VBox vBox = new VBox(10, nameLabel, name, secretLabel, secret, issuerLabel, issuer, advanced);

        getDialogPane().setContent(vBox);
        getDialogPane().setMinWidth(350);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                super.bind(name.textProperty(), secret.textProperty(), issuer.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return name.getText().isEmpty() || secret.getText().isBlank() || issuer.getText().isBlank();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new TotpData(
                        name.getText(),
                        secret.getText(),
                        issuer.getText(),
                        algorithm.getValue(),
                        digits.getValue(),
                        period.getValue()
                );
            }
            return null;
        });

        setOnShowing(e -> Platform.runLater(() -> {
            name.clear();
            secret.clear();
            issuer.clear();

            advanced.setExpanded(false);
            digits.getValueFactory().setValue(6);
            algorithm.setValue(TOTP.DEFAULT_ALGORITHM);
            period.getValueFactory().setValue(30);

            name.requestFocus();
        }));
    }
}
