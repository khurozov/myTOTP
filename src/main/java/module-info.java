module uz.khurozov.mytotp {
    requires javafx.controls;
    requires javafx.fxml;

    requires totp;

    opens uz.khurozov.mytotp to javafx.fxml;
    exports uz.khurozov.mytotp;
    exports uz.khurozov.mytotp.controller;
    opens uz.khurozov.mytotp.controller to javafx.fxml;
}