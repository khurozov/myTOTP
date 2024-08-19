package uz.khurozov.mytotp.fx.dialog.totpdata;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uz.khurozov.mytotp.store.TotpData;
import uz.khurozov.totp.Algorithm;
import uz.khurozov.totp.TOTP;

public class TotpDataManualDialog extends TotpDataDialog{
    public TotpDataManualDialog() {
        super();

        TextField label = new TextField();
        Label labelLabel = new Label("Label:");
        labelLabel.setLabelFor(label);

        TextField secret = new TextField();
        Label secretLabel = new Label("Secret:");
        secretLabel.setLabelFor(secret);

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

        getDialogPane().setContent(new VBox(10, labelLabel, label, secretLabel, secret, advanced));

        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                super.bind(label.textProperty(), secret.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return label.getText().isBlank() || secret.getText().isBlank();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new TotpData(
                        label.getText().trim(),
                        secret.getText().trim(),
                        algorithm.getValue(),
                        digits.getValue(),
                        period.getValue()
                );
            }
            return null;
        });

        setOnShowing(e -> Platform.runLater(() -> {
            advanced.setExpanded(false);
            digits.getValueFactory().setValue(6);
            algorithm.setValue(TOTP.DEFAULT_ALGORITHM);
            period.getValueFactory().setValue(30);

            label.requestFocus();
        }));
    }
}
