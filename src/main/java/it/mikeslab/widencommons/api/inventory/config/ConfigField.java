package it.mikeslab.widencommons.api.inventory.config;

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
    ACTION("action");

    private final String field;

}
