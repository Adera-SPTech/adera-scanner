package com.adera.entities;

import com.adera.enums.CommandEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
public class CommandEntity {

    private UUID id;

    private CommandEnum comand;

    private Boolean executed;

    private UUID machineId;

    @Override
    public String toString() {
        return "CommandEntity{" +
                "id=" + id +
                ", comand=" + comand +
                ", executed=" + executed +
                ", machineId=" + machineId +
                '}';
    }
}
