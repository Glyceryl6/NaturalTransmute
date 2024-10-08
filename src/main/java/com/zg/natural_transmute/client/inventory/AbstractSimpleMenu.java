package com.zg.natural_transmute.client.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSimpleMenu extends AbstractContainerMenu {

    protected final Level level;
    protected final ContainerLevelAccess access;

    protected AbstractSimpleMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(menuType, containerId);
        this.level = inventory.player.level();
        this.addPlayerInventory(inventory);
        this.addPlayerHotBar(inventory);
        this.access = access;
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                int index = l + i * 9 + 9;
                int x = 8 + l * 18;
                int y = 84 + i * 18;
                this.addSlot(new Slot(inventory, index, x, y));
            }
        }
    }

    private void addPlayerHotBar(Inventory inventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

}