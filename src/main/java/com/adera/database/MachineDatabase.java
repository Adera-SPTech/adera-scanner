package com.adera.database;

import com.adera.entities.MachineEntity;
import com.adera.extensions.SQLExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class MachineDatabase {
    private static final Connection connMySql = ConnectionMySQL.getConnection();
    private static final Connection connSqlServer = ConnectionSQLServer.getConnection();

    public static MachineEntity getMachineByMacAddress(String macAddress) {
        String query = "select * from maquina where enderecoMac = ?";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            assert connSqlServer != null;
            assert connMySql != null;
            PreparedStatement statement = connSqlServer.prepareStatement(query);
            statement.setString(1, macAddress);
            statement.execute();
            ResultSet result = statement.getResultSet();

            if(result.next()) {
                return new MachineEntity(
                        UUID.fromString(result.getString(1)),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getInt(5),
                        result.getString(6),
                        UUID.fromString(result.getString(7))
                );
            }
            return null;
        } catch (SQLException e) {
            SQLExtension.handleException(e);
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
            return null;
        }
    }

    public static void insertOne(MachineEntity machine) throws SQLException {

        HashMap<Connection, String> queries = new HashMap<>();
        queries.put(connMySql, "INSERT INTO maquina VALUES (?, ?, ?, ?, ?, ?, ?)");
        queries.put(connSqlServer, "INSERT INTO maquina VALUES (?, ?, ?, ?, ?, ?, ?)");

        queries.forEach((conn, query) -> {
            assert conn != null;
            try {
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, machine.getId().toString());
                statement.setString(2, machine.getMachineName());
                statement.setString(3, machine.getOs());
                statement.setString(4, machine.getVendor());
                statement.setString(5, machine.getArchitecture().toString());
                statement.setString(6, machine.getMacAddress());
                statement.setString(7, machine.getEstablishmentId().toString());
                statement.execute();

            } catch (SQLException e) {
                SQLExtension.handleException(e);
                return;
            }
        });

    }
}
