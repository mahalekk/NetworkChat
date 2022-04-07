package chat.server.authentication;

import java.sql.*;

public class DBAuthenticationService implements AuthenticationService{
    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) throws SQLException, ClassNotFoundException {
        connection();
        rs = stmt.executeQuery(String.format("SELECT * FROM auth WHERE login = '%s'", login));
        if (rs.isClosed()) {
            return null;
        }
        String username = rs.getString("username");
        String passwordDB = rs.getString("password");
        disconnect ();
        return ((passwordDB != null) && (passwordDB.equals(password))) ? username : null;
    }

    private static void connection() {
        try {
            Class.forName ("org.sqlite.JDBC");
            connection = DriverManager.getConnection ("jdbc:sqlite:src/chat/resources/db/authDB");
            stmt = connection.createStatement ();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void disconnect() throws SQLException {
        connection.close();
    }

    public boolean checkLogin(String login) throws SQLException {
        connection();
        rs = stmt.executeQuery(String.format("SELECT login FROM auth WHERE login = '%s'", login));
        if (rs.isClosed()) {
            return false;
        }
        String loginDB = rs.getString("login");
        disconnect ();
        return loginDB != null;
    }

    public void addNewClient (String login, String password, String username) throws SQLException {
        connection();
        connection.setAutoCommit(false);
        stmt.addBatch (String.format("INSERT INTO auth (login, password, username) VALUES ('%s', '%s', " + "'%s')", login, password, username));
        stmt.executeBatch();
        connection.setAutoCommit(true);
        disconnect ();
    };
    @Override
    public void startAuthentication() {
        System.out.println("Старт аутентификации");
    }

    @Override
    public void endAuthentication() {
        System.out.println("Конец аутентификации");
    }

    public boolean checkUsername(String username) throws SQLException, ClassNotFoundException {
        connection();
        rs = stmt.executeQuery(String.format("SELECT username FROM auth WHERE username = '%s'", username));
        if (rs.isClosed()) {
            return false;
        }
        String usernameDB = rs.getString("username");
        disconnect ();
        return usernameDB != null;
    }

    public boolean checkPassword(String password) throws SQLException {
        connection();
        rs = stmt.executeQuery(String.format("SELECT password FROM auth WHERE login = '%s'", password));
        if (rs.isClosed()) {
            return false;
        }
        String passwordDB = rs.getString("password");
        disconnect ();
        return passwordDB != null;
    }

    public void changeUsername(String login, String newUsername) throws SQLException {
        connection();
        connection.setAutoCommit(false);
        stmt.addBatch (String.format("UPDATE auth SET username='%s' where login ='%s'", newUsername, login));
        stmt.executeBatch();
        connection.setAutoCommit(true);
        disconnect ();
    }
}
