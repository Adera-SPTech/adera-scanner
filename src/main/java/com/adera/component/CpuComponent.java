package com.adera.component;

import com.adera.commonTypes.Machine;
import com.adera.database.AlertDatabase;
import com.adera.database.OptionDatabase;
import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
import com.adera.entities.OptionsEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.adera.repositories.AlertRepository;
import com.github.britooo.looca.api.core.Looca;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

public class CpuComponent extends Component {

    public CpuComponent(UUID id, String model, String description, Double capacity, ComponentTypeEnum type, MetricUnitEnum metricUnit) {
        super(id, model, description, capacity, type, metricUnit);
    }

    @Override
    public AlertEntity getAlert(List<MetricEntity> recentMetrics, UUID establishmentId, Machine machine) {
        var repo = new AlertRepository(new HashMap<>());
        var options = OptionDatabase.getOptionsByEstablishmentId(establishmentId);
        String  level = null;
        String  description = null;
        if (checkIfRecentMetricsAreAboveTheAttention(recentMetrics, options)) {
            level = "Atenção";
            description = String.format("A CPU da Maquina %s ultrapassou o limite de Atenção", machine.getMachineName());
            if (checkIfRecentMetricsAreAboveTheLimit(recentMetrics, options)){
                level = "Crítico";
                description = String.format("A CPU da Maquina %s ultrapassou o limite Critico", machine.getMachineName());
            }
            var alert = new AlertEntity(
                    UUID.randomUUID(),
                    LocalDateTime.now(),
                    level,
                    description,
                    recentMetrics.get(0).id,
                    false
            );
            repo.registerNew(alert);
            repo.commit();

            return alert;
        }
        return null;
    }

    @Override
    protected boolean checkIfRecentMetricsAreAboveTheLimit(List<MetricEntity> recentMetrics, OptionsEntity options) {
        return recentMetrics.stream().allMatch(metric -> metric.getMeasurement() >= options.getCpuLimit() && !metric.getAlerted());
    }

    @Override
    protected boolean checkIfRecentMetricsAreAboveTheAttention(List<MetricEntity> recentMetrics, OptionsEntity options) {
        return recentMetrics.stream().allMatch(metric -> metric.getMeasurement() >= options.getCpuAttention() && !metric.getAlerted());
    }

    @Override
    public MetricEntity getMetric() {
        var l = new Looca();
        var uso = l.getProcessador().getUso();
        return new MetricEntity(
                UUID.randomUUID(),
                uso.intValue(),
                LocalDateTime.now(),
                false,
                getId()
        );
    }
}
