package net.pedroksl.advanced_ae.api;

import appeng.api.util.IConfigurableObject;

/**
 * Common host contract for the quantum crafter terminal on AE2 8.x.
 *
 * AE2 8.4.7 has no ISubMenuHost API; submenu navigation is handled by the
 * concrete 1.16 containers instead, so the shared host only needs to expose
 * its configuration manager here.
 */
public interface IQuantumCrafterTermMenuHost extends IConfigurableObject {}
