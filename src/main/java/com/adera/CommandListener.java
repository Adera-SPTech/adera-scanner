package com.adera;

import com.adera.commonTypes.Command;
import com.adera.commonTypes.Machine;
import com.adera.commonTypes.Options;
import com.adera.database.CommandDatabase;
import com.adera.database.OptionDatabase;
import com.adera.enums.CommandEnum;
import com.adera.mappers.CommandMapper;
import com.adera.mappers.OptionsMapper;
import com.adera.repositories.CommandRepository;
import com.github.britooo.looca.api.core.Looca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

public class CommandListener {
    private Stream<Command> commandQueue;

    private final UUID _establishemntId;

    private final Machine _machine;

    private Options _options;

    public CommandListener(UUID establishmentId, Machine _machine) {
        this._establishemntId = establishmentId;
        this._machine = _machine;
        this._options = OptionsMapper.toOptions(OptionDatabase.getOptionsByEstablishmentId(establishmentId));
    }

    public void fetchCommands() {
        var commands = CommandDatabase.getCommandsByMachineId(_machine.getId());
        commandQueue = commands.stream().map(CommandMapper::toCommand);
    }

    public void runCommands() {
        var commandRepository = new CommandRepository(new HashMap<>());
        var isWindows = _machine.getOs().equals("Windows");

        Runtime runtime = Runtime.getRuntime();
        commandQueue.forEach(command -> {
                command.setExecuted(true);
                commandRepository.registerModified(CommandMapper.toCommandEntity(command));
                commandRepository.commit();

                var current = isWindows ? command.getCommand().getWindowsCommand() : command.getCommand().getLinuxCommand();

                Arrays.stream(current).forEach(s -> {
                    try {
                        Process pc = runtime.exec(s);
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(pc.getErrorStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.err.println("Error: " + line);
                            }
                        }

                        int exitCode = pc.waitFor();
                        System.out.println("Command exited with code: " + exitCode);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
        });
    }

    public void watch() {
        var isWindows = _machine.getOs().equals("Windows");
        var restartTime = _options.getRestartTime();
        Runtime runtime = Runtime.getRuntime();

        var now = LocalTime.now();

        try {
            var restartCommand = isWindows ? CommandEnum.RESTART.getWindowsCommand() : CommandEnum.RESTART.getLinuxCommand();
            if((now == restartTime && _options.getPeriodicalRestart()) || (isCrashed() && _options.getAutoRestart())) {
                runtime.exec(restartCommand);
            }
        } catch (IOException e) {
            Logger.logError(e.getMessage(), e);
        }
    }

    private boolean isCrashed() {
        var l = new Looca();
        return l.getProcessador().getUso() >= 98.0 || Objects.equals(l.getMemoria().getTotal(), l.getMemoria().getEmUso());
    }
}
