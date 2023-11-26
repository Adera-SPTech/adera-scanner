package com.adera.enums;

import lombok.Getter;

import java.util.ArrayList;

public enum CommandEnum {
    RESTART(1, "restart", new String[]{"shutdown /r /t 10"}, new String[]{"sudo systemctl reboot && sleep 10"}),
    OPTIMIZE(2, "optimize", new String[]{"sfc /scannow", "runas /user:Administrator dism /online /cleanup-image /restorehealth", "netsh winsock reset", "ipconfig /flushdns"}, new String[]{"sudo apt-get update", "sudo apt-get install -f", "sudo systemd-resolve --flush-caches"});
    private final Integer id;
    private final String name;

    @Getter
    private final String[] windowsCommand;

    @Getter
    private final String[] linuxCommand;

    CommandEnum(Integer id, String name, String[] windowsCommand, String[] linuxCommand) {
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

