package net.pedroksl.advanced_ae.client.gui;

import java.util.Iterator;

import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.pedroksl.advanced_ae.common.helpers.AutoCraftingContainer;

/**
 * This class is used on the client-side to represent a pattern provider and its inventory as it is shown in the
 * {@link QuantumCrafterTermScreen}'s table.
 */
public class AutoCrafterContainerRecord implements Comparable<AutoCrafterContainerRecord> {

    /**
     * Identifier for this quantum crafter on the server-side. See {@link QuantumCrafterTermScreen}
     */
    private final long serverId;

    // The client-side representation of the machine's inventory, which is only used for display purposes.
    private final DisplayInventory inventory;

    private final Int2BooleanArrayMap enabledArray;
    private final Int2BooleanArrayMap invalidArray;

    /**
     * Used to sort this record in the pattern access terminal's table, comes from
     * {@link AutoCraftingContainer#getTerminalSortOrder()}
     */
    private final long order;

    public AutoCrafterContainerRecord(long serverId, int slots, long order) {
        this.inventory = new DisplayInventory(slots);
        this.enabledArray = new Int2BooleanArrayMap(slots);
        this.invalidArray = new Int2BooleanArrayMap(slots);
        this.serverId = serverId;
        this.order = order;
    }

    @Override
    public int compareTo(AutoCrafterContainerRecord o) {
        return Long.compare(this.order, o.order);
    }

    public long getServerId() {
        return this.serverId;
    }

    public DisplayInventory getInventory() {
        return inventory;
    }

    public Int2BooleanArrayMap getEnabledArray() {
        return enabledArray;
    }

    public Int2BooleanArrayMap getInvalidArray() {
        return invalidArray;
    }

    /**
     * Minimal client-side inventory used only to mirror the terminal's pattern slots. Keeping this local avoids a
     * dependency on AppEngInternalInventory, which does not exist in AE2 8.4.7.
     */
    public static final class DisplayInventory implements Iterable<ItemStack> {
        private final NonNullList<ItemStack> stacks;

        private DisplayInventory(int slots) {
            this.stacks = NonNullList.withSize(slots, ItemStack.EMPTY);
        }

        public void setItemDirect(int slot, ItemStack stack) {
            this.stacks.set(slot, stack == null ? ItemStack.EMPTY : stack);
        }

        public int size() {
            return this.stacks.size();
        }

        @Override
        public Iterator<ItemStack> iterator() {
            return this.stacks.iterator();
        }
    }
}
