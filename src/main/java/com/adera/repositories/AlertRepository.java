package com.adera.repositories;

import com.adera.database.AlertDatabase;
import com.adera.database.ComponentDatabase;
import com.adera.entities.AlertEntity;
import com.adera.entities.ComponentEntity;
import com.adera.extensions.MySQLExtension;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class AlertRepository implements IUnitOfWork<AlertEntity> {

    private final Map<String, ArrayList<AlertEntity>> context;

    @Override
    public void registerNew(AlertEntity entity) {
        register(entity, "INSERT");
    }

    @Override
    public void registerModified(AlertEntity entity) {
        register(entity, "MODIFY");
    }

    @Override
    public void registerDeleted(AlertEntity entity) {
        register(entity, "DELETE");
    }

    private void register(AlertEntity entity, String operation) {
        ArrayList<AlertEntity> alertsToOperate = this.context.get(operation);
        if(alertsToOperate == null) {
            alertsToOperate = new ArrayList<AlertEntity>();
        }
        alertsToOperate.add(entity);
        this.context.put(operation, alertsToOperate);
    }

    @Override
    public void commit() {
        if(this.context.get("INSERT") != null) {
            this.commitInserted();
        }
    }

    private void commitInserted() {
        ArrayList<AlertEntity> alertsToBeInserted = this.context.get("INSERT");
        for(AlertEntity alert : alertsToBeInserted) {
            try {
                AlertDatabase.insertOne(alert);
            } catch (SQLException e) {
                MySQLExtension.handleException(e);
            }
        }
    }
}
