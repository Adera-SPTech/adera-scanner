package com.adera.database;

import com.adera.commonTypes.Machine;
import com.adera.entities.MachineEntity;
import com.adera.extensions.MySQLExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MachineDatabase {
    private static final Connection conn = ConnectionMySQL.getConnection();

    public MachineEntity getMachineByMacAddress(String macAddress) throws SQLException {
        assert conn != null;
        String query = "select * from maquina where enderecoMac = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        try {
            statement.setString(1, macAddress);
            statement.execute();
            ResultSet result = statement.getResultSet();

            if(result.next()) {
                return new MachineEntity(
                        UUID.fromString(result.getString(1)),
                        result.getString(2),
                        result.getString(3),
                        result.getInt(4),
                        result.getString(5),
                        UUID.fromString(result.getString(6))
                );
            }
            return null;
        } catch (SQLException e) {
            MySQLExtension.handleException(e);
            return null;
        }
    }

    public static void insertOne(MachineEntity machine) throws SQLException {
        assert conn != null;
        String query = "INSERT INTO maquina VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        try {
            statement.setString(1, machine.getId().toString());
            statement.setString(2, machine.getOs());
            statement.setString(3, machine.getVendor());
            statement.setString(4, machine.getArchitecture().toString());
            statement.setString(5, machine.getMacAddress());
            statement.setString(6, machine.getEstablishmentId().toString());
            statement.execute();

        } catch (SQLException e) {
            MySQLExtension.handleException(e);
            return;
        }
    }
}
