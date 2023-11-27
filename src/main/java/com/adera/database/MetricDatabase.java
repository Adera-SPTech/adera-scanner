package com.adera.database;

import com.adera.component.Component;
import com.adera.entities.AlertEntity;
import com.adera.Logger;
import com.adera.entities.MetricEntity;
import com.adera.extensions.SQLExtension;
import lombok.SneakyThrows;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;

public class MetricDatabase {
    private static final Connection connMySql = ConnectionMySQL.getConnection();
    private static final Connection connSqlServer = ConnectionSQLServer.getConnection();

    public static void insertOne(MetricEntity metric) {
        HashMap<Connection, String> queries = new HashMap<>();
        queries.put(connMySql, "INSERT INTO metrica VALUES (?, ?, ?, ?, ?)");
        queries.put(connSqlServer, "INSERT INTO metrica VALUES (?, ?, ?, ?, ?)");

        queries.forEach((conn, query) -> {
            try {
                var pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:s");
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, metric.getId().toString());
                statement.setInt(2, metric.getMeasurement());
                if(conn == connMySql) {
                    statement.setString(3, metric.getDate().toString());
                } else {
                    statement.setString(3, metric.getDate().format(pattern));
                }
                statement.setBoolean(4, metric.getAlerted());
                statement.setString(5, metric.getFkComponent().toString());
                statement.execute();

                ResultSet result = statement.getResultSet();
            } catch(SQLException e) { SQLExtension.handleException(e, conn); }
        });

    }

    public static void updateOne(MetricEntity metric) {
        HashMap<Connection, String> queries = new HashMap<>();
        queries.put(connMySql, "UPDATE metrica SET alertado = ? WHERE id = ?");
        queries.put(connSqlServer, "UPDATE metrica SET alertado = ? WHERE id = ?");

        queries.forEach(((conn, query) -> {
            try {
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setBoolean(1, metric.getAlerted());
                statement.setString(2, metric.getId().toString());
                statement.execute();
            } catch (SQLException e) {
                SQLExtension.handleException(e, conn);
            }
        }));

    }

    public static List<MetricEntity> getRecentMetricsByComponentId(UUID id){
        try {
            String query = "SELECT * FROM recent_metrics where MaquinaComponenteId = ? ORDER BY data DESC";
            PreparedStatement statement = connSqlServer.prepareStatement(query);

            statement.setString(1, id.toString());
            statement.execute();

            ResultSet result = statement.getResultSet();

            ArrayList<MetricEntity> recentMetrics = new ArrayList<MetricEntity>();
            while (result.next()) {
                MetricEntity metric = new MetricEntity(
                        UUID.fromString(result.getString(1)),
                        result.getInt(2),
                        result.getTimestamp(3).toLocalDateTime(),
                        result.getBoolean(4),
                        UUID.fromString(result.getString(5))
                );
                recentMetrics.add(metric);
            }
            return recentMetrics;
        } catch (SQLException e) {
            SQLExtension.handleException(e, connSqlServer);
            return new ArrayList<>();
        }
    }

}
