package com.adera.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.UUID;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.Setter;
    import java.util.UUID;

    @Getter
    @Setter
    @AllArgsConstructor
public class OptionsEntity {

        private Integer id;

        private Boolean autoRestart;

        private Boolean periodicalRestart;

        private Time restartTime;

        private Integer cpuLimit;

        private Integer ramLimit;

        private Integer diskLimit;

        private Integer latencyLimit;

        private UUID establishmentId;
}
