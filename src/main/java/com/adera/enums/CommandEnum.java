package com.adera.enums;

import lombok.Getter;

public enum CommandEnum {
    RESTART(1, "restart", "shutdown /r /t 0", "sudo systemctl reboot"),
    CLEANCACHE(2, "cleancache", "", "");
    private final Integer id;
    private final String name;

    @Getter
    private final String windowsCommand;

    @Getter
    private final String linuxCommand;

    CommandEnum(Integer id, String name, String windowsCommand, String linuxCommand) {
        this.id = id;
        this.name = name;
        this.windowsCommand = windowsCommand;
        this.linuxCommand = linuxCommand;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static CommandEnum fromName(String name){
        return CommandEnum.valueOf(name.toUpperCase());
    }
}

