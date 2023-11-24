package com.adera;

import com.adera.commonTypes.Machine;
import com.adera.component.*;
import com.adera.database.AlertDatabase;
import com.adera.database.MetricDatabase;
import com.adera.database.OptionDatabase;
import com.adera.entities.AlertEntity;
import com.adera.entities.MetricEntity;
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

    @SneakyThrows
    public static void handleNotification(Component component, UUID establismentId, Machine machine) {
        var options = OptionDatabase.getOptionsByEstablishmentId(establismentId);
        List<MetricEntity> recentMetrics = MetricDatabase.getRecentMetricsByComponentId(component.getId());

       AlertEntity alert = component.getAlert(recentMetrics, options);

        if (alert.getLevel() != null) {
            String alertMessage = String
                    .format("Alerta Nivel: %s,\n" +
                            "\tMaquina: %s,\n" +
                            "\tComponente: %s,\n" +
                            "\tNome Componente: %s,\n" +
                            "\tValor Medido: %d%%", alert.getLevel(), machine.getMachineName(), component.getType(), component.getModel(), recentMetrics.get(0).getMeasurement());

            formatNotification(alertMessage);
            recentMetrics.get(0).setAlerted(true);
            MetricDatabase.updateOne(recentMetrics.get(0));
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
