package com.github.wolfshotz.wyrmroost.entities.util;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class EntityConstants {

    public static final EntitySerializer<TameableDragonEntity> TAMEABLE_DRAGON_SERIALIZER = EntitySerializer.builder(b -> b
            .track(EntitySerializer.POS.optional(), "HomePos", t -> Optional.ofNullable(t.getHomePos()), (d, v) -> d.setHomePos(v.orElse(null)))
            .track(EntitySerializer.INT, "BreedCount", TameableDragonEntity::getBreedCount, TameableDragonEntity::setBreedCount));

    public static final byte HEAL_PARTICLES_EVENT_ID = 8;
    public static final int AGE_UPDATE_INTERVAL = 200;

    // Common Data Parameters
    public static final DataParameter<Boolean> GENDER = EntityDataManager.defineId(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> FLYING = EntityDataManager.defineId(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> SLEEPING = EntityDataManager.defineId(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Integer> VARIANT = EntityDataManager.defineId(TameableDragonEntity.class, DataSerializers.INT); // todo in 1.17: make this use strings for nbt based textures
    public static final DataParameter<ItemStack> ARMOR = EntityDataManager.defineId(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    public static final DataParameter<BlockPos> HOME_POS = EntityDataManager.defineId(TameableDragonEntity.class, DataSerializers.BLOCK_POS);
    public static final DataParameter<Integer> AGE = EntityDataManager.defineId(TameableDragonEntity.class, DataSerializers.INT);

}
