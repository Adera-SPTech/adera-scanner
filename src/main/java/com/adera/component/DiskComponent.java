package com.adera.component;

import com.adera.commonTypes.Machine;
import com.adera.database.AlertDatabase;
import com.adera.database.OptionDatabase;
import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
import com.adera.entities.OptionsEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Volume;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
                (int) Math.round(percentageUsing),
                LocalDateTime.now(),
                false,
                getId()
        );
    }

    @Override
    public AlertEntity getAlert(List<MetricEntity> recentMetrics, UUID establishmentId, Machine machine) {
        var options = OptionDatabase.getOptionsByEstablishmentId(establishmentId);
        String  level = null;
        String  description = null;
        if (checkIfRecentMetricsAreAboveTheAttention(recentMetrics, options)) {
            level = "Atenção";
            description = String.format("O Disco da Maquina %s ultrapassou o limite de Atenção", machine.getMachineName());
            if (checkIfRecentMetricsAreAboveTheLimit(recentMetrics, options)){
                level = "Crítico";
                description = String.format("O Disco da Maquina %s ultrapassou o limite Critico", machine.getMachineName());
            }
            var alert = new AlertEntity(
                    UUID.randomUUID(),
                    LocalDateTime.now(),
                    level,
                    description,
                    recentMetrics.get(0).id,
                    false
            );
            AlertDatabase.insertOne(alert);

            return alert;
        }
        return null;
    }

    @Override
    protected boolean checkIfRecentMetricsAreAboveTheLimit(List<MetricEntity> recentMetrics, OptionsEntity options) {
        return recentMetrics.stream().allMatch(metric -> metric.getMeasurement() >= options.getDiskLimit() && !metric.getAlerted());
    }

    @Override
    protected boolean checkIfRecentMetricsAreAboveTheAttention(List<MetricEntity> recentMetrics, OptionsEntity options) {
        return recentMetrics.stream().allMatch(metric -> metric.getMeasurement() >= options.getDiskAttention() && !metric.getAlerted());
    }
}
