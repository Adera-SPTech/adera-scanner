package com.adera.component;

import com.adera.entities.MetricEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Volume;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


public class DiskComponent extends Component{

    public DiskComponent(UUID id, String model, String description, Double capacity, ComponentTypeEnum type, MetricUnitEnum metricUnit) {
        super(id, model, description, capacity, type, metricUnit);
    }

    public DiskComponent() {
    }

    @Override
    public MetricEntity getMetric() {
        var looca = new Looca();
        var disks = looca.getGrupoDeDiscos().getVolumes();

        long total = disks.stream().mapToLong(Volume::getTotal).sum();
        long available = disks.stream().mapToLong(Volume::getDisponivel).sum();

        long inUse = total - available;
        Double percentageUsing = ((double) inUse /total)*100;

        return new MetricEntity(
                UUID.randomUUID(),
                LocalDateTime.now(),
                String.format("%.0f%%", percentageUsing),
                getId()
        );
    }
}
