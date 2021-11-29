package com.github.wolfshotz.wyrmroost.client.model.entity;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.TetraWyvernEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;

public class TetraWyvernModel extends DragonEntityModel<TetraWyvernEntity>
{

    @Override
    public ResourceLocation getModelLocation(TetraWyvernEntity object)
    {
        //return new ResourceLocation(Wyrmroost.MOD_ID, "/tetrawyvern.json");



            ResourceLocation getTextureLocation;
            if(object.getVariant() == 1){
                return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/tetrawyvern/tetrawyvern_cyan.png");
            }
            else if (object.getVariant() == 2){
                return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/tetrawyvern/tetrawyvern_pearl.png");
            }
            else if (object.getVariant() == 3) {
                return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/tetrawyvern/tetrawyvern_midnight.png");
            }
            else {
                return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/tetrawyvern/tetrawyvern_lime.png");
            }

    }

    @Override
    public ResourceLocation getTexture(TetraWyvernEntity entity) {
        return null;
    }

    @Override
    public float getShadowRadius(TetraWyvernEntity entity) {
        return 0;
    }

    @Override
    public void setupAnim(TetraWyvernEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {

    }

    @Override
    public void renderToBuffer(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {

    }
}