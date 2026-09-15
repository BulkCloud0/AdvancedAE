package net.pedroksl.advanced_ae.common.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * Quantum armor upgrade item using the Minecraft 1.16 tooltip API.
 */
public class QuantumUpgradeBaseItem extends Item {
    private final UpgradeType type;

    public QuantumUpgradeBaseItem(Properties properties) {
        super(properties);
        this.type = UpgradeType.EMPTY;
    }

    public QuantumUpgradeBaseItem(UpgradeType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public UpgradeType getType() {
        return this.type;
    }

    @Override
    public void addInformation(
            ItemStack stack, @Nullable World world, List<ITextComponent> lines, ITooltipFlag tooltipFlag) {
        super.addInformation(stack, world, lines, tooltipFlag);
        lines.add(new TranslationTextComponent(type.getTranslationKey()).mergeStyle(TextFormatting.GRAY));
    }
}
