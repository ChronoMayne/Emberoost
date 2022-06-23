package com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyvern.canari;

import com.github.wolfshotz.wyrmroost.client.model.entity.CanariWyvernModel;
import com.github.wolfshotz.wyrmroost.containers.BookContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyvern.canari.goals.CanariWyvernAttackGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.wyvern.canari.goals.CanariWyvernThreatenGoal;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializer;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializerBuilder;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializerType;
import com.github.wolfshotz.wyrmroost.items.book.action.BookActions;
import com.github.wolfshotz.wyrmroost.network.packets.AnimationPacket;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.animation.Animation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.entity.ai.attributes.Attributes.*;
import static com.github.wolfshotz.wyrmroost.entities.util.EntityConstants.*;

public class CanariWyvern extends TameableDragonEntity {

    public PlayerEntity pissedOffTarget;

    public CanariWyvern(EntityType<? extends TameableDragonEntity> dragon, World level) {
        super(dragon, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(3, new MoveToHomeGoal(this));
        goalSelector.addGoal(4, new CanariWyvernAttackGoal(this));
        goalSelector.addGoal(5, new CanariWyvernThreatenGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(8, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(9, new LookAtGoal(this, LivingEntity.class, 8f));
        goalSelector.addGoal(10, new LookRandomlyGoal(this));

        targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(2, new DefendHomeGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    protected BodyController createBodyControl() {
        return new BodyController(this);
    }

    @Override
    public EntitySerializer<CanariWyvern> getSerializer() {
        return EntitySerializerBuilder.getEntitySerializer(this.getClass(),EntitySerializerType.SLEEPING, EntitySerializerType.VARIANT, EntitySerializerType.GENDER);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(flyingData, false);
        entityData.define(genderData, false);
        entityData.define(sleepingData, false);
        entityData.define(variantData, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!level.isClientSide && !isPissed() && !isSleeping() && !isFlying() && !isRiding() && noAnimations()) {
            double rand = getRandom().nextDouble();
            if (rand < 0.001) AnimationPacket.send(this, CANARI_WYVERN_FLAP_WINGS_ANIMATION);
            else if (rand < 0.002) AnimationPacket.send(this, CANARI_WYVERN_PREEN_ANIMATION);
        }
    }

    public void flapWingsAnimation(int time) {
        if (time == 5 || time == 12) playSound(SoundEvents.PHANTOM_FLAP, 0.7f, 2, true);
        if (!level.isClientSide && time == 9 && getRandom().nextDouble() <= 0.25)
            spawnAtLocation(new ItemStack(Items.FEATHER), 0.5f);
    }

    public void threatAnimation(int time) {
        if (isPissed())
            yRot = yBodyRot = yHeadRot = (float) Mafs.getAngle(CanariWyvern.this, pissedOffTarget) - 270f;
    }

    @Override
    public ActionResultType playerInteraction(PlayerEntity player, Hand hand, ItemStack stack) {
        ActionResultType result = super.playerInteraction(player, hand, stack);
        if (result.consumesAction()) return result;

        if (!isTame() && isFood(stack) && (isPissed() || player.isCreative() || isHatchling())) {
            eat(stack);
            if (!level.isClientSide) tame(getRandom().nextDouble() < 0.2, player);
            return ActionResultType.sidedSuccess(level.isClientSide);
        }

        if (isOwnedBy(player) && player.getPassengers().size() < 3 && !player.isShiftKeyDown() && !isLeashed()) {
            setOrderedToSit(true);
            setFlying(false);
            clearAI();
            startRiding(player, true);
            return ActionResultType.sidedSuccess(level.isClientSide);
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity) && entity instanceof LivingEntity) {
            int i = 5;
            switch (level.getDifficulty()) {
                case HARD:
                    i = 15;
                    break;
                case NORMAL:
                    i = 8;
                    break;
                default:
                    break;
            }
            ((LivingEntity) entity).addEffect(new EffectInstance(Effects.POISON, i * 20));
            return true;
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.MAGIC || super.isInvulnerableTo(source);
    }

    @Override
    public void applyStaffInfo(BookContainer container) {
        super.applyStaffInfo(container);
        container.addAction(BookActions.TARGET);
    }

    @Override
    public boolean shouldSleep() {
        return !isPissed() && super.shouldSleep();
    }

    @Override
    public boolean defendsHome() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return WRSounds.ENTITY_CANARI_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return WRSounds.ENTITY_CANARI_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return WRSounds.ENTITY_CANARI_DEATH.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{NO_ANIMATION, CANARI_WYVERN_FLAP_WINGS_ANIMATION, CANARI_WYVERN_PREEN_ANIMATION, CANARI_WYVERN_THREAT_ANIMATION, CANARI_WYVERN_ATTACK_ANIMATION};
    }

    @Override
    public int determineVariant() {
        return getRandom().nextInt(5);
    }

    @Override
    public int getYawRotationSpeed() {
        return isFlying() ? 12 : 75;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.SWEET_BERRIES;
    }

    public boolean isPissed() {
        return pissedOffTarget != null;
    }

    public static AttributeModifierMap.MutableAttribute getAttributeMap() {
        return MobEntity.createMobAttributes()
                .add(MAX_HEALTH, 12)
                .add(MOVEMENT_SPEED, 0.2)
                .add(FLYING_SPEED, 0.1)
                .add(ATTACK_DAMAGE, 3);
    }
}