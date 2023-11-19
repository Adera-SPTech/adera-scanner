package com.adera.commonTypes;

import com.adera.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Machine {
    private UUID id;

    private String machineName;

    private String macAddress;

    private String os;

    private String vendor;

    private Integer architecture;

    private ArrayList<Component> components;

    private UUID establishmentId;

    public Machine() {
        this.components = new ArrayList<Component>();
    }

    @Override
    public String toString() {
        return "Machine{" +
                "id=" + id +
                ", machineName='" + machineName + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", os='" + os + '\'' +
                ", vendor='" + vendor + '\'' +
                ", architecture=" + architecture +
                ", components=" + components +
                ", establishmentId=" + establishmentId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Machine machine = (Machine) o;
        return Objects.equals(machineName, machine.machineName) && Objects.equals(macAddress, machine.macAddress) && Objects.equals(os, machine.os) && Objects.equals(vendor, machine.vendor) && Objects.equals(architecture, machine.architecture) && Objects.equals(components, machine.components) && Objects.equals(establishmentId, machine.establishmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress, os, vendor, architecture, components, establishmentId);
    }
}
