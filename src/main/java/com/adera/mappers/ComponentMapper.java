package com.adera.mappers;

import com.adera.commonTypes.Component;
import com.adera.entities.ComponentEntity;
import com.adera.enums.MetricUnitEnum;

import java.util.UUID;

public abstract class ComponentMapper {
    public static ComponentEntity toComponentEntity(Component self, UUID establishmentId) {
        return new ComponentEntity(
                self.getId(),
                self.getModel(),
                self.getDescription(),
                self.getCapacity(),
                establishmentId,
                self.getType(),
                self.getMetricUnit()
        );
    }

    public static Component toComponent(ComponentEntity self) {
        return new Component(
                self.getId(),
                self.getModel(),
                self.getDescription(),
                self.getCapacity(),
                self.getType(),
                self.getMetricUnit()
        );
    }
}
