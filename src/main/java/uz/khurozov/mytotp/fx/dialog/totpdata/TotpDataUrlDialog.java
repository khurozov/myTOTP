package uz.khurozov.mytotp.fx.dialog.totpdata;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import uz.khurozov.mytotp.store.TotpData;

public class TotpDataUrlDialog extends TotpDataDialog{
    public TotpDataUrlDialog() {
        super();

        TextField url = new TextField();
        Label urlLabel = new Label("URL:");
        urlLabel.setLabelFor(url);

        getDialogPane().setContent(new VBox(urlLabel, url));

        getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(new BooleanBinding() {
            {
                super.bind(url.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return url.getText().isBlank() || !url.getText().trim().startsWith("otpauth://totp/");
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return TotpData.parseUrl(url.getText());
            }
            return null;
        });

        setOnShowing(e -> Platform.runLater(url::requestFocus));
    }
}
