package com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred;

import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class RoyalRedDragonAttacks {

    private RoyalRedDragon dragon;

    public RoyalRedDragonAttacks(RoyalRedDragon dragon) {
        this.dragon = dragon;
    }

    /**
     * Bite/slap Animation Trigger
     */
    public void meleeAttack() { //TODO: find animations
//        if (!dragon.level.isClientSide)
//            AnimationPacket.send(this, dragon.isFlying() || dragon.getRandom().nextBoolean()? BITE_ATTACK_ANIMATION : SLAP_ATTACK_ANIMATION);
    }

    //Play sound for roar during animation
    public void tickRoarAnimation(int time) {
        if (time == 0) dragon.playSound(WRSounds.ENTITY_ROYALRED_ROAR.get(), 3, 1, true);
        ((LessShitLookController) dragon.getLookControl()).stopLooking();
        for (LivingEntity entity : dragon.getEntitiesNearby(10, dragon::isAlliedTo))
            entity.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 60));
    }

    public void tickSlapAnimation(int time) {
        if (time == 7) dragon.playSound(WRSounds.ENTITY_ROYALRED_HURT.get(), 1, 1, true);
        else if (time != 12) return;

        dragon.attackInBox(dragon.getOffsetBox(dragon.getBbWidth()).inflate(0.2), 50);
        dragon.yRot = dragon.yHeadRot;
    }

    public void tickBiteAnimation(int time) {
        if (time == 4) {
            dragon.attackInBox(dragon.getOffsetBox(dragon.getBbWidth()).inflate(-0.3), 100);
            dragon.playSound(WRSounds.ENTITY_ROYALRED_HURT.get(), 2, 1, true);
        }
    }

}
