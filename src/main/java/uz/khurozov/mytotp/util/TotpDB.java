package uz.khurozov.mytotp.util;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TotpDB {
    private static TotpDB instance;
    private static boolean isInitialized;

    private final String USERNAME;
    private final String PASSWORD;
    private final String JDBC_URL;

    private TotpDB(String username, String password) {
        USERNAME = username;
        PASSWORD = password;

        Path path = Path.of(System.getProperty("user.home"));

        if (System.getProperty("os.name").toUpperCase().startsWith("LINUX")) {
            path = path.resolve(".local/share");
        }

        JDBC_URL = "jdbc:h2:" + path + File.separator + "myTOTP";

        try (Connection con = getCon()) {
            con.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getCon() {
        try {
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void init(String username, String password) {
        if (isInitialized) {
            throw new IllegalStateException("DB has been initialized already");
        }
        instance = new TotpDB(username, password);
        // TODO create table if new db
        isInitialized = true;
    }

    public static synchronized TotpDB getInstance() {
        if (!isInitialized) {
            throw new IllegalStateException("DB has not been initialized yet");
        }
        return instance;
    }
}
