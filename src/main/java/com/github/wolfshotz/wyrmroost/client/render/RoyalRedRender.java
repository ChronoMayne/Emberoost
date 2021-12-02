package com.github.wolfshotz.wyrmroost.client.render;

import com.github.wolfshotz.wyrmroost.client.model.entity.RoyalRedModelGecko;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoyalRedEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RoyalRedRender extends GeoEntityRenderer<RoyalRedEntity>
{
    public RoyalRedRender(EntityRendererManager renderManager)
    {
        super(renderManager, new RoyalRedModelGecko());
    }
}