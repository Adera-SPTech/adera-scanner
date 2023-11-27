package com.adera.database;

import com.adera.entities.CommandEntity;
import com.adera.enums.CommandEnum;
import com.adera.extensions.SQLExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class CommandDatabase {
    private static final Connection connMySql = ConnectionMySQL.getConnection();
    private static final Connection connSqlServer = ConnectionSQLServer.getConnection();

    public static ArrayList<CommandEntity> getCommandsByMachineId(UUID machineId) {
        var commands = new ArrayList<CommandEntity>();
        String[] queries = {"SELECT * FROM comando WHERE fkMaquina = ? AND rodou = false", "SELECT * FROM comando WHERE fkMaquina = ? AND rodou = 0"};
        try {
            assert connMySql != null;
            var statement = connSqlServer.prepareStatement(queries[1]);
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
            SQLExtension.handleException(e, connSqlServer);
        }
        return commands;
    }

    public static void updateOne(CommandEntity command) {
        HashMap<Connection, String> queries = new HashMap<>();
        queries.put(connMySql, "UPDATE comando SET rodou = ? WHERE id = ?");
        queries.put(connSqlServer, "UPDATE comando SET rodou = ? WHERE id = ?");

        queries.forEach((connection, query) -> {
            try {
                assert connection != null;
                PreparedStatement statement = null;
                statement = connection.prepareStatement(query);
                statement.setBoolean(1, command.getExecuted());
                statement.setString(2, command.getId().toString());

                statement.executeUpdate();
            } catch(SQLException e) {
                SQLExtension.handleException(e, connection);
            }
        });
    }
}
