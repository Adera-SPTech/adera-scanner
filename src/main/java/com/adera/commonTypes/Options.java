package com.adera.commonTypes;

import com.adera.enums.CommandEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class Options {
    private Integer id;

    private Boolean autoRestart;

    private Boolean periodicalRestar;

    private Time restartTime;

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
