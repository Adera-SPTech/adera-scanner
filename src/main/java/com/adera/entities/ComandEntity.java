package com.adera.entities;

import com.adera.enums.ComandEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ComandEntity {

    private UUID id;

    private ComandEnum comand;

    private Boolean executed;

    private UUID establishmentId;
}
