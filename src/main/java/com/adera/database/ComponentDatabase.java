package com.adera.database;

import com.adera.entities.ComponentEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.adera.extensions.SQLExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ComponentDatabase {
    private static final Connection connMySql = ConnectionMySQL.getConnection();
    private static final Connection connSqlServer = ConnectionSQLServer.getConnection();

    public static ArrayList<ComponentEntity> getComponentsByMachineId(UUID idMachine) throws SQLException{
        String query = "SELECT maquinacomponente.id,\n" +
                "maquinacomponente.modelo,\n" +
                "maquinacomponente.descricao,\n" +
                "maquinacomponente.capacidade,\n" +
                "maquinacomponente.fkmaquina,\n" +
                "maquinacomponente.ativo,\n" +
                "tipocomponente.nome as tipocomponente,\n" +
                "unidademedida.nome as unidadedemedida\n" +
                " FROM maquinacomponente join tipocomponente on maquinacomponente.fktipocomponente = tipocomponente.id join unidademedida on tipocomponente.fkunidademedida = unidademedida.id where maquinacomponente.fkMaquina = ?";
        PreparedStatement statement = connMySql.prepareStatement(query);

        try {
            statement.setString(1, idMachine.toString());
            statement.execute();

            ResultSet result = statement.getResultSet();

            ArrayList<ComponentEntity> list = new ArrayList<>();

            while(result.next()) {
                ComponentEntity component = new ComponentEntity(
                        UUID.fromString(result.getString(1)),
                        result.getString(2),
                        result.getString(3),
                        result.getDouble(4),
                        UUID.fromString(result.getString(5)),
                        result.getBoolean(6),
                        ComponentTypeEnum.valueOf(result.getString(7)),
                        MetricUnitEnum.fromName(result.getString(8))
                );
                list.add(component);
            }

            return list;
        } catch (SQLException e) {
            SQLExtension.handleException(e);
            return new ArrayList<>();
        }

    }
    public void insertOne(ComponentEntity component) throws SQLException {

        HashMap<Connection, String> queries = new HashMap<>();
        queries.put(connMySql, "INSERT INTO maquinacomponente VALUES (?, ?, ?, ?, ?, ?, ?)");
        queries.put(connSqlServer, "INSERT INTO maquinacomponente VALUES (?, ?, ?, ?, ?, ?, ?)");

        queries.forEach((conn, query) -> {
            try {
                PreparedStatement statement = conn.prepareStatement(query);

                statement.setString(1, component.getId().toString());
                statement.setString(2, component.getModel());
                statement.setString(3, component.getDescription());
                statement.setDouble(4, component.getCapacity());
                statement.setBoolean(5, component.getIsActive());
                statement.setString(6, component.getIdMachine().toString());
                statement.setInt(7, component.getType().getId());

                statement.execute();

                ResultSet result = statement.getResultSet();
            } catch(SQLException e) { SQLExtension.handleException(e); }
        });

    }

    public void updateOne(ComponentEntity component) {
        String query = "UPDATE maquinacomponente SET modelo = ?, descricao = ?, capacidade = ?, ativo = ? WHERE id = ?";
        try {
            PreparedStatement statement = this.connMySql.prepareStatement(query);
            statement.setString(1, component.getModel());
            statement.setString(2, component.getDescription());
            statement.setDouble(3, component.getCapacity());
            statement.setBoolean(4, component.getIsActive());
            statement.setString(5, component.getId().toString());

            statement.execute();
        } catch (SQLException e) {
            SQLExtension.handleException(e);
        }
    }
}
