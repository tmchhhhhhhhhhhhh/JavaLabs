package by.iba.model;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/lab8db";
    private static final String USER = "root";
    private static final String PASS = "root"; // свой пароль

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
