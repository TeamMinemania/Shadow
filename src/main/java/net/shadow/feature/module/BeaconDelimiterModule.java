package net.shadow.feature.module;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.shadow.feature.base.Module;
import net.shadow.feature.base.ModuleType;
import net.shadow.feature.configuration.CustomValue;

public class BeaconDelimiterModule extends Module {

    final CustomValue<String> e1 = this.config.create("Effect", "Speed");

    public BeaconDelimiterModule() {
        super("BeaconDelimiter", "effects on beacons", ModuleType.EXPLOIT);
    }

    private static StatusEffect getEffectFromName(String partial) {
        return switch (partial.toUpperCase()) {
            case "ABSORPTION" -> StatusEffects.ABSORPTION;
            case "BAD OMEN" -> StatusEffects.BAD_OMEN;
            case "BLINDNESS" -> StatusEffects.BLINDNESS;
            case "CONDUIT POWER" -> StatusEffects.CONDUIT_POWER;
            case "DOLPHINS GRACE" -> StatusEffects.DOLPHINS_GRACE;
            case "FIRE RESISTANCE" -> StatusEffects.FIRE_RESISTANCE;
            case "GLOWING" -> StatusEffects.GLOWING;
            case "HASTE" -> StatusEffects.HASTE;
            case "HEALTH BOOST" -> StatusEffects.HEALTH_BOOST;
            case "HERO OF THE VILLAGE" -> StatusEffects.HERO_OF_THE_VILLAGE;
            case "HUNGER" -> StatusEffects.HUNGER;
            case "INSTANT DAMAGE" -> StatusEffects.INSTANT_DAMAGE;
            case "INSTANT HEALTH" -> StatusEffects.INSTANT_HEALTH;
            case "INVISIBILITY" -> StatusEffects.INVISIBILITY;
            case "JUMP BOOST" -> StatusEffects.JUMP_BOOST;
            case "LEVITATION" -> StatusEffects.LEVITATION;
            case "LUCK" -> StatusEffects.LUCK;
            case "MINING FATIGUE" -> StatusEffects.MINING_FATIGUE;
            case "NAUSEA" -> StatusEffects.NAUSEA;
            case "NIGHT VISION" -> StatusEffects.NIGHT_VISION;
            case "POISON" -> StatusEffects.POISON;
            case "REGENERATION" -> StatusEffects.REGENERATION;
            case "RESISTANCE" -> StatusEffects.RESISTANCE;
            case "SATURATION" -> StatusEffects.SATURATION;
            case "SLOWNESS" -> StatusEffects.SLOWNESS;
            case "SLOW FALLING" -> StatusEffects.SLOW_FALLING;
            case "SPEED" -> StatusEffects.SPEED;
            case "STRENGTH" -> StatusEffects.STRENGTH;
            case "UNLUCK" -> StatusEffects.UNLUCK;
            case "WATER BREATHING" -> StatusEffects.WATER_BREATHING;
            case "WEAKNESS" -> StatusEffects.WEAKNESS;
            case "WITHER" -> StatusEffects.WITHER;
            default -> null;
        };
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onRender() {

    }

    @Override
    public String getSpecial() {
        StatusEffect i1 = getEffectFromName(e1.getThis());
        int ii1 = StatusEffect.getRawId(i1);
        return ii1 + "";
    }
}
