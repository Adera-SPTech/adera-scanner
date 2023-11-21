package com.adera;

import com.adera.commonTypes.Command;
import com.adera.commonTypes.Machine;
import com.adera.database.CommandDatabase;
import com.adera.enums.CommandEnum;
import com.adera.mappers.CommandMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

public class CommandListener {
    private Stream<Command> commandQueue;

    private final Machine _machine;

    public CommandListener(Machine _machine) {
        this._machine = _machine;
    }

    public void fetchCommands() {
        var commands = CommandDatabase.getCommandsByMachineId(_machine.getId());
        System.out.println(commands);
        commandQueue = commands.stream().map(CommandMapper::toCommand);
    }

    public void runCommands() {
        var isWindows = _machine.getOs().equals("Windows");

        Runtime runtime = Runtime.getRuntime();
        commandQueue.forEach(command -> {
            try {
                var current = isWindows ? command.getComand().getWindowsCommand() : command.getComand().getLinuxCommand();
                Process pc = runtime.exec(current);
                pc.info();
            } catch (IOException e) {
                // Implementar logger
            }
        });
    }
}
