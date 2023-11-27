package com.adera.extensions;

import com.adera.Logger;
import com.adera.database.ConnectionMySQL;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLExtension {
    public static void handleException(SQLException e, Connection conn) {
        Logger.logError(String.format("Erro %s - %d - %s", conn == ConnectionMySQL.getConnection() ? "MySQL" : "SQL Server", e.getErrorCode(), e.getMessage()), e);
    }
}
