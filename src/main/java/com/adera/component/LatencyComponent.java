package com.adera.component;

import com.adera.database.AlertDatabase;
import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
import com.adera.entities.OptionsEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import lombok.AllArgsConstructor;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class LatencyComponent extends Component{

    public LatencyComponent(UUID id, String model, String description, Double capacity, ComponentTypeEnum type, MetricUnitEnum metricUnit) {
        super(id, model, description, capacity, type, metricUnit);
    }

    public LatencyComponent() {
    }

    @Override
    public MetricEntity getMetric() {
        var metric = new MetricEntity(
                UUID.randomUUID(),
                LocalDateTime.now(),
                "-1",
                false,
                getId()
        );

        try {
            InetAddress address = InetAddress.getByName("www.google.com");

            long startTime = System.currentTimeMillis();
            if (address.isReachable(1000)) {
                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;
                metric.setMeasurement(String.format("%d", latency));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return metric;
    }

    @Override
    public AlertEntity getAlert(List<MetricEntity> recentMetrics, OptionsEntity options) {
        String  level = null;
        String  description = null;
        if (recentMetrics.stream().allMatch(m -> (Integer.parseInt(m.getMeasurement()) >= options.getLatencyAttention() &&
                !m.getAlerted()))) {
            level = "Atenção";
            description = String.format("A Latência da Maquina %s ultrapassou o limite de Atenção");
            if (recentMetrics.stream().allMatch(m -> (Integer.parseInt(m.getMeasurement()) >= options.getLatencyAttention() &&
                    !m.getAlerted()))){
                level = "Crítico";
                description = String.format("A Latência da Maquina %s ultrapassou o limite Critico");
            }
        }

        var alert = new AlertEntity(
                UUID.randomUUID(),
                new Date(),
                level,
                description,
                recentMetrics.get(0).id,
                false
        );
        AlertDatabase.insertOne(alert);

        return alert;
    }
}
