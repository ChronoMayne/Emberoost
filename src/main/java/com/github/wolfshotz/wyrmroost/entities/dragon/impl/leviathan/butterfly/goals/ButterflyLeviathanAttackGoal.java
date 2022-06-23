package com.github.wolfshotz.wyrmroost.entities.dragon.impl.leviathan.butterfly.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.impl.leviathan.butterfly.ButterflyLeviathan;
import com.github.wolfshotz.wyrmroost.entities.util.EntityConstants;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class ButterflyLeviathanAttackGoal extends Goal {

    private ButterflyLeviathan leviathan;

    public ButterflyLeviathanAttackGoal(ButterflyLeviathan leviathan) {
        this.leviathan = leviathan;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return !leviathan.canBeControlledByRider() && leviathan.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity target = leviathan.getTarget();
        if (target == null) return;
        double distFromTarget = leviathan.distanceToSqr(target);

        leviathan.getLookControl().setLookAt(target, leviathan.getMaxHeadYRot(), leviathan.getMaxHeadXRot());

        boolean isClose = distFromTarget < 40;

        if (leviathan.getNavigation().isDone())
            leviathan.getNavigation().moveTo(target, 1.2);

        if (isClose) leviathan.yRot = (float) Mafs.getAngle(leviathan, target) + 90f;

        if (leviathan.noAnimations()) {
            if (distFromTarget > 225 && (leviathan.isTame() || target.getType() == EntityType.PLAYER) && leviathan.canZap())
                AnimationPacket.send(leviathan, EntityConstants.BUTTERFLY_LEVIATHAN_LIGHTNING_ANIMATION);
            else if (isClose && MathHelper.degreesDifferenceAbs((float) Mafs.getAngle(leviathan, target) + 90, leviathan.yRot) < 30)
                AnimationPacket.send(leviathan, EntityConstants.BUTTERFLY_LEVIATHAN_BITE_ANIMATION);
        }
    }
}