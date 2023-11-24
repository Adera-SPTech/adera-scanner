package com.adera.repositories;

import com.adera.database.CommandDatabase;
import com.adera.database.ComponentDatabase;
import com.adera.entities.CommandEntity;
import com.adera.entities.ComponentEntity;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

@RequiredArgsConstructor
public class CommandRepository implements IUnitOfWork<CommandEntity> {

    private final Map<String, ArrayList<CommandEntity>> context;

    @Override
    public void registerNew(CommandEntity entity) {

    }

    @Override
    public void registerModified(CommandEntity entity) {
        register(entity, "MODIFIED");
    }

    @Override
    public void registerDeleted(CommandEntity entity) {

    }

    @Override
    public void commit() {
        if(this.context.get("MODIFIED") != null) {
            this.commitModified();
        }
    }

    private void register(CommandEntity entity, String operation) {
        ArrayList<CommandEntity> componentsToOperate = this.context.get(operation);
        if(componentsToOperate == null) {
            componentsToOperate = new ArrayList<CommandEntity>();
        }
        componentsToOperate.add(entity);
        this.context.put(operation, componentsToOperate);
    }

    private void commitModified() {
        CommandDatabase database = new CommandDatabase();

        ArrayList<CommandEntity> componentsToBeModified = this.context.get("MODIFIED");
        for(CommandEntity component : componentsToBeModified) {
            database.updateOne(component);
        }
    }
}
