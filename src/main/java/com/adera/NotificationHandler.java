package com.adera;

import com.adera.commonTypes.Machine;
import com.adera.component.*;
import com.adera.database.AlertDatabase;
import com.adera.database.MetricDatabase;
import com.adera.database.OptionDatabase;
import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
import com.adera.repositories.MetricRepository;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class NotificationHandler {
    private static HttpClient client = HttpClient.newHttpClient();
    private static final String URL = "https://hooks.slack.com/services/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";

    public static void handleNotification(Component component, UUID establismentId, Machine machine) {
        var repo = new MetricRepository();
        List<MetricEntity> recentMetrics = MetricDatabase.getRecentMetricsByComponentId(component.getId());

        var alert = component.getAlert(recentMetrics, establismentId, machine);

        if(alert != null) {
            if (alert.getLevel() != null) {
                String alertMessage = String
                        .format("Alerta Nivel: %s,\n" +
                                "\tMaquina: %s,\n" +
                                "\tComponente: %s,\n" +
                                "\tNome Componente: %s,\n" +
                                "\tValor Medido: %d%%", alert.getLevel(), machine.getMachineName(), component.getType(), component.getModel(), recentMetrics.get(0).getMeasurement());

                Logger.logInfo(String.format("Enviando alerta para o Slack - %s", alert.getDescription()));
                formatNotification(alertMessage);
                recentMetrics.stream().forEach(metric -> {
                    metric.setAlerted(true);
                    repo.registerModified(metric);
                });
                repo.commit();
            }
        }
    }

    public static void formatNotification(String alert) {
        JSONObject json = new JSONObject();
        json.put("text", alert);
        notify(json);
    }

    @SneakyThrows
    public static void notify(JSONObject content) {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(URL))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(String.format("Status: %s", response.statusCode()));
        System.out.println(String.format("Response: %s", response.body()));
    }
}
