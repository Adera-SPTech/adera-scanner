package com.adera.database;

import com.adera.extensions.MySQLExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMySQL {
    private static final String url = "jdbc:mysql://localhost:3306/adera";
    private static final String user = "root";
    private static final String password = "2805";

    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null) {
                conn = DriverManager.getConnection(url, user, password);
            }
            return conn;
        } catch (SQLException e) {
            MySQLExtension.handleException(e);
            return null;
        }
    }
}
