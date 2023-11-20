package com.adera.database;

import com.adera.entities.ComponentEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.adera.extensions.MySQLExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class ComponentDatabase {
    private static final Connection conn = ConnectionMySQL.getConnection();

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
        PreparedStatement statement = conn.prepareStatement(query);

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
            MySQLExtension.handleException(e);
            return new ArrayList<>();
        }

    }
    public void insertOne(ComponentEntity component) throws SQLException {
        String query = "INSERT INTO maquinacomponente VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.conn.prepareStatement(query);

        try {
            statement.setString(1, component.getId().toString());
            statement.setString(2, component.getModel());
            statement.setString(3, component.getDescription());
            statement.setDouble(4, component.getCapacity());
            statement.setBoolean(5, component.getIsActive());
            statement.setString(6, component.getIdMachine().toString());
            statement.setInt(7, component.getType().getId());

            statement.execute();

            ResultSet result = statement.getResultSet();
        } catch(SQLException e) { MySQLExtension.handleException(e); }
    }

    public void updateOne(ComponentEntity component) {
        String query = "UPDATE maquinacomponente SET modelo = ?, descricao = ?, capacidade = ?, ativo = ? WHERE id = ?";
        try {
            PreparedStatement statement = this.conn.prepareStatement(query);
            statement.setString(1, component.getModel());
            statement.setString(2, component.getDescription());
            statement.setDouble(3, component.getCapacity());
            statement.setBoolean(4, component.getIsActive());
            statement.setString(5, component.getId().toString());

            statement.execute();
        } catch (SQLException e) {
            MySQLExtension.handleException(e);
        }
    }
}
