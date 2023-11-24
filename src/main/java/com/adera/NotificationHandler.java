package com.adera;

import com.adera.commonTypes.Machine;
import com.adera.component.*;
import com.adera.database.OptionDatabase;
import com.adera.entities.MetricEntity;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class NotificationHandler {
    private static HttpClient client = HttpClient.newHttpClient();
    private static final String URL = "https://hooks.slack.com/services/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";

    private static String level;

    @SneakyThrows
    public static void handleNotification(Component component, MetricEntity metric, UUID establismentId, Machine machine) {
        var options = OptionDatabase.getOptionsByEstablishmentId(establismentId);

        switch (component.getType()) {
            case CPU -> {
                if (Integer.parseInt(metric.getMeasurement()) >= options.getCpuAttention()) {
                    level = "Atenção";
                    if (Integer.parseInt(metric.getMeasurement()) >= options.getCpuLimit()) {
                        level = "Crítico";
                    }
                }
            }
            case DISK -> {
                if (Integer.parseInt(metric.getMeasurement()) >= options.getDiskAttention()) {
                    level = "Atenção";
                    if (Integer.parseInt(metric.getMeasurement()) >= options.getDiskLimit()) {
                        level = "Crítico";
                    }
                }
            }
            case MEMORY -> {
                if (Integer.parseInt(metric.getMeasurement()) >= options.getRamAttention()) {
                    level = "Atenção";
                    if (Integer.parseInt(metric.getMeasurement()) >= options.getRamLimit()) {
                        level = "Crítico";
                    }
                }
            }
            case NETWORK -> {
                if (Integer.parseInt(metric.getMeasurement()) >= options.getLatencyAttention()) {
                    level = "Atenção";
                    if (Integer.parseInt(metric.getMeasurement()) >= options.getLatencyLimit()) {
                        level = "Crítico";
                    }
                }
            }
        }
        String alert = String
                .format("Alerta Nivel: %s,\n" +
                        "\tMaquina: %s,\n" +
                        "\tComponente: %s,\n" +
                        "\tNome Componente: %s,\n" +
                        "\tValor Medido: %d%%", level, machine.getMachineName(), component.getType(), component.getModel(), metric.getMeasurement());

        formatNotification(alert);
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
