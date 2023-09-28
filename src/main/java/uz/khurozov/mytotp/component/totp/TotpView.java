package uz.khurozov.mytotp.component.totp;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import uz.khurozov.totp.TOTP;

public class TotpView extends VBox {
    private final Text code;

    public TotpView(String name, TOTP totp) {
        code = new Text(totp.getCode());
        code.setFont(Font.font("Monospace", FontWeight.BLACK, FontPosture.REGULAR, 40));

        final Text title = new Text(name);
        title.setFont(Font.font("Monospace", FontWeight.NORMAL, FontPosture.REGULAR, 10));

        final TimeBar bar = new TimeBar(totp.getTimeStep(), () -> code.setText(totp.getCode()));
        bar.prefWidthProperty().bind(widthProperty());

        getChildren().addAll(code, title, bar);

        setSpacing(5);
        setPadding(new Insets(5));
        setFillWidth(true);
    }

    public String getCode() {
        return code.getText();
    }
}
