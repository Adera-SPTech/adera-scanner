package com.adera.component;

import com.adera.database.AlertDatabase;
import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
import com.adera.entities.OptionsEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.util.Conversor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
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
        int porcentagemUso = (int) (uso / total) * 100;

        return new MetricEntity(
                UUID.randomUUID(),
                LocalDateTime.now(),
                porcentagemUso,
                false,
                getId()
        );
    }

    @Override
    public AlertEntity getAlert(List<MetricEntity> recentMetrics, OptionsEntity options) {
        String  level = null;
        String  description = null;
        if (recentMetrics.stream().allMatch(m -> m.getMeasurement() >= options.getRamAttention() &&
                !m.getAlerted())) {
            level = "Atenção";
            description = String.format("A Ram da Maquina %s ultrapassou o limite de Atenção");
            if (recentMetrics.stream().allMatch(m -> m.getMeasurement() >= options.getRamAttention() &&
                    !m.getAlerted())){
                level = "Crítico";
                description = String.format("A Ram da Maquina %s ultrapassou o limite Critico");
            }
        }

        var alert = new AlertEntity(
                UUID.randomUUID(),
                new Date(),
                level,
                description,
                recentMetrics.get(0).id,
                false
        );
        AlertDatabase.insertOne(alert);

        return alert;
    }
}
