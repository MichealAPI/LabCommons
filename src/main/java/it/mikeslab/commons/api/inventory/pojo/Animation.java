package it.mikeslab.commons.api.inventory.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Animation {

    private final GuiElement guiElement;
    private final List<Integer> slots;


}
