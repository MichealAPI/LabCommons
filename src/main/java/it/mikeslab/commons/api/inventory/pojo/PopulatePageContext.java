package it.mikeslab.commons.api.inventory.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PopulatePageContext {

    private char targetChar;
    private List<GuiElement> subList;

}