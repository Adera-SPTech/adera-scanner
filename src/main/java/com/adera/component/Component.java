package com.adera.component;

import com.adera.commonTypes.Machine;
import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
import com.adera.entities.OptionsEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class Component {
    protected UUID id;

    protected String model;

    protected String description;

    protected Double capacity;

    protected ComponentTypeEnum type;

    protected MetricUnitEnum metricUnit;

    public abstract MetricEntity getMetric();

    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", description='" + description + '\'' +
                ", capacity=" + capacity +
                ", type=" + type +
                ", metricUnit=" + metricUnit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(model, component.model) && Objects.equals(description, component.description) && Objects.equals(capacity, component.capacity) && type == component.type && metricUnit == component.metricUnit;
    }

    public abstract AlertEntity getAlert(List<MetricEntity> recentMetrics, UUID establishmentId, Machine machine);

    protected abstract boolean checkIfRecentMetricsAreAboveTheLimit(List<MetricEntity> recentMetrics, OptionsEntity options);
    protected abstract boolean checkIfRecentMetricsAreAboveTheAttention(List<MetricEntity> recentMetrics, OptionsEntity options);

    @Override
    public int hashCode() {
        return Objects.hash(model, description, capacity, type, metricUnit);
    }
}
