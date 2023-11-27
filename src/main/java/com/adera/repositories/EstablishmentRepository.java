package com.adera.repositories;

import com.adera.database.EstablishmentDatabase;
import com.adera.entities.EstablishmentEntity;
import com.adera.extensions.SQLExtension;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class EstablishmentRepository implements IUnitOfWork<EstablishmentEntity> {

    private final Map<String, ArrayList<EstablishmentEntity>> context;

    @Override
    public void registerNew(EstablishmentEntity entity) {
        register(entity, "INSERT");
    }

    @Override
    public void registerModified(EstablishmentEntity entity) {
        register(entity, "MODIFY");
    }

    @Override
    public void registerDeleted(EstablishmentEntity entity) {
        register(entity, "DELETE");
    }

    private void register(EstablishmentEntity ec, String operation) {
        ArrayList<EstablishmentEntity> establishmentsToOperate = context.get(operation);
        if(establishmentsToOperate == null) {
            establishmentsToOperate = new ArrayList<EstablishmentEntity>();
        }
        establishmentsToOperate.add(ec);
        context.put(operation, establishmentsToOperate);
    }

    @SneakyThrows
    @Override
    public void commit() {
        throw new ExecutionControl.NotImplementedException("");
    }

    private void commitInsert() {
        ArrayList<EstablishmentEntity> ecsToBeInserted = context.get("INSERT");
        for(var establishment : ecsToBeInserted) {

            EstablishmentDatabase.insertOne(establishment);

        }
    }

    @SneakyThrows
    private void commitModify() {
        throw new ExecutionControl.NotImplementedException("");
    }

}
