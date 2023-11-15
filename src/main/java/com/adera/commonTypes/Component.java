package com.adera.commonTypes;

import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(model, component.model) && Objects.equals(description, component.description) && Objects.equals(capacity, component.capacity) && type == component.type && metricUnit == component.metricUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, description, capacity, type, metricUnit);
    }
}
