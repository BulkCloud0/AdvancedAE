package net.pedroksl.advanced_ae.common.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

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
    public void appendHoverText(
            ItemStack stack, @Nullable World level, List<ITextComponent> lines, ITooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, lines, tooltipFlag);
        if (type == UpgradeType.EMPTY) {
            lines.add(type.getTooltip());
            return;
        }

        lines.add(AAEText.QuantumUpgradeTooltip.text());
        lines.add(type.getTooltip());
        List<QuantumArmorBase> list = QuantumArmorBase.upgradeAvailableFor(type);
        lines.add(new StringTextComponent(""));
        lines.add(AAEText.UpgradeTooltip.text());
        for (QuantumArmorBase equip : list) {
            lines.add(new TranslationTextComponent(equip.getDescriptionId()));
        }
    }
}
