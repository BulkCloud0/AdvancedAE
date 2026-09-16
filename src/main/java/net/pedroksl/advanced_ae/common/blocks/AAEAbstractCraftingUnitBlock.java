package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.block.AEBaseTileBlock;
import appeng.tile.AEBaseTileEntity;
import appeng.util.InteractionUtil;

public abstract class AAEAbstractCraftingUnitBlock<T extends AEBaseTileEntity> extends AEBaseTileBlock<T> {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty MULTIBLOCKED = BooleanProperty.create("multiblocked");
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 15);

    public final AAECraftingUnitType type;

    public AAEAbstractCraftingUnitBlock(AbstractBlock.Properties props, AAECraftingUnitType type) {
        super(props);
        this.type = type;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FORMED, false)
                .setValue(POWERED, false)
                .setValue(MULTIBLOCKED, false)
                .setValue(LIGHT_LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED, FORMED, MULTIBLOCKED, LIGHT_LEVEL);
    }

    @Override
    public void neighborChanged(
            BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        T tile = this.getTileEntity(world, pos);
        if (tile instanceof AdvCraftingBlockEntity) {
            ((AdvCraftingBlockEntity) tile).updateMultiBlock(fromPos);
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != state.getBlock()) {
            T tile = this.getTileEntity(world, pos);
            if (tile instanceof AdvCraftingBlockEntity) {
                ((AdvCraftingBlockEntity) tile).breakCluster();
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public ActionResultType use(
            BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        T tile = this.getTileEntity(world, pos);
        if (tile instanceof AdvCraftingBlockEntity) {
            AdvCraftingBlockEntity crafting = (AdvCraftingBlockEntity) tile;
            if (!InteractionUtil.isInAlternateUseMode(player) && crafting.isFormed() && crafting.isActive()) {
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}
