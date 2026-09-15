package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

public class AAECraftingUnitBlock extends AAEAbstractCraftingUnitBlock<AdvCraftingBlockEntity> {

    public AAECraftingUnitBlock(AAECraftingUnitType type) {
        super(getProps(type), type);
    }

    private static AbstractBlock.Properties getProps(AAECraftingUnitType type) {
        return getProps(type, false);
    }

    public static AbstractBlock.Properties getProps(AAECraftingUnitType type, boolean formed) {
        AbstractBlock.Properties props = defaultProps(type == AAECraftingUnitType.STRUCTURE ? Material.GLASS : Material.IRON);
        if (type == AAECraftingUnitType.QUANTUM_CORE || type == AAECraftingUnitType.STRUCTURE) {
            props.setLightLevel(state -> state.get(AAEAbstractCraftingUnitBlock.LIGHT_LEVEL));
            props.notSolid();
        }
        return props;
    }
}
