package com.adera;

import com.adera.component.*;
import com.adera.commonTypes.Config;
import com.adera.commonTypes.Establishment;
import com.adera.commonTypes.Machine;
import com.adera.database.ComponentDatabase;
import com.adera.database.MachineDatabase;
import com.adera.entities.MetricEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.adera.mappers.ComponentMapper;
import com.adera.mappers.MachineMapper;
import com.adera.repositories.ComponentRepository;
import com.adera.repositories.MachineRepository;
import com.adera.repositories.MetricRepository;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import com.github.britooo.looca.api.group.rede.RedeInterfaceGroup;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.github.britooo.looca.api.util.Conversor;
import lombok.Getter;
import lombok.SneakyThrows;
import oshi.hardware.CentralProcessor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Monitor {
    private final Looca _looca;
    @Getter
    private Machine machine;
    private Establishment establishment;

    private final Config _config;

    public Monitor(Config config) {
        _config = config;
        _looca = new Looca();
        this.buildMachine();
        if(checkIfNewMachine()) {
            this.insertNewMachine();
        } else if(hasSameComponents()) {
            this.buildMachineFromDatabase();
        } else {
            updateComponents();
        }
    }

    private void buildMachine() {
        ArrayList<Component> components = new ArrayList<>();

        Sistema sys = this._looca.getSistema();
        this.machine = new Machine();
        this.machine.setId(UUID.randomUUID());
        this.machine.setMachineName(this._looca.getRede().getParametros().getHostName());
        this.machine.setOs(sys.getSistemaOperacional());
        this.machine.setVendor(sys.getFabricante());
        this.machine.setArchitecture(sys.getArquitetura());
        this.machine.setMacAddress(this._looca.getRede().getGrupoDeInterfaces().getInterfaces().get(0).getEnderecoMac());
        this.machine.setEstablishmentId(_config.getEstablishmentId());

        Processador cpu = this._looca.getProcessador();
        String cpuDescription = String.format("%s, Núcleos: %d, Threads: %d", cpu.getMicroarquitetura(), cpu.getNumeroCpusFisicas(), cpu.getNumeroCpusLogicas());

        Component cpuComponent = new CpuComponent(
                null,
                cpu.getNome(),
                cpuDescription,
                cpu.getNumeroCpusLogicas().doubleValue(),
                ComponentTypeEnum.CPU,
                MetricUnitEnum.PORCENTAGEM
        );
        components.add(cpuComponent);

        Memoria mem = new Memoria();
        Component memoryComponent = new MemoryComponent();
        memoryComponent.setType(ComponentTypeEnum.MEMORY);
        memoryComponent.setMetricUnit(MetricUnitEnum.BYTE);
        memoryComponent.setModel(Conversor.formatarBytes(mem.getTotal()));
        memoryComponent.setCapacity(mem.getTotal().doubleValue());
        memoryComponent.setDescription(Conversor.formatarBytes(mem.getTotal()));
        components.add(memoryComponent);

        DiscoGrupo disks = this._looca.getGrupoDeDiscos();
        for (Disco disk : disks.getDiscos()) {
            Component diskComponent = new DiskComponent();
            diskComponent.setType(ComponentTypeEnum.DISK);
            diskComponent.setMetricUnit(MetricUnitEnum.BYTE);
            diskComponent.setModel(disk.getModelo());
            diskComponent.setCapacity(disk.getTamanho().doubleValue());
            diskComponent.setDescription(Conversor.formatarBytes(disk.getTamanho()));
            components.add(diskComponent);
        }

        RedeInterfaceGroup networks = this._looca.getRede().getGrupoDeInterfaces();
        for (RedeInterface network : networks.getInterfaces()){
            Component networkComponent = new LatencyComponent();
            networkComponent.setType(ComponentTypeEnum.NETWORK);
            networkComponent.setMetricUnit(MetricUnitEnum.MILISEGUNDOS);
            networkComponent.setModel(network.getNome());
            networkComponent.setCapacity(0.0);
            networkComponent.setDescription(network.getNomeExibicao());
            components.add(networkComponent);
        }

        this.machine.setComponents(components);
    }

    @SneakyThrows
    private void buildMachineFromDatabase() {
        var machineEntity = MachineDatabase.getMachineByMacAddress(_looca.getRede().getGrupoDeInterfaces().getInterfaces().get(0).getEnderecoMac());

        var componentsInDatabase = ComponentDatabase.getComponentsByMachineId(machineEntity.getId());
        var components = new ArrayList<Component>(componentsInDatabase.stream().map(ComponentMapper::toComponent).toList());

        this.machine = MachineMapper.toMachine(machineEntity, components);
    }

    private Boolean checkIfNewMachine() {
        var entity = MachineDatabase.getMachineByMacAddress(_looca.getRede().getGrupoDeInterfaces().getInterfaces().get(0).getEnderecoMac());
        return entity == null;
    }

    @SneakyThrows
    private Boolean hasSameComponents() {
        machine.setId(MachineDatabase.getMachineByMacAddress(this._looca.getRede().getGrupoDeInterfaces().getInterfaces().get(0).getEnderecoMac()).getId());
        var componentsInDatabase = ComponentDatabase.getComponentsByMachineId(machine.getId());
        var components = new ArrayList<Component>(componentsInDatabase.stream().map(ComponentMapper::toComponent).toList());

        return machine.getComponents().containsAll(components);
    }

    @SneakyThrows
    private void updateComponents() {
        var repository = new ComponentRepository(new HashMap<>());
        var componentsInDatabase = ComponentDatabase.getComponentsByMachineId(machine.getId());
        var components = new ArrayList<Component>(
                componentsInDatabase
                        .stream()
                        .map(ComponentMapper::toComponent)
                        .toList()
        );

        var localComponents = machine.getComponents();


        var componentsThatExistsInBoth = new ArrayList<>(localComponents);
        componentsThatExistsInBoth.retainAll(components);

        var componentsDBOnly = new ArrayList<>(components);
        componentsDBOnly.removeAll(componentsThatExistsInBoth);

        var componentsLocalOnly = new ArrayList<>(localComponents);
        componentsLocalOnly.removeAll(componentsThatExistsInBoth);

        componentsDBOnly.forEach(entity -> {
            entity.setId(UUID.randomUUID());
            var component = ComponentMapper.toComponentEntity(entity, _config.getEstablishmentId());
            component.setIsActive(false);
            repository.registerModified(component);
        });

        componentsLocalOnly.forEach(entity -> {
            entity.setId(UUID.randomUUID());
            var component = ComponentMapper.toComponentEntity(entity, _config.getEstablishmentId());
            component.setIsActive(true);
            repository.registerNew(component);
        });

        repository.commit();
    }

    private void insertNewMachine() {
        var machineRepo = new MachineRepository(new HashMap<>());
        machineRepo.registerNew(MachineMapper.toMachineEntity(this.machine));
        machineRepo.commit();

        var componentsRepo = new ComponentRepository(new HashMap<>());

        machine.getComponents().forEach(component -> {
            component.setId(UUID.randomUUID());
            var entity = ComponentMapper.toComponentEntity(component, machine.getId());
            componentsRepo.registerNew(entity);
        });

        componentsRepo.commit();
    }

    public void insertMetrics() {
        var metricRepository = new MetricRepository();
        machine
                .getComponents()
                .forEach(component -> {
                    var metric = component.getMetric();
                    Logger.logInfo(String.format("Inserting metric %s on component %s", metric.getId(), component.getId()));
                    metricRepository.registerNew(metric);
                });

        metricRepository.commit();
    }
}
