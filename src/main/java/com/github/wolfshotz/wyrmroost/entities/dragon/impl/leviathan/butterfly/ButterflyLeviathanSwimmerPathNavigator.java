package com.github.wolfshotz.wyrmroost.entities.dragon.impl.leviathan.butterfly;

import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.util.math.BlockPos;

public class ButterflyLeviathanSwimmerPathNavigator extends SwimmerPathNavigator {

    private ButterflyLeviathan leviathan;

    public ButterflyLeviathanSwimmerPathNavigator(ButterflyLeviathan leviathan) {
        super(leviathan, leviathan.level);
        this.leviathan = leviathan;
    }

    @Override
    protected PathFinder createPathFinder(int range) {
        return new PathFinder(nodeEvaluator = new WalkAndSwimNodeProcessor(), range);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return !level.getBlockState(pos.below()).isAir(level, pos.below());
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }
}