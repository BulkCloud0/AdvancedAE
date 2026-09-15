package net.pedroksl.advanced_ae.common.items.upgrades;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.ae2addonlib.registry.helpers.LibItemDefinition;

public enum UpgradeType {
    EMPTY("Empty", null, SettingType.NONE, 0, ApplicationType.PASSIVE, AAEItems.QUANTUM_UPGRADE_BASE),

    WALK_SPEED(
            "Walk Speed",
            UpgradeCards::walkSpeed,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE,
            AAEItems.WALK_SPEED_CARD),
    SPRINT_SPEED(
            "Sprint Speed",
            UpgradeCards::sprintSpeed,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE,
            AAEItems.SPRINT_SPEED_CARD),
    STEP_ASSIST("Step Assist", null, SettingType.NUM_INPUT, 5, ApplicationType.PASSIVE_USE, AAEItems.STEP_ASSIST_CARD),
    JUMP_HEIGHT(
            "Jump Height",
            UpgradeCards::jumpHeight,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE_USE,
            AAEItems.JUMP_HEIGHT_CARD),
    LAVA_IMMUNITY(
            "Lava Immunity", null, SettingType.NONE, 10, ApplicationType.PASSIVE_USE, AAEItems.LAVA_IMMUNITY_CARD),
    FLIGHT(
            "Flight",
            UpgradeCards::creativeFlight,
            SettingType.NUM_INPUT,
            10,
            ApplicationType.PASSIVE,
            AAEItems.FLIGHT_CARD),
    WATER_BREATHING(
            "Water Breathing", null, SettingType.NONE, 10, ApplicationType.PASSIVE_USE, AAEItems.WATER_BREATHING_CARD),
    AUTO_FEED(
            "Auto Feed",
            UpgradeCards::autoFeed,
            SettingType.FILTER,
            5,
            ApplicationType.PASSIVE,
            AAEItems.AUTO_FEED_CARD),
    AUTO_STOCK(
            "Auto Stock",
            UpgradeCards::autoStock,
            SettingType.FILTER,
            5,
            ApplicationType.PASSIVE,
            AAEItems.AUTO_STOCK_CARD),
    MAGNET(
            "Magnet",
            UpgradeCards::magnet,
            SettingType.NUM_AND_FILTER,
            5,
            ApplicationType.PASSIVE,
            AAEItems.MAGNET_CARD,
            ExtraSettings.BLACKLIST),
    HP_BUFFER("HP Buffer", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.HP_BUFFER_CARD),
    EVASION("Evasion", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.EVASION_CARD),
    REGENERATION(
            "Regeneration",
            UpgradeCards::regeneration,
            SettingType.NONE,
            10,
            ApplicationType.PASSIVE,
            AAEItems.REGENERATION_CARD),
    STRENGTH("Strength", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.STRENGTH_CARD),
    ATTACK_SPEED("Attack Speed", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.ATTACK_SPEED_CARD),
    LUCK("Luck Boost", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.LUCK_CARD),
    REACH("Reach Boost", null, SettingType.NUM_INPUT, 10, ApplicationType.BUFF, AAEItems.REACH_CARD),
    SWIM_SPEED(
            "Swim Speed",
            UpgradeCards::swimSpeed,
            SettingType.NUM_INPUT,
            5,
            ApplicationType.PASSIVE,
            AAEItems.SWIM_SPEED_CARD),
    NIGHT_VISION("Night Vision", null, SettingType.NONE, 10, ApplicationType.BUFF, AAEItems.NIGHT_VISION_CARD),
    FLIGHT_DRIFT("No Flight Drift", null, SettingType.NUM_INPUT, 10, ApplicationType.BUFF, AAEItems.FLIGHT_DRIFT_CARD),
    CHARGING(
            "ME Recharging",
            UpgradeCards::recharging,
            SettingType.NONE,
            0,
            ApplicationType.PASSIVE,
            AAEItems.RECHARGING_CARD),
    WORKBENCH("Portable Workbench", null, SettingType.NONE, 0, ApplicationType.PASSIVE_USE, AAEItems.WORKBENCH_CARD),
    PICK_CRAFT("Pick-Craft", null, SettingType.NONE, 1000, ApplicationType.PASSIVE_USE, AAEItems.PICK_CRAFT_CARD);

    public enum SettingType {
        NONE,
        NUM_INPUT,
        FILTER,
        NUM_AND_FILTER,
        BOOL_LIST
    }

    public enum ApplicationType {
        PASSIVE,
        PASSIVE_USE,
        BUFF
    }

    public enum ExtraSettings {
        NONE,
        BLACKLIST
    }

    public final String name;
    public final Ability ability;
    private final SettingType settingType;
    private final int cost;
    public final ApplicationType applicationType;
    private final LibItemDefinition<? extends QuantumUpgradeBaseItem> item;
    private final ExtraSettings extra;

    UpgradeType(
            String name,
            @Nullable Ability ability,
            SettingType settingType,
            int cost,
            ApplicationType applicationType,
            LibItemDefinition<? extends QuantumUpgradeBaseItem> item) {
        this(name, ability, settingType, cost, applicationType, item, ExtraSettings.NONE);
    }

    UpgradeType(
            String name,
            @Nullable Ability ability,
            SettingType settingType,
            int cost,
            ApplicationType applicationType,
            LibItemDefinition<? extends QuantumUpgradeBaseItem> item,
            ExtraSettings extra) {
        this.name = name;
        this.ability = ability;
        this.settingType = settingType;
        this.cost = cost;
        this.applicationType = applicationType;
        this.item = item;
        this.extra = extra;
    }

    public LibItemDefinition<? extends QuantumUpgradeBaseItem> item() {
        return this.item;
    }

    public SettingType getSettingType() {
        return this.settingType;
    }

    public int getCost() {
        return this.cost;
    }

    public ApplicationType getApplicationType() {
        return this.applicationType;
    }

    public String getTranslationKey() {
        return "gui.upgrades." + AdvancedAE.MOD_ID + "." + this.name.replaceAll("\\s+", "") + "Upgrade";
    }

    public IFormattableTextComponent getTranslatedName() {
        return new TranslationTextComponent(getTranslationKey());
    }

    public UpgradeSettings getSettings() {
        switch (this) {
            case EMPTY:
            case LAVA_IMMUNITY:
            case WATER_BREATHING:
            case AUTO_FEED:
            case AUTO_STOCK:
            case REGENERATION:
            case NIGHT_VISION:
            case CHARGING:
            case WORKBENCH:
            case PICK_CRAFT:
                return new UpgradeSettings(1);
            case WALK_SPEED:
                return new UpgradeSettings(1, AAEConfig.instance().getMaxWalkSpeed(), 0.1f);
            case SPRINT_SPEED:
                return new UpgradeSettings(1, AAEConfig.instance().getMaxSprintSpeed(), 0.1f);
            case STEP_ASSIST:
                return new UpgradeSettings(1, AAEConfig.instance().getMaxStepHeight());
            case JUMP_HEIGHT:
                return new UpgradeSettings(1, AAEConfig.instance().getMaxJumpHeight());
            case MAGNET:
                return new UpgradeSettings(3, AAEConfig.instance().getMaxMagnetRange());
            case HP_BUFFER:
                return new UpgradeSettings(AAEConfig.instance().getmaxHpBuffer());
            case FLIGHT:
                return new UpgradeSettings(1, AAEConfig.instance().getMaxFlightSpeed());
            case EVASION:
                return new UpgradeSettings(AAEConfig.instance().getEvasionChance());
            case STRENGTH:
                return new UpgradeSettings(AAEConfig.instance().getStrengthBoost());
            case ATTACK_SPEED:
                return new UpgradeSettings(AAEConfig.instance().getAttackSpeedBoost());
            case LUCK:
                return new UpgradeSettings(AAEConfig.instance().getLuckBoost());
            case REACH:
                return new UpgradeSettings(1, AAEConfig.instance().getMaxReachBoost());
            case SWIM_SPEED:
                return new UpgradeSettings(1, AAEConfig.instance().getMaxSwimSpeedBoost(), 0.1f);
            case FLIGHT_DRIFT:
                return new UpgradeSettings(0, 100, 1, 50);
            default:
                throw new IllegalStateException("Unhandled upgrade type: " + this);
        }
    }

    public IFormattableTextComponent getTooltip() {
        switch (this) {
            case EMPTY:
                return AAEText.UpgradeBaseTooltip.text();
            case WALK_SPEED:
                return AAEText.WalkSpeedTooltip.text();
            case SPRINT_SPEED:
                return AAEText.SprintSpeedTooltip.text();
            case STEP_ASSIST:
                return AAEText.StepAssistTooltip.text(AAEConfig.instance().getMaxStepHeight());
            case JUMP_HEIGHT:
                return AAEText.JumpHeightTooltip.text(AAEConfig.instance().getMaxJumpHeight());
            case LAVA_IMMUNITY:
                return AAEText.LavaImmunityTooltip.text();
            case FLIGHT:
                return AAEText.FlightTooltip.text();
            case WATER_BREATHING:
                return AAEText.WaterBreathingTooltip.text();
            case AUTO_FEED:
                return AAEText.AutoFeedTooltip.text();
            case AUTO_STOCK:
                return AAEText.AutoStockTooltip.text();
            case MAGNET:
                return AAEText.MagnetTooltip.text(AAEConfig.instance().getMaxMagnetRange());
            case HP_BUFFER:
                return AAEText.HpBufferTooltip.text(AAEConfig.instance().getmaxHpBuffer());
            case EVASION:
                return AAEText.EvasionTooltip.text(AAEConfig.instance().getEvasionChance());
            case REGENERATION:
                return AAEText.RegenerationTooltip.text();
            case STRENGTH:
                return AAEText.StrengthTooltip.text(AAEConfig.instance().getStrengthBoost());
            case ATTACK_SPEED:
                return AAEText.AttackSpeedTooltip.text(AAEConfig.instance().getAttackSpeedBoost());
            case LUCK:
                return AAEText.LuckTooltip.text(AAEConfig.instance().getLuckBoost());
            case REACH:
                return AAEText.ReachTooltip.text(AAEConfig.instance().getMaxReachBoost());
            case SWIM_SPEED:
                return AAEText.SwimSpeedTooltip.text();
            case NIGHT_VISION:
                return AAEText.NightVisionTooltip.text();
            case FLIGHT_DRIFT:
                return AAEText.FlightDriftTooltip.text();
            case CHARGING:
                return AAEText.RechargingTooltip.text();
            case WORKBENCH:
                return AAEText.PortableWorkbenchTooltip.text();
            case PICK_CRAFT:
                return AAEText.PickCraftTooltip.text();
            default:
                throw new IllegalStateException("Unhandled upgrade type: " + this);
        }
    }

    public ExtraSettings getExtraSettings() {
        return this.extra;
    }

    @FunctionalInterface
    public interface Ability {
        boolean execute(World level, PlayerEntity player, ItemStack stack);
    }
}
