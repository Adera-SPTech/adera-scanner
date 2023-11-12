package com.adera.commonTypes;

import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Component {
    private UUID id;

    private String model;

    private String description;

    private Double capacity;

    private ComponentTypeEnum type;

    private MetricUnitEnum metricUnit;

    @Override
    public String toString() {
        return String.format("""
                \n\t\tComponent {
                    \t\tid: %s,
                    \t\tmodel: %s,
                    \t\tdescription: %s,
                    \t\ttype: %s,
                    \t\tmetricUnit: %s
                \t\t}""", id == null ? "null" : id.toString(), model, description, type, metricUnit);
    }
}
