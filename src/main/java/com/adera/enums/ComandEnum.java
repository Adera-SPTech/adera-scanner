package com.adera.enums;

public enum ComandEnum {
    RESTART(1, "byte"),
    CLEANCACHE(2, "porcentagem");
    private final Integer id;
    private final String name;

    ComandEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static ComandEnum fromName(String name){
        return ComandEnum.valueOf(name.toUpperCase());
    }
}

