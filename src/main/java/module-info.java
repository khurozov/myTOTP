module uz.khurozov.mytotp {
    requires javafx.controls;
    requires javafx.fxml;

    requires totp;
    requires java.sql;

    opens uz.khurozov.mytotp to javafx.fxml;
    exports uz.khurozov.mytotp;
}