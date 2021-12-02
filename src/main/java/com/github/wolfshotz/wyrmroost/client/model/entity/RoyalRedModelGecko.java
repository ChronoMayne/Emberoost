package com.github.wolfshotz.wyrmroost.client.model.entity;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoyalRedEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RoyalRedModelGecko extends AnimatedGeoModel<RoyalRedEntity>
{
    @Override
    public ResourceLocation getModelLocation(RoyalRedEntity object)
    {
        return new ResourceLocation(Wyrmroost.MOD_ID, "geo/royalredgeo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(RoyalRedEntity object)
    {
        return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/royalred_red.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(RoyalRedEntity object)
    {
        return new ResourceLocation(Wyrmroost.MOD_ID, "animations/royalred.animation.json");
    }

}