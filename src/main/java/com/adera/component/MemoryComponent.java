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
import com.github.britooo.looca.api.util.Conversor;
import lombok.AllArgsConstructor;

import javax.crypto.Mac;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MemoryComponent extends Component{

    public MemoryComponent(UUID id, String model, String description, Double capacity, ComponentTypeEnum type, MetricUnitEnum metricUnit) {
        super(id, model, description, capacity, type, metricUnit);
    }

    public MemoryComponent() {
    }

    @Override
    public MetricEntity getMetric() {
        var l = new Looca();
        var total = l.getMemoria().getTotal();
        var uso = l.getMemoria().getEmUso();

        int porcentagemUso = (int) (((double) uso / total) * 100);
        ZonedDateTime zone = ZonedDateTime.now(ZoneId.of("BET"));
        return new MetricEntity(
                UUID.randomUUID(),
                porcentagemUso,
                zone.toLocalDateTime(),
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
            description = String.format("A Ram da Maquina %s ultrapassou o limite de Atenção", machine.getMachineName());
            if (checkIfRecentMetricsAreAboveTheLimit(recentMetrics, options)){
                level = "Crítico";
                description = String.format("A Ram da Maquina %s ultrapassou o limite Critico", machine.getMachineName());
            }
            ZonedDateTime zone = ZonedDateTime.now(ZoneId.of("BET"));
            var alert = new AlertEntity(
                    UUID.randomUUID(),
                    zone.toLocalDateTime(),
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
        return recentMetrics.stream().allMatch(metric -> metric.getMeasurement() >= options.getRamLimit() && !metric.getAlerted());
    }

    @Override
    protected boolean checkIfRecentMetricsAreAboveTheAttention(List<MetricEntity> recentMetrics, OptionsEntity options) {
        return recentMetrics.stream().allMatch(metric -> metric.getMeasurement() >= options.getRamAttention() && !metric.getAlerted());
    }
}
