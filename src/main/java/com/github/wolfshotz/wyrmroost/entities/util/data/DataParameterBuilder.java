package com.github.wolfshotz.wyrmroost.entities.util.data;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;

import java.util.HashMap;

public class DataParameterBuilder {

    private static final HashMap<Class, HashMap<IDataSerializer, DataParameter>> parameterCache = new HashMap<>();

    public static <T> DataParameter<T> getDataParameter(Class classType, IDataSerializer<T> serializer) {

        HashMap<IDataSerializer, DataParameter> parameters = parameterCache.getOrDefault(classType, new HashMap<>());
        DataParameter parameter = parameters.getOrDefault(serializer, EntityDataManager.defineId(classType, serializer));

        parameters.put(serializer, parameter);
        parameterCache.put(classType, parameters);

        return parameters.remove(parameter);
    }


}
