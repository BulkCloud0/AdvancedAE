package net.pedroksl.advanced_ae.client.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;

import appeng.blockentity.crafting.CraftingCubeModelData;
import appeng.client.render.cablebus.CubeBuilder;
import appeng.util.Platform;

abstract class AAECraftingCubeBakedModel implements IDynamicBakedModel {
    private static final ChunkRenderTypeSet RENDER_TYPES = ChunkRenderTypeSet.of(RenderType.cutout());

    private final TextureAtlasSprite ringCorner;
    private final TextureAtlasSprite ringHor;
    private final TextureAtlasSprite ringVer;

    AAECraftingCubeBakedModel(TextureAtlasSprite ringCorner, TextureAtlasSprite ringHor, TextureAtlasSprite ringVer) {
        this.ringCorner = ringCorner;
        this.ringHor = ringHor;
        this.ringVer = ringVer;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            @NotNull RandomSource rand,
            @NotNull ModelData extraData,
            RenderType renderType) {
        if (side == null) {
            return Collections.emptyList();
        }

        EnumSet<Direction> connections = getConnections(extraData);
        List<BakedQuad> quads = new ArrayList<>();
        CubeBuilder builder = new CubeBuilder(quads);
        builder.setDrawFaces(EnumSet.of(side));

        this.addRing(builder, side, connections);

        float x2 = connections.contains(Direction.EAST) ? 16 : 14.01f;
        float x1 = connections.contains(Direction.WEST) ? 0 : 1.99f;
        float y2 = connections.contains(Direction.UP) ? 16 : 14.01f;
        float y1 = connections.contains(Direction.DOWN) ? 0 : 1.99f;
        float z2 = connections.contains(Direction.SOUTH) ? 16 : 14.01f;
        float z1 = connections.contains(Direction.NORTH) ? 0 : 1.99f;

        switch (side) {
            case DOWN:
            case UP:
                y1 = 0;
                y2 = 16;
                break;
            case NORTH:
            case SOUTH:
                z1 = 0;
                z2 = 16;
                break;
            case WEST:
            case EAST:
                x1 = 0;
                x2 = 16;
                break;
            default:
                break;
        }

        this.addInnerCube(side, state, extraData, builder, x1, y1, z1, x2, y2, z2);
        return quads;
    }

    private void addRing(CubeBuilder builder, Direction side, EnumSet<Direction> connections) {
        builder.setTexture(this.ringCorner);
        this.addCornerCap(builder, connections, side, Direction.UP, Direction.EAST, Direction.NORTH);
        this.addCornerCap(builder, connections, side, Direction.UP, Direction.EAST, Direction.SOUTH);
        this.addCornerCap(builder, connections, side, Direction.UP, Direction.WEST, Direction.NORTH);
        this.addCornerCap(builder, connections, side, Direction.UP, Direction.WEST, Direction.SOUTH);
        this.addCornerCap(builder, connections, side, Direction.DOWN, Direction.EAST, Direction.NORTH);
        this.addCornerCap(builder, connections, side, Direction.DOWN, Direction.EAST, Direction.SOUTH);
        this.addCornerCap(builder, connections, side, Direction.DOWN, Direction.WEST, Direction.NORTH);
        this.addCornerCap(builder, connections, side, Direction.DOWN, Direction.WEST, Direction.SOUTH);

        for (Direction a : Direction.values()) {
            if (a == side || a == side.getOpposite()) {
                continue;
            }

            if (side.getAxis() != Direction.Axis.Y
                    && (a == Direction.NORTH || a == Direction.EAST || a == Direction.WEST || a == Direction.SOUTH)) {
                builder.setTexture(this.ringVer);
            } else if (side.getAxis() == Direction.Axis.Y && (a == Direction.EAST || a == Direction.WEST)) {
                builder.setTexture(this.ringVer);
            } else {
                builder.setTexture(this.ringHor);
            }

            if (!connections.contains(a)) {
                float x1 = 0, y1 = 0, z1 = 0, x2 = 16, y2 = 16, z2 = 16;

                switch (a) {
                    case DOWN:
                        y1 = 0;
                        y2 = 2;
                        break;
                    case UP:
                        y1 = 14.0f;
                        break;
                    case WEST:
                        x1 = 0;
                        x2 = 2;
                        break;
                    case EAST:
                        x1 = 14;
                        break;
                    case NORTH:
                        z1 = 0;
                        z2 = 2;
                        break;
                    case SOUTH:
                        z1 = 14;
                        break;
                    default:
                        break;
                }

                Direction perpendicular = Platform.rotateAround(a, side);
                for (Direction cornerCandidate : EnumSet.of(perpendicular, perpendicular.getOpposite())) {
                    if (!connections.contains(cornerCandidate)) {
                        switch (cornerCandidate) {
                            case DOWN:
                                y1 = 2;
                                break;
                            case UP:
                                y2 = 14;
                                break;
                            case NORTH:
                                z1 = 2;
                                break;
                            case SOUTH:
                                z2 = 14;
                                break;
                            case WEST:
                                x1 = 2;
                                break;
                            case EAST:
                                x2 = 14;
                                break;
                            default:
                                break;
                        }
                    }
                }

                builder.addCube(x1, y1, z1, x2, y2, z2);
            }
        }
    }

    private void addCornerCap(
            CubeBuilder builder,
            EnumSet<Direction> connections,
            Direction side,
            Direction down,
            Direction west,
            Direction north) {
        if (connections.contains(down) || connections.contains(west) || connections.contains(north)) {
            return;
        }
        if (side != down && side != west && side != north) {
            return;
        }

        float x1 = west == Direction.WEST ? 0 : 14;
        float y1 = down == Direction.DOWN ? 0 : 14;
        float z1 = north == Direction.NORTH ? 0 : 14;
        float x2 = west == Direction.WEST ? 2 : 16;
        float y2 = down == Direction.DOWN ? 2 : 16;
        float z2 = north == Direction.NORTH ? 2 : 16;
        builder.addCube(x1, y1, z1, x2, y2, z2);
    }

    private static EnumSet<Direction> getConnections(ModelData modelData) {
        if (modelData.has(CraftingCubeModelData.CONNECTIONS)) {
            return modelData.get(CraftingCubeModelData.CONNECTIONS);
        }
        return EnumSet.noneOf(Direction.class);
    }

    protected abstract void addInnerCube(
            Direction facing,
            BlockState state,
            ModelData modelData,
            CubeBuilder builder,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2);

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.ringCorner;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @ParametersAreNonnullByDefault
    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return RENDER_TYPES;
    }
}
