package com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyvern.canari.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyvern.canari.CanariWyvern;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.EntityPredicates;

import java.util.EnumSet;

public class CanariWyvernAttackGoal extends Goal {
    private int repathTimer = 10;
    private int attackDelay = 0;

    private CanariWyvern wyvern;

    public CanariWyvernAttackGoal(CanariWyvern wyvern) {
        this.wyvern = wyvern;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = wyvern.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = wyvern.getTarget();
        return target != null && target.isAlive() && wyvern.isWithinRestriction(target.blockPosition()) && EntityPredicates.ATTACK_ALLOWED.test(target);
    }

    @Override
    public void tick() {
        LivingEntity target = wyvern.getTarget();

        if ((++repathTimer >= 10 || wyvern.getNavigation().isDone()) && wyvern.getSensing().canSee(target)) {
            repathTimer = 0;
            if (!wyvern.isFlying()) wyvern.setFlying(true);
            wyvern.getNavigation().moveTo(target.getX(), target.getBoundingBox().maxY - 2, target.getZ(), 1);
            wyvern.getLookControl().setLookAt(target, 90, 90);
        }

        if (--attackDelay <= 0 && wyvern.distanceToSqr(target.position().add(0, target.getBoundingBox().getYsize(), 0)) <= 2.25 + target.getBbWidth()) {
            attackDelay = 20 + wyvern.getRandom().nextInt(10);
            AnimationPacket.send(wyvern, CanariWyvern.ATTACK_ANIMATION);
            wyvern.doHurtTarget(target);
        }
    }

    @Override
    public void stop() {
        repathTimer = 10;
        attackDelay = 0;
    }
}
