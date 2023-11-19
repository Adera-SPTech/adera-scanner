package com.adera.mappers;

import com.adera.component.*;
import com.adera.entities.ComponentEntity;

import java.util.UUID;

public abstract class ComponentMapper {
    public static ComponentEntity toComponentEntity(Component self, UUID establishmentId) {
        return new ComponentEntity(
                self.getId(),
                self.getModel(),
                self.getDescription(),
                self.getCapacity(),
                establishmentId,
                true,
                self.getType(),
                self.getMetricUnit()
        );
    }

    public static Component toComponent(ComponentEntity self) {
        switch (self.getType()) {
            case CPU -> {
                return new CpuComponent(
                        self.getId(),
                        self.getModel(),
                        self.getDescription(),
                        self.getCapacity(),
                        self.getType(),
                        self.getMetricUnit()
                );
            }
            case DISK -> {
                return new DiskComponent(
                        self.getId(),
                        self.getModel(),
                        self.getDescription(),
                        self.getCapacity(),
                        self.getType(),
                        self.getMetricUnit()
                );
            }
            case MEMORY -> {
                return new MemoryComponent(
                        self.getId(),
                        self.getModel(),
                        self.getDescription(),
                        self.getCapacity(),
                        self.getType(),
                        self.getMetricUnit()
                );
            }
            case NETWORK -> {
                return new LatencyComponent(
                        self.getId(),
                        self.getModel(),
                        self.getDescription(),
                        self.getCapacity(),
                        self.getType(),
                        self.getMetricUnit()
                );
            }
            default -> {
                return null;
            }
        }
    }
}
