package com.adera.repositories;

import com.adera.database.MachineDatabase;
import com.adera.entities.MachineEntity;
import com.adera.extensions.SQLExtension;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class MachineRepository implements IUnitOfWork<MachineEntity> {
    private final Map<String, ArrayList<MachineEntity>> context;

    @Override
    public void registerNew(MachineEntity entity) {
        register(entity, "INSERT");
    }

    @Override
    public void registerModified(MachineEntity entity) {

    }

    @Override
    public void registerDeleted(MachineEntity entity) {

    }

    private void register(MachineEntity entity, String operation){
        ArrayList<MachineEntity> machinesToOperate = this.context.get(operation);
        if(machinesToOperate == null) {
            machinesToOperate = new ArrayList<MachineEntity>();
        }
        machinesToOperate.add(entity);
        this.context.put(operation, machinesToOperate);
    }

    @Override
    public void commit() {
        if (context == null || context.isEmpty()) {
            return;
        }
        if(context.containsKey("INSERT")) {
            commitInserted();
        }

        context.clear();
    }

    private void commitInserted() {
        ArrayList<MachineEntity> machinesToBeInserted = context.get("INSERT");
        for(MachineEntity machine : machinesToBeInserted) {

            MachineDatabase.insertOne(machine);

        }
    }
}
