package com.adera;

import com.adera.commonTypes.Command;
import com.adera.commonTypes.Machine;
import com.adera.database.CommandDatabase;
import com.adera.mappers.CommandMapper;
import com.adera.repositories.CommandRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class CommandListener {
    private Stream<Command> commandQueue;

    private final Machine _machine;

    public CommandListener(Machine _machine) {
        this._machine = _machine;
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
}
