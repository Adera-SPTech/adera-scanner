package com.adera.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class MetricEntity {

    public UUID id;

    public LocalDateTime date;

    public String measurement;

    public Boolean alerted;

    public UUID fkComponent;
}
