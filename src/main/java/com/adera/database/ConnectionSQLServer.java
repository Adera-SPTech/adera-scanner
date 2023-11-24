package com.adera.database;

import com.adera.extensions.SQLExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSQLServer {
    private static String url = "jdbc:sqlserver://ec2-100-25-245-5.compute-1.amazonaws.com:1433;databaseName=adera;encrypt=false;trustServerCertificate=true;";
    private static String user = "sa";
    private static String password = "aderatech";

    private static Connection conn;

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            if (conn == null) {
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Conectou SQL Server");
            }
            return conn;
        } catch (SQLException e) {
            System.out.println("Não Conectou");
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
            return null;
        }
    }
}
