package net.pedroksl.ae2addonlib.registry;

import net.minecraftforge.common.ForgeConfigSpec;

/** Minimal config helper retained from AE2AddonLib for the 1.16.5 backport. */
public class ConfigRegistry {
    protected static ForgeConfigSpec.BooleanValue define(
            ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        builder.comment(comment);
        return builder.define(name, defaultValue);
    }

    protected static ForgeConfigSpec.BooleanValue define(
            ForgeConfigSpec.Builder builder, String name, boolean defaultValue) {
        return builder.define(name, defaultValue);
    }

    protected static ForgeConfigSpec.IntValue define(
            ForgeConfigSpec.Builder builder, String name, int defaultValue, String comment) {
        builder.comment(comment);
        return builder.defineInRange(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    protected static ForgeConfigSpec.IntValue define(
            ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        builder.comment(comment);
        return builder.defineInRange(name, defaultValue, min, max);
    }

    protected static ForgeConfigSpec.IntValue define(
            ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    protected static ForgeConfigSpec.IntValue define(
            ForgeConfigSpec.Builder builder, String name, int defaultValue) {
        return builder.defineInRange(name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    protected static ForgeConfigSpec.DoubleValue define(
            ForgeConfigSpec.Builder builder, String name, double defaultValue, String comment) {
        builder.comment(comment);
        return builder.defineInRange(name, defaultValue, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    protected static ForgeConfigSpec.DoubleValue define(
            ForgeConfigSpec.Builder builder, String name, double defaultValue) {
        return builder.defineInRange(name, defaultValue, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    protected static ForgeConfigSpec.DoubleValue define(
            ForgeConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        builder.comment(comment);
        return builder.defineInRange(name, defaultValue, min, max);
    }

    protected static ForgeConfigSpec.DoubleValue define(
            ForgeConfigSpec.Builder builder, String name, double defaultValue, double min, double max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    protected static <T extends Enum<T>> ForgeConfigSpec.EnumValue<T> defineEnum(
            ForgeConfigSpec.Builder builder, String name, T defaultValue) {
        return builder.defineEnum(name, defaultValue);
    }

    protected static <T extends Enum<T>> ForgeConfigSpec.EnumValue<T> defineEnum(
            ForgeConfigSpec.Builder builder, String name, T defaultValue, String comment) {
        builder.comment(comment);
        return builder.defineEnum(name, defaultValue);
    }
}
