package it.mikeslab.commons.api.inventory.pojo;

import it.mikeslab.commons.api.inventory.factory.GuiFactory;
import it.mikeslab.commons.api.inventory.InventoryType;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Data
@Builder
public class GuiContext {

    private Optional<Map<String, Consumer<GuiInteractEvent>>> consumers;
    private GuiDetails defaultGuiDetails;

    private String fieldIdentifier;

    private Path relativePath; // The file name of the inventory

    @Deprecated
    private boolean closeOnFail; // If the inventory should be closed if the checks fail // todo remove

    private InventoryType inventoryType; // The type of the inventory

    private GuiFactory guiFactory;

    private int id = -1; // Default is -1 to indicate that it is not set

}
