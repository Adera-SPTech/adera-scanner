package com.adera.enums;

import lombok.Getter;

@Getter
public enum ComponentTypeEnum {
    CPU(1),
    MEMORY(2),
    DISK(3),
    NETWORK(4);

    private final Integer id;

    ComponentTypeEnum(Integer id) {
        this.id = id;
    }

}
