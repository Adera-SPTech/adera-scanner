package com.adera.entities;

import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ComponentEntity {
    private UUID id;
    private String model;
    private String description;
    private Double capacity;
    private UUID idMachine;
    private Boolean isActive;
    private ComponentTypeEnum type;
    private MetricUnitEnum metricUnit;
}
