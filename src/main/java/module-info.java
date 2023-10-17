module uz.khurozov.mytotp {
    requires javafx.controls;

    requires totp;
    requires java.sql;
    requires com.h2database;
    requires java.desktop;

    exports uz.khurozov.mytotp;
}