package com.adera.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Getter@Setter
public class AlertEntity {
    private UUID id;
    private Date date;
    private String level;
    private String description;
    private UUID metricId;
    private Boolean read;
}
