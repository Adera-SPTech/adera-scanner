package com.adera.commonTypes;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Config {
    public static String userId;

    public static UUID establishmentId;

    @Override
    public String toString() {
        return "Config{" +
                "userId='" + userId + '\'' +
                ", establishmentId='" + establishmentId + '\'' +
                '}';
    }
}
