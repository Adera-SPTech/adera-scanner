package com.adera.repositories;

import com.adera.database.ComponentDatabase;
import com.adera.database.ConnectionMySQL;
import com.adera.entities.ComponentEntity;
import com.adera.entities.EstablishmentEntity;
import com.adera.extensions.MySQLExtension;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ComponentRepository implements IUnitOfWork<ComponentEntity>{

    private final Map<String, ArrayList<ComponentEntity>> context;

    @Override
    public void registerNew(ComponentEntity entity) {
        register(entity, "INSERT");
    }

    @Override
    public void registerModified(ComponentEntity entity) {
        register(entity, "MODIFY");
    }

    @Override
    public void registerDeleted(ComponentEntity entity) {
        register(entity, "DELETE");
    }

    private void register(ComponentEntity entity, String operation) {
        ArrayList<ComponentEntity> componentsToOperate = this.context.get(operation);
        if(componentsToOperate == null) {
            componentsToOperate = new ArrayList<ComponentEntity>();
        }
        componentsToOperate.add(entity);
        this.context.put(operation, componentsToOperate);
    }

    @Override
    public void commit() {
        if(this.context.get("INSERT") != null) {
            this.commitInserted();
        }

        if(this.context.get("MODIFIED") != null) {
            this.commitModified();
        }
    }

    private void commitInserted() {
        ComponentDatabase database = new ComponentDatabase();

        ArrayList<ComponentEntity> componentsToBeInserted = this.context.get("INSERT");
        for(ComponentEntity component : componentsToBeInserted) {
            try {
                database.insertOne(component);
            } catch (SQLException e) {
                MySQLExtension.handleException(e);
            }
        }
    }

    private void commitModified() {
        ComponentDatabase database = new ComponentDatabase();

        ArrayList<ComponentEntity> componentsToBeModified = this.context.get("MODIFIED");
        for(ComponentEntity component : componentsToBeModified) {
            database.updateOne(component);
        }
    }
}
