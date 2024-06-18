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
    IS_GROUP_ELEMENT("is-group-element"),
    CONDITION("condition");

    private final String field;

}
