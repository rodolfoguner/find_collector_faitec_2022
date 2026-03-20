package br.fai.findcollectors.enums;

import br.fai.findcollectors.exceptions.BusinessRuleException;

import java.util.Arrays;

public enum PersonType {

    RECYCLER,
    COLLECTOR;

    public static PersonType fromString(String value) {
        return Arrays.stream(PersonType.values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("invalid personType"));
    }
}
