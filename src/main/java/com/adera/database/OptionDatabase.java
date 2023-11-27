package com.adera.database;

import com.adera.commonTypes.Options;
import com.adera.entities.CommandEntity;
import com.adera.entities.OptionsEntity;
import com.adera.enums.CommandEnum;
import com.adera.extensions.SQLExtension;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class OptionDatabase {
        private static final Connection conn = ConnectionSQLServer.getConnection();

        public static OptionsEntity getOptionsByEstablishmentId(UUID establishmentId) {
            var query = "SELECT * FROM opcoes WHERE fkEstabelecimento = ?";

            try {
                assert conn != null;
                var statement = conn.prepareStatement(query);
                statement.setString(1, establishmentId.toString());

                var result = statement.executeQuery();
                var  options = new OptionsEntity();
                while(result.next()) {
                    options = new OptionsEntity(
                            result.getInt(1),
                            result.getBoolean(2),
                            result.getBoolean(3),
                            result.getTime(4).toLocalTime(),
                            result.getInt(5),
                            result.getInt(6),
                            result.getInt(7),
                            result.getInt(8),
                            result.getInt(9),
                            result.getInt(10),
                            result.getInt(11),
                            result.getInt(12),
                            UUID.fromString(result.getString(13))
                    );
                }
                return options;
            } catch (SQLException e) {
                SQLExtension.handleException(e, conn);
                return null;
            }
        }
    }