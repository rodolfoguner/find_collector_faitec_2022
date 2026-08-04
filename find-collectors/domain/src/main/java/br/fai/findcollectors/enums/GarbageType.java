package br.fai.findcollectors.enums;

import br.fai.findcollectors.exceptions.BusinessRuleException;

import java.util.Arrays;

public enum GarbageType {

    PAPER,
    GLASS,
    PLASTIC,
    METAL,
    ORGANIC,
    NO_RECICLABLE;

    public static GarbageType fromString(String value) {
        return Arrays.stream(GarbageType.values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("invalid garbageType"));
    }
}