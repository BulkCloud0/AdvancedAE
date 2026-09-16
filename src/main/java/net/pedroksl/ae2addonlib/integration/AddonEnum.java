package net.pedroksl.ae2addonlib.integration;

/**
 * Minimal compatibility contract used by the 1.16.5 AdvancedAE backport.
 * It replaces the external AE2AddonLib type while preserving the source-level API.
 */
public interface AddonEnum {
    String getModId();

    String getModName();

    boolean isLoaded();
}
