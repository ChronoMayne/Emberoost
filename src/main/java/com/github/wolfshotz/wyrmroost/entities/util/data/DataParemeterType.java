package com.github.wolfshotz.wyrmroost.entities.util.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.CoinDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.drake.overworld.OverworldDrake;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.leviathan.butterfly.ButterflyLeviathan;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred.RoyalRedDragon;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.stalker.roost.RoostStalker;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyrm.desert.LesserDesertwyrm;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;

import java.util.HashMap;

public enum DataParemeterType {

    GENDER("gender", DataSerializers.BOOLEAN),
    FLYING("flying", DataSerializers.BOOLEAN),
    SLEEPING("sleeping", DataSerializers.BOOLEAN),
    VARIANT("variant", DataSerializers.INT), // todo in 1.17: make this use strings for nbt based textures
    COIN_VARIANT("coin_variant", CoinDragonEntity.class, DataSerializers.INT), // todo in 1.17: make this use strings for nbt based textures
    ARMOR("armor", DataSerializers.ITEM_STACK),
    HOME_POS("homePos", DataSerializers.BLOCK_POS),
    AGE("age", DataSerializers.INT),

    OVERWORLD_DRAKE_SADDLED("saddled", OverworldDrake.class, DataSerializers.BOOLEAN),
    BUTTERFLY_LEVIATHAN_HAS_CONDUIT("hasConduit", ButterflyLeviathan.class, DataSerializers.BOOLEAN),

    ROYAL_RED_DRAGON_BREATHING_FIRE("breathingFire", RoyalRedDragon.class, DataSerializers.BOOLEAN),
    ROYAL_RED_DRAGON_KNOCKED_OUT("knockedOut", RoyalRedDragon.class, DataSerializers.BOOLEAN),
    ROYAL_RED_DRAGON_SLAP("slap", RoyalRedDragon.class, DataSerializers.BOOLEAN),

    ROOST_STALKER_ITEM("item", RoostStalker.class, DataSerializers.ITEM_STACK),
    ROOST_STALKER_SCAVENGING("scavenging", RoostStalker.class, DataSerializers.BOOLEAN),

    LESSER_DESERT_WYRM_BURROWED("burrowed", LesserDesertwyrm.class, DataSerializers.BOOLEAN),

    ;

    private String key;

    private Class classType;

    private IDataSerializer serializer;

    DataParemeterType(String key, Class classType, IDataSerializer serializer) {
        this.key = key;
        this.classType = classType;
        this.serializer = serializer;
    }

    DataParemeterType(String key, IDataSerializer serializer) {
        this.key = key;
        this.serializer = serializer;
        this.classType = TameableDragonEntity.class;
    }

    private static final HashMap<DataParemeterType, Object> parameterCache = new HashMap<>();

    private  <T> DataParameter<T> buildAndCacheParemeter() {
        DataParameter<T> parameter = EntityDataManager.defineId(classType, serializer);
        parameterCache.put(this, parameter);
        return parameter;
    }

    public static <T> DataParameter<T> get(DataParemeterType type) {
        return (DataParameter<T>) parameterCache.getOrDefault(type, type.buildAndCacheParemeter());
    }
}
