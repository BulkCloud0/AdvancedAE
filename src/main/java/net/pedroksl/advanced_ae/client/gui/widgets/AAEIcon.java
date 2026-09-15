package net.pedroksl.advanced_ae.client.gui.widgets;

import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.client.gui.style.Blitter;
import appeng.core.AppEng;

/**
 * Icon atlas entries used by AdvancedAE's 1.16 GUI code.
 *
 * The modern addon used ae2addonlib's IBlitterIcon. AE2 8.4.7 already ships
 * the Blitter utility we need, so keeping the atlas description local avoids
 * an unnecessary runtime/compile-time dependency.
 */
public enum AAEIcon {
    ME_EXPORT_ON(0, 0),
    ME_EXPORT_OFF(16, 0),
    DIRECTION_OUTPUT(32, 0),
    VALID_INPUT(48, 0),
    INVALID_INPUT(64, 0),
    CLEAR(80, 0),
    CLEAR_DISABLED(96, 0),
    WHITELIST(112, 0),
    BLACKLIST(128, 0),
    FILTERED_IMPORT_ON(144, 0),
    FILTERED_IMPORT_OFF(160, 0),
    PALETTE(176, 0),
    PALETTE_DISABLED(192, 0),

    CONFIRM(48, 16),

    CLEAR_SMALL(0, 16),
    REGULATE_ON(16, 16),
    REGULATE_OFF(32, 16),

    SHOW_ON_CRAFTER_TERMINAL(64, 80, TextureSource.AE2),
    HIDE_ON_CRAFTER_TERMINAL(80, 80, TextureSource.AE2),
    CRAFTER_TERMINAL_VISIBLE(96, 80, TextureSource.AE2),
    CRAFTER_TERMINAL_ALL(112, 80, TextureSource.AE2),
    CRAFTER_TERMINAL_NOT_FULL(128, 80, TextureSource.AE2),

    TOOLBAR_BUTTON_BACKGROUND(176, 128, 18, 18),
    TOOLBAR_BUTTON_ENABLED(194, 128, 18, 18);

    public static final ResourceLocation TEXTURE = AdvancedAE.makeId("textures/guis/states.png");
    private static final ResourceLocation AE2_TEXTURE =
            new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png");
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 256;

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    private final TextureSource textureSource;

    AAEIcon(int x, int y) {
        this(x, y, 16, 16);
    }

    AAEIcon(int x, int y, TextureSource textureSource) {
        this(x, y, 16, 16, textureSource);
    }

    AAEIcon(int x, int y, int width, int height) {
        this(x, y, width, height, TextureSource.CUSTOM);
    }

    AAEIcon(int x, int y, int width, int height, TextureSource textureSource) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.textureSource = textureSource;
    }

    public ResourceLocation getTexture() {
        return textureSource == TextureSource.AE2 ? AE2_TEXTURE : TEXTURE;
    }

    public Rectangle2d getRect() {
        return new Rectangle2d(x, y, width, height);
    }

    public Blitter getBlitter() {
        return Blitter.texture(getTexture(), TEXTURE_WIDTH, TEXTURE_HEIGHT)
                .src(x, y, width, height);
    }

    private enum TextureSource {
        CUSTOM,
        AE2
    }
}
