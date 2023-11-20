package com.adera.mappers;

import com.adera.commonTypes.Command;
import com.adera.entities.CommandEntity;

public class CommandMapper {
    public static Command toCommand(CommandEntity self) {
        return new Command(
                self.getId(),
                self.getComand(),
                self.getExecuted(),
                self.getMachineId()
        );
    }
}
