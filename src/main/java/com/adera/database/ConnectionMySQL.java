package com.adera.database;

import com.adera.extensions.SQLExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMySQL {

    private static final String url = "jdbc:mysql://localhost:3306/adera";
    private static final String user = "adera";
    private static final String password = "aderatech";

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
