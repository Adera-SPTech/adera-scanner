package com.adera.extensions;

import com.adera.Logger;

import java.sql.SQLException;

public class SQLExtension {
    public static void handleException(SQLException e) {
        Logger.logError(String.format("Erro SQL - %d - %s", e.getErrorCode(), e.getMessage()), e);
    }
}
