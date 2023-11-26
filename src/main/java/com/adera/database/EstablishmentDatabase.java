package com.adera.database;

import com.adera.entities.EstablishmentEntity;
import com.adera.extensions.SQLExtension;

import java.sql.*;
import java.util.UUID;

public class EstablishmentDatabase {

    private static final Connection conn = ConnectionMySQL.getConnection();

    public static EstablishmentEntity getOneById(String id) throws SQLException {
        assert conn != null;
        String query = "select * from estabelecimento where id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        try {
            statement.setString(1, id);
            statement.execute();
            ResultSet result = statement.getResultSet();

            EstablishmentEntity ec = new EstablishmentEntity();
            while(result.next()) {
                ec.setId(UUID.fromString(result.getString(1)));
                ec.setFantasyName(result.getString(2));
                ec.setCnpj(result.getString(3));
            }
            return ec;
        } catch (SQLException e) {
            SQLExtension.handleException(e);
            return null;
        }
    }

    public static ResultSet insertOne(EstablishmentEntity market) throws SQLException {
        assert conn != null;
        String query = "INSERT INTO estabelecimento VALUES (?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        try {
            statement.setString(1, market.getId().toString());
            statement.setString(2, market.getFantasyName());
            statement.setString(3, market.getCnpj());
            statement.execute();
            return statement.getResultSet();
        } catch (SQLException e) {
            SQLExtension.handleException(e);
            return null;
        }
    }
}
