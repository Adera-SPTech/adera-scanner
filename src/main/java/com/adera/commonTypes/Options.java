package com.adera.commonTypes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class Options {
    private Integer id;

    private Boolean autoRestart;

    private Boolean periodicalRestart;

    private LocalTime restartTime;

    private Integer cpuAttention;

    private Integer ramAttention;

    private Integer diskAttention;

    private Integer latencyAttention;

    private Integer cpuLimit;

    private Integer ramLimit;

    private Integer diskLimit;

    private Integer latencyLimit;

    private UUID establishmentId;
}
