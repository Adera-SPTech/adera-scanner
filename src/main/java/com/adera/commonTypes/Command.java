package com.adera.commonTypes;

import com.adera.enums.CommandEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class Command {
    private UUID id;

    private CommandEnum comand;

    private Boolean executed;

    private UUID machineId;
}
