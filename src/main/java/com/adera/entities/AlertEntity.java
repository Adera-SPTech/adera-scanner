package com.adera.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Getter@Setter
public class AlertEntity {
    private UUID id;
    private LocalDateTime date;
    private String level;
    private String description;
    private UUID metricId;
    private Boolean read;
}
