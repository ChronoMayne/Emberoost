package com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred.RoyalRedDragon;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class RedRoyalDragonAttackGoal extends Goal {

    private RoyalRedDragon dragon;

    public RedRoyalDragonAttackGoal(RoyalRedDragon dragon) {
        this.dragon = dragon;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = dragon.getTarget();
        if (target != null && target.isAlive()) {
            if (!dragon.isWithinRestriction(target.blockPosition())) return false;
            return EntityPredicates.ATTACK_ALLOWED.test(target);
        }
        return false;
    }

    @Override
    public void tick() {
        LivingEntity target = dragon.getTarget();
        double distFromTarget = dragon.distanceToSqr(target);
        double degrees = Math.atan2(target.getZ() - dragon.getZ(), target.getX() - dragon.getX()) * (180 / Math.PI) - 90;
        boolean isBreathingFire = dragon.isBreathingFire();
        boolean canSeeTarget = dragon.getSensing().canSee(target);

        dragon.getLookControl().setLookAt(target, 90, 90);

        double headAngle = Math.abs(MathHelper.wrapDegrees(degrees - dragon.yHeadRot));
        boolean shouldBreatheFire = !dragon.isAtHome() && (distFromTarget > 100 || target.getY() - dragon.getY() > 3 || dragon.isFlying()) && headAngle < 30 && dragon.canBreatheFire();
        if (isBreathingFire != shouldBreatheFire) dragon.setBreathingFire(isBreathingFire = shouldBreatheFire);

        if (dragon.getRandom().nextDouble() < 0.001 || distFromTarget > 900) dragon.setFlying(true);
        else if (distFromTarget <= 24 && dragon.noAnimations() && !isBreathingFire && canSeeTarget) {
            dragon.yBodyRot = dragon.yRot = (float) Mafs.getAngle(dragon, target) + 90;
            dragon.getAttacks().meleeAttack();
        }

        if (dragon.getNavigation().isDone() || dragon.getAge() % 10 == 0) {
            boolean isFlyingTarget = target instanceof TameableDragonEntity && ((TameableDragonEntity) target).isFlying();
            double y = target.getY() + (!isFlyingTarget && dragon.getRandom().nextDouble() > 0.1 ? 8 : 0);
            dragon.getNavigation().moveTo(target.getX(), y, target.getZ(), !dragon.isFlying() && isBreathingFire ? 0.8d : 1.3d);
        }
    }
}