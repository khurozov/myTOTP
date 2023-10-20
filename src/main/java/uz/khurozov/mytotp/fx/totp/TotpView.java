package uz.khurozov.mytotp.fx.totp;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import uz.khurozov.mytotp.store.TotpData;
import uz.khurozov.totp.TOTP;

public class TotpView extends VBox {
    private final Text code;
    private final TotpData totpData;

    public TotpView(TotpData data) {
        totpData = data;

        TOTP totp = new TOTP(totpData.hmac(), totpData.secret(), totpData.passwordLength(), totpData.timeStep());

        code = new Text(totp.getCode());
        code.setFont(Font.font("Monospace", FontWeight.BLACK, FontPosture.REGULAR, 40));

        final Text title = new Text(totpData.name());
        title.setFont(Font.font("Monospace", FontWeight.NORMAL, FontPosture.REGULAR, 10));

        final TimeBar bar = new TimeBar(totp.getTimeStep(), () -> code.setText(totp.getCode()));
        bar.prefWidthProperty().bind(widthProperty());

        getChildren().addAll(code, title, bar);

        setSpacing(5);
        setPadding(new Insets(5));
        setFillWidth(true);
    }

    public TotpData getTotpData() {
        return totpData;
    }

    public String getCode() {
        return code.getText();
    }
}
