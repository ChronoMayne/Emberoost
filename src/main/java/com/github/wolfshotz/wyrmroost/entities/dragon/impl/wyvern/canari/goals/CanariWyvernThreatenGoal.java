package com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyvern.canari.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyvern.canari.CanariWyvern;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class CanariWyvernThreatenGoal extends Goal {

    private PlayerEntity target;
    private CanariWyvern wyvern;

    public CanariWyvernThreatenGoal(CanariWyvern wyvern) {
        this.wyvern = wyvern;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (wyvern.isTame()) return false;
        if (wyvern.isFlying()) return false;
        if (wyvern.getTarget() != null) return false;
        if ((target = wyvern.level.getNearestPlayer(wyvern.getX(), wyvern.getY(), wyvern.getZ(), 12d, true)) == null)
            return false;
        return wyvern.canAttack(target);
    }

    @Override
    public void tick() {
        double distFromTarget = wyvern.distanceToSqr(target);
        if (distFromTarget > 30 && !wyvern.isPissed()) {
            if (wyvern.getNavigation().isDone()) {
                Vector3d vec3d = RandomPositionGenerator.getPosAvoid(wyvern, 16, 7, target.position());
                if (vec3d != null) wyvern.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1.5);
            }
        } else {
            wyvern.getLookControl().setLookAt(target, 90, 90);
            if (!wyvern.isPissed()) {
                wyvern.pissedOffTarget = target;
                AnimationPacket.send(wyvern, CanariWyvern.THREAT_ANIMATION);
                wyvern.clearAI();
            }

            if (distFromTarget < 6) wyvern.setTarget(target);
        }
    }

    @Override
    public void stop() {
        target = null;
        wyvern.pissedOffTarget = null;
    }
}
