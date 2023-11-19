package com.adera.enums;

public enum MetricUnitEnum {
    BYTE(1, "byte"),
    PORCENTAGEM(2, "porcentagem"),
    HERTZ(3, "hertz"),
    MILISEGUNDOS(4, "milisegundos");

    private final Integer id;
    private final String name;

    MetricUnitEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static MetricUnitEnum fromName(String name){
        return MetricUnitEnum.valueOf(name.toUpperCase());
    }
}

