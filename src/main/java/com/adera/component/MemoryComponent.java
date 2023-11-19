package com.adera.component;

import com.adera.entities.MetricEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.util.Conversor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class MemoryComponent extends Component{

    public MemoryComponent(UUID id, String model, String description, Double capacity, ComponentTypeEnum type, MetricUnitEnum metricUnit) {
        super(id, model, description, capacity, type, metricUnit);
    }

    public MemoryComponent() {
    }

    @Override
    public MetricEntity getMetric() {
        return new MetricEntity(
                UUID.randomUUID(),
                LocalDateTime.now(),
                Conversor.formatarBytes(new Looca().getMemoria().getEmUso()),
                getId()
        );
    }
}
