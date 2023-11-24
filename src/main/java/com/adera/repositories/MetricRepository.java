package com.adera.repositories;

import com.adera.database.ComponentDatabase;
import com.adera.database.MetricDatabase;
import com.adera.database.ConnectionMySQL;
import com.adera.entities.ComponentEntity;
import com.adera.entities.MetricEntity;
import com.adera.extensions.MySQLExtension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MetricRepository implements IUnitOfWork<MetricEntity>{

    private final Map<String, ArrayList<MetricEntity>> context = new HashMap<String, ArrayList<MetricEntity>>();

    @Override
    public void registerNew(MetricEntity metric) {
        register(metric, "INSERT");
    }

    @Override
    public void registerModified(MetricEntity metric) {
        register(metric, "MODIFY");
    }

    @Override
    public void registerDeleted(MetricEntity metric) {
        register(metric, "DELETE");
    }

    public void register(MetricEntity metric, String operation) {
        var metricsToOperate = this.context.get(operation);
        if(metricsToOperate == null) {
            metricsToOperate = new ArrayList<MetricEntity>();
        }
        metricsToOperate.add(metric);
        this.context.put(operation, metricsToOperate);
    }

    @Override
    public void commit() {
        if(this.context.get("INSERT") != null) {
            this.commitInserted();
        } else if (this.context.get("MODIFY") != null) {
            this.commitModified();
        }

        this.context.clear();
    }

    private void commitInserted() {
        MetricDatabase database = new MetricDatabase(ConnectionMySQL.getConnection());

        ArrayList<MetricEntity> metricsToBeInserted = this.context.get("INSERT");
        for(MetricEntity metric : metricsToBeInserted) {
            try {
                metric.setId(UUID.randomUUID());
                database.insertOne(metric);
            } catch (SQLException e) {
                MySQLExtension.handleException(e);
            }
        }
    }

    private  void commitModified(){
        MetricDatabase database = new MetricDatabase(ConnectionMySQL.getConnection());
        ArrayList<MetricEntity> metricsToBeModified = this.context.get("MODIFY");

        for(MetricEntity metric : metricsToBeModified) {
            try {
                database.updateOne(metric);
            } catch (SQLException e) {
                MySQLExtension.handleException(e);
            }
        }
    }
}
