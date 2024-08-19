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
    private final Text label;

    public TotpView(TotpData data) {
        TOTP totp = new TOTP(data.algorithm(), data.secret(), data.digits(), data.period());

        code = new Text(totp.getCode());
        code.setFont(Font.font("Monospace", FontWeight.BLACK, FontPosture.REGULAR, 40));

        label = new Text(data.label());
        label.setFont(Font.font("Monospace", FontWeight.NORMAL, FontPosture.REGULAR, 10));

        final TimeBar bar = new TimeBar(totp.getPeriod(), () -> code.setText(totp.getCode()));
        bar.prefWidthProperty().bind(widthProperty());

        getChildren().addAll(code, label, bar);

        setSpacing(5);
        setPadding(new Insets(5));
        setFillWidth(true);
    }

    public String getLabel() {
        return label.getText();
    }

    public String getCode() {
        return code.getText();
    }
}
