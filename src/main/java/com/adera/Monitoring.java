package com.adera;

import com.adera.commonTypes.Component;
import com.adera.commonTypes.Establishment;
import com.adera.commonTypes.Machine;
import com.adera.database.ComponentDatabase;
import com.adera.database.ConnectionMySQL;
import com.adera.database.MachineDatabase;
import com.adera.entities.ComponentEntity;
import com.adera.entities.EstablishmentEntity;
import com.adera.entities.MachineEntity;
import com.adera.entities.MetricEntity;
import com.adera.enums.ComponentTypeEnum;
import com.adera.enums.MetricUnitEnum;
import com.adera.extensions.MySQLExtension;
import com.adera.mappers.ComponentMapper;
import com.adera.mappers.EstablishmentMapper;
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
import com.github.britooo.looca.api.group.rede.Rede;
import com.github.britooo.looca.api.group.rede.RedeInterface;
import com.github.britooo.looca.api.group.rede.RedeInterfaceGroup;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.github.britooo.looca.api.util.Conversor;
import jdk.jshell.spi.ExecutionControl;
import lombok.SneakyThrows;

import javax.crypto.Mac;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.adera.database.MachineDatabase.*;

public class Monitoring {
    private static Looca _looca;
    private Machine machine;
    private Establishment establishment;

    @SneakyThrows
    public static void setup(EstablishmentEntity establishment) {
        Monitoring monitor = new Monitoring();
        monitor._looca = new Looca();

        monitor.machine = MachineMapper.toMachine(getMachineByMacAddress(_looca.getRede().getGrupoDeInterfaces().getInterfaces().get(0).getEnderecoMac()), new ArrayList<Component>());
        monitor.establishment = EstablishmentMapper.toEstablishment(establishment);

        if(monitor.machine == null){
            monitor.machine = monitor.buildMachine(monitor.establishment.getId());
        } else {
            Boolean isNewMachine = monitor.checkIfComponentsAreEqual(monitor.machine);
        }

        if(!isNewMachine) {
            ComponentRepository repository = new ComponentRepository(new HashMap<>());
            ComponentDatabase database = new ComponentDatabase();

            try {
                var entityList = database.getComponentsByMachineId(monitor.machine.getId());

                var componentList = new ArrayList<Component>();

                for(var entity : entityList) {
                    componentList.add(ComponentMapper.toComponent(entity));
                }

                monitor.machine.setComponents(componentList);
            } catch(SQLException e) {
                MySQLExtension.handleException(e);
            }

            repository.commit();
        } else {
            monitor.machine.setId(UUID.randomUUID());
            MachineRepository machineRepository = new MachineRepository(new HashMap<>());
            machineRepository.registerNew(MachineMapper.toMachineEntity(monitor.machine));
            machineRepository.commit();

            ComponentRepository componentRepository = new ComponentRepository(new HashMap<>());
            ArrayList<Component> componentList = new ArrayList<>();
            for(int i = 0; i < monitor.machine.getComponents().size(); i++) {
                var component = monitor.machine.getComponents().get(i);
                component.setId(UUID.randomUUID());
                componentRepository.registerNew(ComponentMapper.toComponentEntity(component, monitor.machine.getId()));
                componentList.add(component);
            }
            monitor.machine.setComponents(componentList);
            componentRepository.commit();
        }

        monitor.start();
    }

    private void start() {
        System.out.println("Iniciando o monitoramento na maquina " + this.machine.getId());

        MetricRepository repository = new MetricRepository();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                var components = machine.getComponents();

                for(var component : components) {
                    if(component.getType() == ComponentTypeEnum.CPU) {
                        repository.registerNew(getCpuMetric(component.getId()));
                    }else if(component.getType() == ComponentTypeEnum.MEMORY) {
                        repository.registerNew(getMemoryMetric(component.getId()));
                    } else if (component.getType() == ComponentTypeEnum.DISK) {
                        repository.registerNew(getDiskMetric(component.getId(), _looca.getGrupoDeDiscos().getVolumes()));
                    } else if (component.getType() == ComponentTypeEnum.NETWORK) {
                        repository.registerNew(getLatencyMetric(component.getId()));
                    }
                }

                repository.commit();
                System.out.println("Métricas inseridas no banco de dados!");
            }
        };

        Timer timer = new Timer();

        timer.schedule(task, new java.util.Date(), 1000);
    }

    private MetricEntity getCpuMetric(UUID componentId) {
        return new MetricEntity(
                UUID.randomUUID(),
                LocalDateTime.now(),
                Double.toString(this._looca.getProcessador().getUso()),
                componentId
        );
    }

    private MetricEntity getMemoryMetric(UUID componentId) {
        return new MetricEntity(
                UUID.randomUUID(),
                LocalDateTime.now(),
                Conversor.formatarBytes(this._looca.getMemoria().getEmUso()),
                componentId
        );
    }

    private MetricEntity getDiskMetric(UUID componentId, List<Volume> disk) {
        Long total = disk.stream().mapToLong(Volume::getTotal).sum();
        Long available = disk.stream().mapToLong(Volume::getDisponivel).sum();

        Long inUse = total - available;
        Double percentageUsing = (inUse.doubleValue()/total)*100;

        return new MetricEntity(
                UUID.randomUUID(),
                LocalDateTime.now(),
                String.format("%.0f%%", percentageUsing),
                componentId
        );
    }

    private MetricEntity getLatencyMetric(UUID componentId){
        try {
            InetAddress address = InetAddress.getByName("www.google.com");

            long startTime = System.currentTimeMillis();
            if (address.isReachable(1000)) {
                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;

                return new MetricEntity(
                        UUID.randomUUID(),
                        LocalDateTime.now(),
                        String.format("%d", latency),
                        componentId
                );
            }
        }catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private Machine buildMachine(UUID establishmentId) {
        assert this._looca != null;

        Machine machine = new Machine();

        ArrayList<Component> components = new ArrayList<>();

        Sistema sys = this._looca.getSistema();
        machine.setMachineName("PlaceHolder");
        machine.setOs(sys.getSistemaOperacional());
        machine.setVendor(sys.getFabricante());
        machine.setArchitecture(sys.getArquitetura());
        machine.setMacAddress(this._looca.getRede().getGrupoDeInterfaces().getInterfaces().get(0).getEnderecoMac());
        machine.setEstablishmentId(establishmentId);

        Processador cpu = this._looca.getProcessador();
        String cpuDescription = String.format("%s, Núcleos: %d, Threads: %d", cpu.getMicroarquitetura(), cpu.getNumeroCpusFisicas(), cpu.getNumeroCpusLogicas());

        Component cpuComponent = new Component(
                null,
                cpu.getNome(),
                cpuDescription,
                cpu.getNumeroCpusLogicas().doubleValue(),
                ComponentTypeEnum.CPU,
                MetricUnitEnum.PORCENTAGEM
        );
        components.add(cpuComponent);

        Memoria mem = new Memoria();
        Component memoryComponent = new Component();
        memoryComponent.setType(ComponentTypeEnum.MEMORY);
        memoryComponent.setMetricUnit(MetricUnitEnum.BYTE);
        memoryComponent.setModel(Conversor.formatarBytes(mem.getTotal()));
        memoryComponent.setCapacity(mem.getTotal().doubleValue());
        memoryComponent.setDescription(Conversor.formatarBytes(mem.getTotal()));
        components.add(memoryComponent);

        DiscoGrupo disks = this._looca.getGrupoDeDiscos();
        for (Disco disk : disks.getDiscos()) {
            Component diskComponent = new Component();
            diskComponent.setType(ComponentTypeEnum.DISK);
            diskComponent.setMetricUnit(MetricUnitEnum.BYTE);
            diskComponent.setModel(disk.getModelo());
            diskComponent.setCapacity(disk.getTamanho().doubleValue());
            diskComponent.setDescription(Conversor.formatarBytes(disk.getTamanho()));
            components.add(diskComponent);
        }

        RedeInterfaceGroup networks = this._looca.getRede().getGrupoDeInterfaces();
        for (RedeInterface network : networks.getInterfaces()){
            Component networkComponent = new Component();
            networkComponent.setType(ComponentTypeEnum.NETWORK);
            networkComponent.setMetricUnit(MetricUnitEnum.MILISEGUNDOS);
            networkComponent.setModel(network.getNome());
            networkComponent.setCapacity(0.0);
            networkComponent.setDescription(network.getNomeExibicao());
            components.add(networkComponent);
        }

        machine.setComponents(components);
        return machine;
    }

    @SneakyThrows
    private Boolean checkIfComponentsAreEqual(Machine machine) {
            var componentEntities = ComponentDatabase.getComponentsByMachineId(machine.getId());
            var components = ComponentDatabase.getComponentsByMachineId(machine.getId());
            machine.setComponents(new ArrayList<Component>(components.stream().map(ComponentMapper::toComponent).toList()));

            return machine.equals(buildMachine(this.establishment.getId()));
    }
}
