package com.adera.commonTypes;

import java.util.UUID;

public final class Config {
    private String userId;

    private UUID establishmentId;

    @Override
    public String toString() {
        return "Config{" +
                "userId='" + userId + '\'' +
                ", establishmentId='" + establishmentId + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UUID getEstablishmentId() {
        return establishmentId;
    }

    public void setEstablishmentId(UUID establishmentId) {
        this.establishmentId = establishmentId;
    }
}
