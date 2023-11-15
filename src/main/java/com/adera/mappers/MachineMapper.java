package com.adera.mappers;

import com.adera.commonTypes.Component;
import com.adera.commonTypes.Machine;
import com.adera.entities.MachineEntity;
import jdk.jshell.spi.ExecutionControl;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public abstract class MachineMapper {
    @SneakyThrows
    public Machine toMachine(MachineEntity mch) {
        throw new ExecutionControl.NotImplementedException("");
    }

    @SneakyThrows
    public static MachineEntity toMachineEntity(Machine self) {
        return new MachineEntity(
                self.getId(),
                self.getOs(),
                self.getVendor(),
                self.getArchitecture(),
                self.getMacAddress(),
                self.getEstablishmentId()
        );
    }

    public static Machine toMachine(MachineEntity self, ArrayList<Component> components) {
        return new Machine(
                self.getId(),
                self.getMacAddress(),
                self.getOs(),
                self.getVendor(),
                self.getArchitecture(),
                components,
                self.getEstablishmentId()
        );
    }
}
