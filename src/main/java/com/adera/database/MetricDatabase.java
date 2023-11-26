package com.adera.database;

import com.adera.component.Component;
import com.adera.entities.AlertEntity;
import com.adera.Logger;
import com.adera.entities.MetricEntity;
import com.adera.extensions.SQLExtension;
import lombok.SneakyThrows;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.UUID;

public class MetricDatabase {
    private static final Connection connMySql = ConnectionMySQL.getConnection();
    private static final Connection connSqlServer = ConnectionSQLServer.getConnection();

    public static void insertOne(MetricEntity metric) throws SQLException {
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

                Logger.logInfo(String.format("Inserindo metrica %s no banco %s às %s", metric.getId(), conn == connMySql ? "MySQL" : "SQL Server", metric.getDate().format(pattern)));
            } catch(SQLException e) { SQLExtension.handleException(e); }
        });

    }

    public static void updateOne(MetricEntity metric) {
        String query = "UPDATE metrica SET alertou = ? WHERE id = ?";
        try {
            PreparedStatement statement = connSqlServer.prepareStatement(query);
            statement.setString(1, metric.getAlerted().toString());
            statement.setString(2, metric.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            SQLExtension.handleException(e);
        }
    }

        @SneakyThrows
        public static List<MetricEntity> getRecentMetricsByComponentId(UUID id){
            try {
                String query = "SELECT * FROM recent_metrics where MaquinaComponenteId = ?";
                PreparedStatement statement = connMySql.prepareStatement(query);

                statement.setString(1, id.toString());
                statement.execute();

                ResultSet result = statement.getResultSet();

                ArrayList<MetricEntity> recentMetrics = new ArrayList<MetricEntity>();
                while (result.next()) {
                    MetricEntity metric = new MetricEntity(
                            UUID.fromString(result.getString(1)),
                            result.getTimestamp(2).toLocalDateTime(),
                            result.getInt(3),
                            result.getBoolean(4),
                            UUID.fromString(result.getString(5))
                    );
                    recentMetrics.add(metric);
                }
                return recentMetrics;
            } catch (SQLException e) {
                SQLExtension.handleException(e);
                return new ArrayList<>();
            }
        }

}
