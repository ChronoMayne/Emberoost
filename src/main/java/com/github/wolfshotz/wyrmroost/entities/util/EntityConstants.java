package com.github.wolfshotz.wyrmroost.entities.util;

import com.github.wolfshotz.wyrmroost.client.model.entity.*;
import com.github.wolfshotz.wyrmroost.entities.dragon.AlpineEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.drake.overworld.OverworldDrake;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.leviathan.butterfly.ButterflyLeviathan;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyvern.canari.CanariWyvern;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import com.github.wolfshotz.wyrmroost.util.animation.LogicalAnimation;

import java.util.Optional;

public class EntityConstants {

    public static final EntitySerializer<TameableDragonEntity> TAMEABLE_DRAGON_SERIALIZER = EntitySerializer.builder(b -> b
            .track(EntitySerializer.POS.optional(), "HomePos", t -> Optional.ofNullable(t.getHomePos()), (d, v) -> d.setHomePos(v.orElse(null)))
            .track(EntitySerializer.INT, "BreedCount", TameableDragonEntity::getBreedCount, TameableDragonEntity::setBreedCount));

    public static final byte HEAL_PARTICLES_EVENT_ID = 8;
    public static final int AGE_UPDATE_INTERVAL = 200;

    public static final String DATA_VARIANT = "Variant";
    public static final String DATA_BURROWED = "Burrowed";

    public static final int OVERWORLD_DRAKE_SADDLE_SLOT = 0;
    public static final int OVERWORLD_DRAKE_ARMOR_SLOT = 1;
    public static final int OVERWORLD_DRAKE_CHEST_SLOT = 2;

    public static final int ROYAL_RED_DRAGON_ARMOR_SLOT = 0;
    public static final int ROYAL_RED_DRAGON_MAX_KNOCKOUT_TIME = 3600; // 3 minutes

    public static final int BUTTERFLY_LEVIATHAN_CONDUIT_SLOT = 0;

    public static final Animation ALPINE_ROAR_ANIMATION = LogicalAnimation.create(84, AlpineEntity::roarAnimation, () -> AlpineModel::roarAnimation);
    public static final Animation ALPINE_WIND_GUST_ANIMATION = LogicalAnimation.create(25, AlpineEntity::windGustAnimation, () -> AlpineModel::windGustAnimation);
    public static final Animation ALPINE_BITE_ANIMATION = LogicalAnimation.create(10, null, () -> AlpineModel::biteAnimation);
    public static final Animation[] ALPINE_ANIMATIONS = new Animation[]{ALPINE_ROAR_ANIMATION, ALPINE_WIND_GUST_ANIMATION, ALPINE_BITE_ANIMATION};

    public static final Animation OVERWORLD_DRAKE_GRAZE_ANIMATION = LogicalAnimation.create(35, OverworldDrake::grazeAnimation, () -> OverworldDrakeModel::grazeAnimation);
    public static final Animation OVERWORLD_DRAKE_HORN_ATTACK_ANIMATION = LogicalAnimation.create(15, OverworldDrake::hornAttackAnimation, () -> OverworldDrakeModel::hornAttackAnimation);
    public static final Animation OVERWORLD_DRAKE_ROAR_ANIMATION = LogicalAnimation.create(86, OverworldDrake::hornAttackAnimation, () -> OverworldDrakeModel::roarAnimation);
    public static final Animation[] OVERWORLD_DRAKE_ANIMATIONS = new Animation[]{OVERWORLD_DRAKE_GRAZE_ANIMATION, OVERWORLD_DRAKE_HORN_ATTACK_ANIMATION, OVERWORLD_DRAKE_ROAR_ANIMATION};

    public static final Animation CANARI_WYVERN_FLAP_WINGS_ANIMATION = LogicalAnimation.create(22, CanariWyvern::flapWingsAnimation, () -> CanariWyvernModel::flapWingsAnimation);
    public static final Animation CANARI_WYVERN_PREEN_ANIMATION = LogicalAnimation.create(36, null, () -> CanariWyvernModel::preenAnimation);
    public static final Animation CANARI_WYVERN_THREAT_ANIMATION = LogicalAnimation.create(40, CanariWyvern::threatAnimation, () -> CanariWyvernModel::threatAnimation);
    public static final Animation CANARI_WYVERN_ATTACK_ANIMATION = LogicalAnimation.create(15, null, () -> CanariWyvernModel::attackAnimation);

    public static final Animation BUTTERFLY_LEVIATHAN_LIGHTNING_ANIMATION = LogicalAnimation.create(64, ButterflyLeviathan::lightningAnimation, () -> ButterflyLeviathanModel::roarAnimation);
    public static final Animation BUTTERFLY_LEVIATHAN_CONDUIT_ANIMATION = LogicalAnimation.create(59, ButterflyLeviathan::conduitAnimation, () -> ButterflyLeviathanModel::conduitAnimation);
    public static final Animation BUTTERFLY_LEVIATHAN_BITE_ANIMATION = LogicalAnimation.create(17, ButterflyLeviathan::biteAnimation, () -> ButterflyLeviathanModel::biteAnimation);
    public static final Animation[] BUTTERFLY_LEVIATHAN_ANIMATIONS = new Animation[]{BUTTERFLY_LEVIATHAN_LIGHTNING_ANIMATION, BUTTERFLY_LEVIATHAN_CONDUIT_ANIMATION, BUTTERFLY_LEVIATHAN_BITE_ANIMATION};

    public static final Animation LESSER_DESERT_WYRM_BITE_ANIMATION = LogicalAnimation.create(10, null, () -> LesserDesertwyrmModel::biteAnimation);

}
