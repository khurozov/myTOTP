package uz.khurozov.mytotp.fx.dialog.totpdata;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import uz.khurozov.mytotp.App;
import uz.khurozov.mytotp.store.TotpData;

public abstract class TotpDataDialog extends Dialog<TotpData> {
    public TotpDataDialog() {
        setTitle(App.TITLE);
        getDialogPane().setMinWidth(350);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }
}
