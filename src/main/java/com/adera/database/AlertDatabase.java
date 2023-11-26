package com.adera.database;

import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
import com.adera.extensions.SQLExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class AlertDatabase {
    private static final Connection connMySQL = ConnectionMySQL.getConnection();
    private static final Connection connSQLServer = ConnectionSQLServer.getConnection();

    public static void insertOne(AlertEntity alert) {
        HashMap<Connection, String> queries = new HashMap<>();
        queries.put(connMySQL, "INSERT INTO alerta VALUES (?, ?, ?, ?, ?, ?)");
        queries.put(connSQLServer, "INSERT INTO alerta VALUES (?, ?, ?, ?, ?, ?)");

        var pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");

        queries.forEach((conn, query) -> {
            try {
            PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, alert.getId().toString());
                statement.setString(2, alert.getDate().format(pattern));
                statement.setString(3, alert.getLevel());
                statement.setString(4, alert.getDescription().toString());
                statement.setString(5, alert.getMetricId().toString());
                statement.setBoolean(6, alert.getRead());
                statement.execute();

                ResultSet result = statement.getResultSet();
            } catch(SQLException e) { SQLExtension.handleException(e); }
        });
    }
}
