package com.github.wolfshotz.wyrmroost.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;

/**
 * A basic tameable entity for wyrmroost's implementations.
 */
public abstract class BasicTameableEntity extends TameableEntity {
    public BasicTameableEntity(EntityType<? extends TameableEntity> p_i48574_1_, World p_i48574_2_) {
        super(p_i48574_1_, p_i48574_2_);
    }
}

