package com.adera.component;

import com.adera.entities.MetricEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import lombok.AllArgsConstructor;

import java.net.InetAddress;
import java.time.LocalDateTime;
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
}
