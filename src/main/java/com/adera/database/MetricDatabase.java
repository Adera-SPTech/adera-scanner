package com.adera.database;

import com.adera.entities.ComponentEntity;
import com.adera.entities.MetricEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.extensions.MySQLExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class MetricDatabase {
    private final Connection conn ;

    public MetricDatabase(Connection conn) {
        this.conn = conn;
    }

    public void insertOne(MetricEntity metric) throws SQLException {
        String query = "INSERT INTO metrica VALUES (?, ?, ?, ?)";
        PreparedStatement statement = this.conn.prepareStatement(query);

        try {
            statement.setString(1, metric.getId().toString());
            statement.setString(2, metric.getMeasurement());
            statement.setString(3, metric.getDate().toString());
            statement.setString(4, metric.getFkComponent().toString());
            statement.execute();

            ResultSet result = statement.getResultSet();
        } catch(SQLException e) { MySQLExtension.handleException(e); }
    }
}
