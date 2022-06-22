package com.github.wolfshotz.wyrmroost.entities.dragon.impl.glider.silver.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.impl.glider.silver.SilverGlider;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

import java.util.EnumSet;

public class SilverGliderSwoopGoal extends Goal {
    private BlockPos pos;

    private SilverGlider glider;

    public SilverGliderSwoopGoal(SilverGlider silverGlider) {
        this.glider = silverGlider;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!glider.isFlying()) return false;
        if (glider.isRiding()) return false;
        if (glider.getRandom().nextDouble() > 0.001) return false;
        if (glider.level.getFluidState(this.pos = glider.level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, glider.blockPosition()).below()).isEmpty())
            return false;
        return glider.getY() - pos.getY() > 8;
    }

    @Override
    public boolean canContinueToUse() {
        return glider.blockPosition().distSqr(pos) > 8;
    }

    @Override
    public void tick() {
        if (glider.getNavigation().isDone()) glider.getNavigation().moveTo(pos.getX(), pos.getY() + 2, pos.getZ(), 1);
        glider.getLookControl().setLookAt(pos.getX(), pos.getY() + 2, pos.getZ());
    }
}