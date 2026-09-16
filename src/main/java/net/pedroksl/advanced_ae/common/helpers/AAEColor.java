package net.pedroksl.advanced_ae.common.helpers;

import java.awt.Color;

/** Java 8-compatible color helper used by AdvancedAE client and armor code. */
public final class AAEColor {
    public static final AAEColor WHITE = ofArgb(0xFFFFFFFF);
    public static final AAEColor LIGHT_GRAY = ofArgb(0xFFADB0C4);
    public static final AAEColor DARK_GRAY_BLUE = ofArgb(0xFF413F54);
    public static final AAEColor LIGHT_PURPLE = ofArgb(0x787D53C1);
    public static final AAEColor PURPLE = ofArgb(0xFF7110A5);
    public static final AAEColor DARK_GRAY = ofArgb(0xFF8B8B8B);

    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    private AAEColor(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    private AAEColor(int red, int green, int blue, int alpha) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
        this.alpha = clamp(alpha);
    }

    public static AAEColor ofArgb(int color) {
        int alpha = color >>> 24 & 0xFF;
        int red = color >>> 16 & 0xFF;
        int green = color >>> 8 & 0xFF;
        int blue = color & 0xFF;
        return new AAEColor(red, green, blue, alpha);
    }

    public static AAEColor ofRgb(int color) {
        int red = color >>> 16 & 0xFF;
        int green = color >>> 8 & 0xFF;
        int blue = color & 0xFF;
        return new AAEColor(red, green, blue);
    }

    public static AAEColor ofHsv(float hue, float saturation, float value) {
        return ofRgb(Color.HSBtoRGB(hue - 0.5e-7f, saturation, value));
    }

    public static AAEColor ofHsv(float hue, float saturation, float value, float alpha) {
        AAEColor rgb = ofHsv(hue, saturation, value);
        return new AAEColor(rgb.red, rgb.green, rgb.blue, Math.round(alpha * 255.0f));
    }

    public float r() { return red / 255.0f; }
    public float g() { return green / 255.0f; }
    public float b() { return blue / 255.0f; }
    public float a() { return alpha / 255.0f; }

    public int argb() {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public int argb(int alpha) {
        return clamp(alpha) << 24 | red << 16 | green << 8 | blue;
    }

    public int rgb() {
        return red << 16 | green << 8 | blue;
    }

    public HSV hsv() {
        float[] values = Color.RGBtoHSB(red, green, blue, null);
        return new HSV(values[0], values[1], values[2]);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public static final class HSV {
        private final float hue;
        private final float saturation;
        private final float value;

        public HSV(float hue, float saturation, float value) {
            this.hue = hue;
            this.saturation = saturation;
            this.value = value;
        }

        public float hue() { return hue; }
        public float saturation() { return saturation; }
        public float value() { return value; }
    }
}
