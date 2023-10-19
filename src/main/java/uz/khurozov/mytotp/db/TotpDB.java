package uz.khurozov.mytotp.db;

import uz.khurozov.totp.HMAC;

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
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
                                name text not null,
                                secret text not null,
                                hmac varchar(10) not null,
                                password_length int not null,
                                time_step long not null,
                                primary key (name, secret)
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

    public TotpData[] getAll() {
        try (Statement statement = getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = statement.executeQuery("select * from totps");

            rs.last();
            int rowsCount = rs.getRow();

            if (rowsCount == 0) {
                return new TotpData[0];
            }

            rs.beforeFirst();

            int i = 0;
            TotpData[] all = new TotpData[rowsCount];

            while (rs.next()) {
                all[i++] = new TotpData(
                        rs.getString("name"),
                        rs.getString("secret"),
                        HMAC.valueOf(rs.getString("hmac")),
                        rs.getInt("password_length"),
                        rs.getLong("time_step")
                );
            }

            return all;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(TotpData totpData) {
        try (
            PreparedStatement statement = getCon().prepareStatement(
                    "insert into totps (name, secret, hmac, password_length, time_step) values (?, ?, ?, ?, ?)"
            )
        ) {
            statement.setString(1, totpData.name());
            statement.setString(2, totpData.secret());
            statement.setString(3, totpData.hmac().name());
            statement.setInt(4, totpData.passwordLength());
            statement.setLong(5, totpData.timeStep());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(TotpData totpData) {
        try (
            PreparedStatement statement = getCon().prepareStatement(
                    "delete from totps where name=? and secret=?"
            )
        ) {
            statement.setString(1, totpData.name());
            statement.setString(2, totpData.secret());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
