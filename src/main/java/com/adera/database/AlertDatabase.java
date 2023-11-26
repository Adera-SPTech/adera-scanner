package com.adera.database;

import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
import com.adera.extensions.SQLExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlertDatabase {
    private static final Connection connMySQL = ConnectionMySQL.getConnection();
    private static final Connection connSQLServer = ConnectionSQLServer.getConnection();

    public static void insertOne(AlertEntity alert) {
        String query = "INSERT INTO alerta VALUES (?, ?, ?, ?, ?, ?)";

        try {
        PreparedStatement statement = connMySQL.prepareStatement(query);
            statement.setString(1, alert.getId().toString());
            statement.setString(3, alert.getDate().toString());
            statement.setString(2, alert.getLevel());
            statement.setString(4, alert.getDescription().toString());
            statement.setString(5, alert.getMetricId().toString());
            statement.setString(6, alert.getRead().toString());
            statement.execute();

            ResultSet result = statement.getResultSet();
        } catch(SQLException e) { SQLExtension.handleException(e); }
    }
}
