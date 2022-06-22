package com.github.wolfshotz.wyrmroost.entities.dragon.impl.drake.dragonfruit.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.impl.drake.dragonfruit.DragonFruitDrake;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.EnumSet;

// todo: completely remake this so it instead looks for random block in range instead of closest,and checks to see if block is in range rather than being directly ontop of it
public class DragonFruitDrakeMoveToCropsGoal extends MoveToBlockGoal {

    private DragonFruitDrake drake;

    public DragonFruitDrakeMoveToCropsGoal(DragonFruitDrake drake) {
        super(drake, 1, DragonFruitDrake.CROP_GROWTH_RADIUS * 2);
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        this.drake = drake;
    }

    @Override
    public boolean canUse() {
        return drake.getGrowCropsTime() >= 0 && findNearestBlock();
    }

    @Override
    protected int nextStartTick(CreatureEntity creature) {
        return 100;
    }

    @Override
    public boolean canContinueToUse() {
        return drake.getGrowCropsTime() >= 0;
    }

    @Override
    public void tick() {
        super.tick();
        drake.getLookControl().setLookAt(blockPos.getX(), blockPos.getY(), blockPos.getY());
        if (tryTicks >= 200 && drake.getRandom().nextInt(tryTicks) >= 100) {
            tryTicks = 0;
            findNearestBlock();
        }
    }

    @Override
    protected boolean isValidTarget(IWorldReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        return !pos.equals(blockPos) && DragonFruitDrake.isCrop(block) && ((IGrowable) block).isValidBonemealTarget(level, pos, state, false);
    }
}
