package com.github.wolfshotz.wyrmroost.entities.util;

import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;

import java.util.HashMap;

/**
 * Builds and caches the entity serializer for each entity.
 *
 * @author Gabriel
 */
public class EntitySerializerBuilder {

    /**
     * The serializers that were built for each entity.
     */
    private static HashMap<Class, EntitySerializer> serializerCache = new HashMap<>();

    private EntitySerializerBuilder() {
    }

    /**
     * Returns the entity serializer based on the entity and entries.
     *
     * @param key
     * @param entries
     * @param <T>
     * @return the serializer
     */
    public static <T extends TameableDragonEntity> EntitySerializer<T> getEntitySerializer(Class key, EntitySerializerType... entries) {
        EntitySerializer<T> serializer = serializerCache.getOrDefault(key, createNewSerializer(entries));
        serializerCache.put(key, serializer);
        return serializer;
    }

    /**
     * Builds a new serializer based on the entries.
     *
     * @param entries
     * @param <T>
     * @return the serializer
     */
    private static <T extends TameableDragonEntity> EntitySerializer<T> createNewSerializer(EntitySerializerType... entries) {
        return EntityConstants.TAMEABLE_DRAGON_SERIALIZER.concat(b -> {
            for (EntitySerializerType entry : entries) {
                b.track(EntitySerializerType.getEntry(entry));
            }
        });
    }
}
