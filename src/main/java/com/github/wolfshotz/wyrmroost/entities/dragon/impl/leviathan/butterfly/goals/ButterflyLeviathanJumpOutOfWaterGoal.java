package com.github.wolfshotz.wyrmroost.entities.dragon.impl.leviathan.butterfly.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.impl.leviathan.butterfly.ButterflyLeviathan;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

import java.util.EnumSet;

public class ButterflyLeviathanJumpOutOfWaterGoal extends Goal {
    private BlockPos pos;

    private ButterflyLeviathan leviathan;

    public ButterflyLeviathanJumpOutOfWaterGoal(ButterflyLeviathan leviathan) {
        this.leviathan = leviathan;
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (leviathan.isInSittingPose()) return false;
        if (leviathan.canBeControlledByRider()) return false;
        if (!leviathan.isUnderWater()) return false;
        if (leviathan.level.getFluidState(this.pos = leviathan.level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, leviathan.blockPosition()).below()).isEmpty())
            return false;
        if (pos.getY() <= 0) return false;
        return leviathan.getRandom().nextDouble() < 0.001;
    }

    @Override
    public boolean canContinueToUse() {
        return !leviathan.canBeControlledByRider() && leviathan.isUnderWater();
    }

    @Override
    public void start() {
        leviathan.getNavigation().stop();
        this.pos = pos.relative(leviathan.getDirection(), (int) ((pos.getY() - leviathan.getY()) * 0.5d));
    }

    @Override
    public void tick() {
        leviathan.getMoveControl().setWantedPosition(pos.getX(), pos.getY(), pos.getZ(), 1.2d);
    }

    @Override
    public void stop() {
        pos = null;
        leviathan.clearAI();
    }
}
