package com.adera.database;

import com.adera.Logger;
import com.adera.entities.MetricEntity;
import com.adera.extensions.SQLExtension;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class MetricDatabase {
    private static final Connection connMySql = ConnectionMySQL.getConnection();
    private static final Connection connSqlServer = ConnectionSQLServer.getConnection();

    public void insertOne(MetricEntity metric) throws SQLException {
        HashMap<Connection, String> queries = new HashMap<>();
        queries.put(connMySql, "INSERT INTO metrica VALUES (?, ?, ?, ?)");
        queries.put(connSqlServer, "INSERT INTO metrica VALUES (?, ?, ?, ?)");


        queries.forEach((conn, query) -> {

            try {
                var pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, metric.getId().toString());
                statement.setString(2, metric.getMeasurement());
                if(conn == connMySql) {
                    statement.setString(3, metric.getDate().toString());
                } else {
                    statement.setString(3, metric.getDate().format(pattern));
                }
                statement.setString(4, metric.getFkComponent().toString());
                statement.execute();

                ResultSet result = statement.getResultSet();

                Logger.logInfo(String.format("Inserindo metrica %s no banco %s às %s", metric.getId(), conn == connMySql ? "MySQL" : "SQL Server", metric.getDate().format(pattern)));
            } catch(SQLException e) { SQLExtension.handleException(e); }
        });

    }
}
