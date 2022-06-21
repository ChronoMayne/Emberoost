package com.github.wolfshotz.wyrmroost.client.model.entity;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred.RoyalRedDragon;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class RoyalRedModelGecko extends AnimatedGeoModel<RoyalRedDragon> {
    @Override
    public ResourceLocation getModelLocation(RoyalRedDragon object) {
        return new ResourceLocation(Wyrmroost.MOD_ID, "geo/royalredgeo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(RoyalRedDragon entity) {

        if (entity.getVariant() == -1 && entity.isMale()) {
            return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/male_spe.png");

        } else if (entity.getVariant() == -1 && !entity.isMale()) {
            return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/female_spe.png");

        } else if (entity.getVariant() != -1 && entity.isMale()) {
            return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/male.png");

        } else if (entity.getVariant() != -1 && !entity.isMale()) {
            return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/female.png");
        } else
            return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/female.png");
        }


        @Override
        public ResourceLocation getAnimationFileLocation (RoyalRedDragon object)
        {
            return new ResourceLocation(Wyrmroost.MOD_ID, "animations/royalred.animation.json");
        }

    }
