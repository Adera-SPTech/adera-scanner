package com.adera.database;

import com.adera.extensions.SQLExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMySQL {

    private static final String url = "jdbc:mysql://172.17.0.2:3306/adera";
    private static final String user = "root";
    private static final String password = "12345678";

    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null) {
                conn = DriverManager.getConnection(url, user, password);
            }
            return conn;
        } catch (SQLException e) {
            SQLExtension.handleException(e);
            return null;
        }
    }
}
