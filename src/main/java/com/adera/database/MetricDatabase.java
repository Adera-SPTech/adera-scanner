package com.adera.database;

import com.adera.component.Component;
import com.adera.entities.AlertEntity;
import com.adera.entities.ComponentEntity;
import com.adera.entities.MetricEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.extensions.MySQLExtension;
import lombok.SneakyThrows;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MetricDatabase {
    private static final Connection connMySQL = ConnectionMySQL.getConnection();
    private static final Connection connSQLServer = ConnectionSQLServer.getConnection();

    public void insertOne(MetricEntity metric) throws SQLException {
        String query = "INSERT INTO metrica VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.conn.prepareStatement(query);

        try {
            statement.setString(1, metric.getId().toString());
            statement.setString(2, metric.getMeasurement());
            statement.setString(3, metric.getDate().toString());
            statement.setBoolean(4, false);
            statement.setString(5, metric.getFkComponent().toString());
            statement.execute();

            ResultSet result = statement.getResultSet();
        } catch (SQLException e) {
            MySQLExtension.handleException(e);
        }
    }

    public static void updateOne(MetricEntity metric) {
        String query = "UPDATE metrica SET alertou = ? WHERE id = ?";
        try {
            PreparedStatement statement = connSQLServer.prepareStatement(query);
            statement.setString(1, metric.getAlerted().toString());
            statement.setString(2, metric.getId().toString());
            statement.execute();
        } catch (SQLException e) {
            MySQLExtension.handleException(e);
        }
    }

        @SneakyThrows
        public static List<MetricEntity> getRecentMetricsByComponentId(UUID id){
            try {
                String query = "SELECT * recent_metrics where MaquinaComponenteId = ?";
                PreparedStatement statement = connMySQL.prepareStatement(query);

                statement.setString(1, id.toString());
                statement.execute();

                ResultSet result = statement.getResultSet();

                ArrayList<MetricEntity> recentMetrics = new ArrayList<MetricEntity>();
                while (result.next()) {
                    MetricEntity metric = new MetricEntity(
                            UUID.fromString(result.getString(1)),
                            result.getTimestamp(2).toLocalDateTime(),
                            result.getString(3),
                            result.getBoolean(4),
                            UUID.fromString(result.getString(5))
                    );
                    recentMetrics.add(metric);
                }
                return recentMetrics;
            } catch (SQLException e) {
                MySQLExtension.handleException(e);
                return new ArrayList<>();
            }
        }

}
