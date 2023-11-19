package com.adera.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class MachineEntity {
    private UUID id;

    private String machineName;

    private String os;

    private String vendor;

    private Integer architecture;

    private String macAddress;

    private UUID establishmentId;
}
