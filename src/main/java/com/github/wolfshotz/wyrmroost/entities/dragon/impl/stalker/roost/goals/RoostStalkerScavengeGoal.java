package com.github.wolfshotz.wyrmroost.entities.dragon.impl.stalker.roost.goals;

import com.github.wolfshotz.wyrmroost.entities.dragon.impl.stalker.roost.RoostStalker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class RoostStalkerScavengeGoal extends MoveToBlockGoal {
    private IInventory chest;
    private int searchDelay;
    private RoostStalker stalker;


    public RoostStalkerScavengeGoal(RoostStalker stalker, double speed) {
        super(stalker, speed, 16);
        this.stalker = stalker;
        this.searchDelay = 20 + stalker.getRandom().nextInt(40) + 5;
    }

    @Override
    public boolean canUse() {
        boolean flag = !stalker.isTame() && !stalker.hasItem() && super.canUse();
        if (flag) return (chest = getInventoryAtPosition()) != null && !chest.isEmpty();
        else return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !stalker.hasItem() && chest != null && super.canContinueToUse();
    }

    @Override
    public void tick() {
        super.tick();

        if (isReachedTarget()) {
            if (stalker.hasItem()) return;

            stalker.setScavenging(true);

            if (chest == null) return;
            if (chest instanceof ChestTileEntity && ((ChestTileEntity) chest).openCount == 0)
                interactChest(chest, true);
            if (!chest.isEmpty() && --searchDelay <= 0) {
                int index = stalker.getRandom().nextInt(chest.getContainerSize());
                ItemStack stack = chest.getItem(index);

                if (!stack.isEmpty()) {
                    stack = chest.removeItemNoUpdate(index);
                    stalker.getInventory().insertItem(stalker.getDefaultItemSlot(), stack, false);
                }
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        interactChest(chest, false);
        searchDelay = 20 + stalker.getRandom().nextInt(40) + 5;
        stalker.setScavenging(false);
    }

    /**
     * Returns the IInventory (if applicable) of the TileEntity at the specified position
     */
    @Nullable
    public IInventory getInventoryAtPosition() {
        IInventory inv = null;
        BlockState blockstate = stalker.level.getBlockState(blockPos);
        Block block = blockstate.getBlock();
        if (blockstate.hasTileEntity()) {
            TileEntity tileentity = stalker.level.getBlockEntity(blockPos);
            if (tileentity instanceof IInventory) {
                inv = (IInventory) tileentity;
                if (inv instanceof ChestTileEntity && block instanceof ChestBlock)
                    inv = ChestBlock.getContainer((ChestBlock) block, blockstate, stalker.level, blockPos, true);
            }
        }

        return inv;
    }

    /**
     * Return true to set given position as destination
     */
    @Override
    protected boolean isValidTarget(IWorldReader world, BlockPos pos) {
        return stalker.level.getBlockEntity(pos) instanceof IInventory;
    }

    /**
     * Used to handle the chest opening animation when being used by the scavenger
     */
    private void interactChest(IInventory intentory, boolean open) {
        if (!(intentory instanceof ChestTileEntity)) return; // not a chest, ignore it
        ChestTileEntity chest = (ChestTileEntity) intentory;

        chest.openCount = open ? 1 : 0;
        chest.getLevel().blockEvent(chest.getBlockPos(), chest.getBlockState().getBlock(), 1, chest.openCount);
    }
}
