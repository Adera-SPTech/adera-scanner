package com.adera.database;

import com.adera.entities.CommandEntity;
import com.adera.enums.CommandEnum;
import com.adera.extensions.MySQLExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class CommandDatabase {
    private static final Connection conn = ConnectionMySQL.getConnection();

    public static ArrayList<CommandEntity> getCommandsByMachineId(UUID machineId) {
        var commands = new ArrayList<CommandEntity>();
        var query = "SELECT * FROM comando WHERE fkMaquina = ?";
        try {
            assert conn != null;
            var statement = conn.prepareStatement(query);
            statement.setString(1, machineId.toString());

            var result = statement.executeQuery();

            while(result.next()) {
                var command = new CommandEntity(
                        UUID.fromString(result.getString(1)),
                        CommandEnum.fromName(result.getString(2)),
                        result.getBoolean(3),
                        UUID.fromString(result.getString(4))
                );
                commands.add(command);
            }

        } catch (SQLException e) {
            MySQLExtension.handleException(e);
        }
        return commands;
    }
}
