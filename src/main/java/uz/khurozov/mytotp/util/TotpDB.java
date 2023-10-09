package uz.khurozov.mytotp.util;

import java.io.File;
import java.nio.file.Path;
import java.sql.*;

public class TotpDB {
    private static TotpDB instance;
    private static boolean isInitialized;

    private final String USERNAME;
    private final String PASSWORD;
    private final String JDBC_URL;

    private TotpDB(String username, String password) {
        USERNAME = username;
        int x = password.indexOf(' ');
        String filePasswd = x == -1 ? password : password.substring(0, x);
        PASSWORD = filePasswd + " " + password;

        JDBC_URL = "jdbc:h2:" + getFilePath() + ";CIPHER=FOG;TRACE_LEVEL_FILE=0";
    }

    public static String getFilePath() {
        Path path = Path.of(System.getProperty("user.home"));

        if (System.getProperty("os.name").toUpperCase().startsWith("LINUX")) {
            path = path.resolve(".local/share");
        }

        return path + File.separator + "myTOTP";
    }

    private Connection getCon() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static synchronized void init(String username, String password) throws IllegalAccessException {
        if (isInitialized) {
            throw new IllegalStateException("DB has been initialized already");
        }
        instance = new TotpDB(username, password);
        // TODO create table if new db
        try (Statement statement = instance.getCon().createStatement()){
            statement.execute("""
                            CREATE TABLE IF NOT EXISTS totps (
                                id int primary key auto_increment,
                                name text not null,
                                secret text unique not null,
                                hmac varchar(10) not null,
                                password_length int not null,
                                time_step long not null
                            )
                            """);
        } catch (SQLInvalidAuthorizationSpecException | SQLNonTransientConnectionException e) {
            throw new IllegalAccessException("Wrong username or password");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        isInitialized = true;
    }

    public static synchronized TotpDB getInstance() {
        if (!isInitialized) {
            throw new IllegalStateException("DB has not been initialized yet");
        }
        return instance;
    }
}
