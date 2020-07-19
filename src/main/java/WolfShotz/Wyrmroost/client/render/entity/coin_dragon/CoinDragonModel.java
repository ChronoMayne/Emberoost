package WolfShotz.Wyrmroost.client.render.entity.coin_dragon;

import WolfShotz.Wyrmroost.client.model.WREntityModel;
import WolfShotz.Wyrmroost.entities.dragon.CoinDragonEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * WRCoinDragon - Ukan
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class CoinDragonModel extends WREntityModel<CoinDragonEntity>
{
    public ModelRenderer body1;
    public ModelRenderer body2;
    public ModelRenderer armL;
    public ModelRenderer armR;
    public ModelRenderer wingL;
    public ModelRenderer wingR;
    public ModelRenderer neck1;
    public ModelRenderer coin;
    public ModelRenderer tail1;
    public ModelRenderer legL;
    public ModelRenderer legR;
    public ModelRenderer tail2;
    public ModelRenderer tail3;
    public ModelRenderer footL;
    public ModelRenderer footR;
    public ModelRenderer head;
    public ModelRenderer eyeL;
    public ModelRenderer eyeR;

    public CoinDragonModel()
    {
        textureWidth = 50;
        textureHeight = 15;
        body2 = new ModelRenderer(this, 11, 0);
        body2.setRotationPoint(0.0F, 0.0F, 0.0F);
        body2.addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, 0.1F, 0.1F, 0.0F);
        setRotateAngle(body2, -0.500909508638178F, 0.0F, 0.0F);
        neck1 = new ModelRenderer(this, 31, 0);
        neck1.setRotationPoint(0.0F, -0.3F, -1.3F);
        neck1.setTextureOffset(-1, 0).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(neck1, -0.27611108233918075F, 0.0F, 0.0F);
        wingR = new ModelRenderer(this, 17, 1);
        wingR.setRotationPoint(-0.7F, -0.7F, -0.8F);
        wingR.addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(wingR, 0.3186971254089062F, 0.0F, -0.956091342937205F);
        footR = new ModelRenderer(this, 6, 6);
        footR.setRotationPoint(0.0F, 2.0F, -2.0F);
        footR.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        body1 = new ModelRenderer(this, 0, 0);
        body1.setRotationPoint(0.0F, 20.0F, 0.0F);
        body1.addBox(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(body1, -0.4098033003787853F, 0.0F, 0.0F);
        legL = new ModelRenderer(this, 0, 6);
        legL.setRotationPoint(1.0F, -0.5F, 2.0F);
        legL.addBox(-0.5F, 0.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(legL, 0.9046041832665941F, 0.0F, 0.0F);
        head = new ModelRenderer(this, 36, 0);
        head.setRotationPoint(0.0F, 0.0F, -1.7F);
        head.addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(head, 0.7494443568379152F, 0.0F, 0.0F);
        eyeL = new ModelRenderer(this, 31, 4);
        eyeL.setRotationPoint(0.7F, -0.4F, -0.7F);
        eyeL.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(eyeL, 0.6829473549475088F, 0.3186971254089062F, 0.0F);
        armL = new ModelRenderer(this, 11, 6);
        armL.setRotationPoint(0.4F, 0.0F, -0.8F);
        armL.addBox(0.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(armL, 0.2275909337942703F, 0.0F, 0.0F);
        armR = new ModelRenderer(this, 11, 6);
        armR.mirror = true;
        armR.setRotationPoint(-0.4F, 0.0F, -0.8F);
        armR.addBox(-1.0F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(armR, 0.2275909337942703F, 0.0F, 0.0F);
        coin = new ModelRenderer(this, 32, 5);
        coin.mirror = true;
        coin.setRotationPoint(0.0F, 3.17F, 1.0F);
        coin.setTextureOffset(-2, 0).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 0.5F, 5.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(coin, -0.4185299466387569F, 3.141592653589793F, 0.0F);
        tail2 = new ModelRenderer(this, 22, 0);
        tail2.mirror = true;
        tail2.setRotationPoint(0.01F, 0.01F, 2.51F);
        tail2.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        wingL = new ModelRenderer(this, 17, 1);
        wingL.setRotationPoint(0.7F, -0.7F, -0.8F);
        wingL.addBox(0.0F, -3.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(wingL, 0.3186971254089062F, 0.0F, 0.956091342937205F);
        tail1 = new ModelRenderer(this, 22, 0);
        tail1.setRotationPoint(0.0F, -0.1F, 2.5F);
        tail1.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(tail1, 0.8529423868075012F, 0.0F, 0.0F);
        legR = new ModelRenderer(this, 0, 6);
        legR.mirror = true;
        legR.setRotationPoint(-1.0F, -0.5F, 2.0F);
        legR.addBox(-0.5F, 0.0F, -2.0F, 1.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(legR, 0.9046041832665941F, 0.0F, 0.0F);
        tail3 = new ModelRenderer(this, 22, 0);
        tail3.setRotationPoint(0.01F, 0.01F, 2.5F);
        tail3.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        footL = new ModelRenderer(this, 6, 6);
        footL.setRotationPoint(0.0F, 2.0F, -2.0F);
        footL.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        eyeR = new ModelRenderer(this, 31, 4);
        eyeR.mirror = true;
        eyeR.setRotationPoint(-0.7F, -0.4F, -0.7F);
        eyeR.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        setRotateAngle(eyeR, 0.6829473549475088F, -0.3186971254089062F, 0.0F);
        body1.addChild(body2);
        body1.addChild(neck1);
        body1.addChild(wingR);
        legR.addChild(footR);
        body2.addChild(legL);
        neck1.addChild(head);
        head.addChild(eyeL);
        body1.addChild(armL);
        body1.addChild(armR);
        body1.addChild(coin);
        tail1.addChild(tail2);
        body1.addChild(wingL);
        body2.addChild(tail1);
        body2.addChild(legR);
        tail2.addChild(tail3);
        legL.addChild(footL);
        head.addChild(eyeR);
    }

    @Override
    public void render(MatrixStack ms, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        body1.render(ms, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(CoinDragonEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {

    }
}