package it.mikeslab.commons.api.inventory.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfigField {

    AMOUNT("amount"),
    DISPLAYNAME("displayName"),
    LORE("lore"),
    LAYOUT("layout"),
    GLOWING("glowing"),
    ELEMENTS("elements"),
    TITLE("title"),
    TYPE("type"),
    SIZE("size"),
    MATERIAL("material"),
    ACTIONS("actions"),
    CUSTOM_MODEL_DATA("data"),
    INTERNAL_VALUE("internalValue"),
    IS_GROUP_ELEMENT("isGroupElement"),
    CONDITION("condition"),
    HEAD_VALUE("headValue");
    // ON_OPEN_ACTIONS("on-open-actions"),
    // ON_CLOSE_ACTIONS("on-close-actions"),;

    private final String field;

}
