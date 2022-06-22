package com.github.wolfshotz.wyrmroost.entities.util;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.drake.dragonfruit.DragonFruitDrake;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred.RoyalRedDragon;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The types of entity serializer entries.
 *
 * @author Gabriel
 */
public enum EntitySerializerType {

    SLEEPING("Sleeping", EntitySerializer.BOOL, TameableDragonEntity::isSleeping, TameableDragonEntity::setSleeping),

    VARIANT("Variant", EntitySerializer.INT, TameableDragonEntity::getVariant, TameableDragonEntity::setVariant),

    GENDER("Gender", EntitySerializer.BOOL, TameableDragonEntity::isMale, TameableDragonEntity::setGender),

    SHEAR_TIMER("ShearTimer", EntitySerializer.INT, DragonFruitDrake::getShearedCooldown, DragonFruitDrake::setShearedCooldown),

    KNOCK_OUT_TIME("KnockOutTime", EntitySerializer.INT, RoyalRedDragon::getKnockOutTime, RoyalRedDragon::setKnockoutTime);

    private EntitySerializer.Entry entry;

    <T extends TameableDragonEntity, E> EntitySerializerType(String key, EntitySerializer.NBTBridge bridge, Function<T, E> write, BiConsumer<T, E> read) {
        this.entry = new EntitySerializer.Entry<E, T>(bridge, key, write, read);
    }

    private static HashMap<EntitySerializerType, EntitySerializer.Entry> entries = buildMap();

    private static HashMap<EntitySerializerType, EntitySerializer.Entry> buildMap() {
        HashMap<EntitySerializerType, EntitySerializer.Entry> serializers = new HashMap<>();

        for (EntitySerializerType value : values()) {
            serializers.put(value, value.entry);
        }

        return serializers;
    }

    public static EntitySerializer.Entry getEntry(EntitySerializerType type) {
        return entries.get(type);
    }
}
