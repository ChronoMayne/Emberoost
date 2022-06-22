package com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyrm.desert.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyrm.desert.LesserDesertwyrm;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.BlockTags;

import java.util.EnumSet;

public class LesserDesertWyrmBurrowGoal extends Goal {
    private int burrowTicks = 30;

    private LesserDesertwyrm wyrm;

    public LesserDesertWyrmBurrowGoal(LesserDesertwyrm wyrm) {
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        this.wyrm = wyrm;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean canUse() {
        return !wyrm.isBurrowed() && belowIsSand();
    }

    @Override
    public boolean canContinueToUse() {
        return belowIsSand() && (wyrm.isBurrowed() || burrowTicks > 0);
    }

    @Override
    public void stop() {
        burrowTicks = 30;
        wyrm.setBurrowed(false);
    }

    @Override
    public void tick() {
        if (burrowTicks > 0 && --burrowTicks == 0) wyrm.setBurrowed(true);
    }

    private boolean belowIsSand() {
        return wyrm.level.getBlockState(wyrm.blockPosition().below(1)).is(BlockTags.SAND);
    }
}
