package com.adera;

import com.adera.commonTypes.Machine;
import com.adera.component.*;
import com.adera.database.MetricDatabase;
import com.adera.entities.MetricEntity;
import com.adera.repositories.MetricRepository;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class NotificationHandler {
    private static HttpClient client = HttpClient.newHttpClient();
    private static final String urlEncoded = "aHR0cHM6Ly9ob29rcy5zbGFjay5jb20vc2VydmljZXMvVDA2Nlc3TTNOUUgvQjA2ODBVQ1NHU1UvYWlmY1lYdXlDYTJTT2NIV1VCNWxKRmVl";

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

    public static void notify(JSONObject content) {
        var decoded = Base64.getDecoder().decode(urlEncoded);
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(new String(decoded)))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            Logger.logError("Erro Slack", e);
        } catch (InterruptedException e) {
            Logger.logError("Erro Slack", e);
        }

        Logger.logInfo(String.format("Requisição Slack - Status: %s, Response: %s", response.statusCode(), response.body()));
    }
}
