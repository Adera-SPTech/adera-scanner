package com.adera.database;

import com.adera.entities.UserEntity;
import com.adera.extensions.SQLExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDatabase {
    private static final Connection conn = ConnectionMySQL.getConnection();

    public void insertOne(UserEntity user) throws SQLException {
        assert conn != null;
        String query = "INSERT INTO usuario VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        try {
            statement.setString(1, user.getId().toString());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getName());
            statement.setString(5, user.getLastName());
            statement.setString(6, user.getRole());
            statement.setString(7, user.getEstablishmentId().toString());
            statement.execute();

        } catch (SQLException e) {
            SQLExtension.handleException(e);
            return;
        }
    }

    public static UserEntity getOneById(String id) throws SQLException {
        assert conn != null;
        String query = "SELECT * FROM usuario WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        try {
            statement.setString(1, id);

            ResultSet result = statement.executeQuery();
            if(result.next()) {
                UserEntity user = new UserEntity();
                user.setId(UUID.fromString(result.getString(1)));
                user.setEmail(result.getString(2));
                user.setPassword(result.getString(3));
                user.setName(result.getString(4));
                user.setLastName(result.getString(5));
                user.setRole(result.getString(6));
                user.setEstablishmentId(UUID.fromString(result.getString(7)));
                return user;
            }
            return null;
        } catch (SQLException e) {
            SQLExtension.handleException(e);
            return null;
        }
    }

    public static UserEntity getOneByEmailAndPassword(String email, String password) throws SQLException {
        assert conn != null;
        String query = "SELECT * FROM usuario WHERE email = ? AND senha = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        try {
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();
            if(result.next()) {
                UserEntity user = new UserEntity();
                user.setId(UUID.fromString(result.getString(1)));
                user.setEmail(result.getString(2));
                user.setPassword(result.getString(3));
                user.setName(result.getString(4));
                user.setLastName(result.getString(5));
                user.setRole(result.getString(6));
                user.setEstablishmentId(UUID.fromString(result.getString(7)));
                return user;
            }
            return null;
        } catch (SQLException e) {
            SQLExtension.handleException(e);
            return null;
        }
    }
}
