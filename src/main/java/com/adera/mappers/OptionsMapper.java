package com.adera.mappers;

import com.adera.commonTypes.Options;
import com.adera.entities.OptionsEntity;

public class OptionsMapper {
    public static Options toOptions(OptionsEntity self) {
        return new Options(
                self.getId(),
                self.getAutoRestart(),
                self.getPeriodicalRestart(),
                self.getRestartTime(),
                self.getCpuAttention(),
                self.getRamAttention(),
                self.getDiskAttention(),
                self.getLatencyAttention(),
                self.getCpuLimit(),
                self.getRamLimit(),
                self.getDiskLimit(),
                self.getLatencyLimit(),
                self.getEstablishmentId()
        );
    }
}
