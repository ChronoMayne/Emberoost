package com.github.wolfshotz.wyrmroost.entities.dragon.impl.stalker.roost;

import com.github.wolfshotz.wyrmroost.client.screen.DragonControlScreen;
import com.github.wolfshotz.wyrmroost.containers.BookContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.DefendHomeGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.DragonBreedGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.MoveToHomeGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.WRFollowOwnerGoal;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.stalker.roost.goals.RoostStalkerScavengeGoal;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializer;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializerBuilder;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializerType;
import com.github.wolfshotz.wyrmroost.entities.util.data.DataParemeterType;
import com.github.wolfshotz.wyrmroost.items.book.action.BookActions;
import com.github.wolfshotz.wyrmroost.network.packets.AddPassengerPacket;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import javax.annotation.Nullable;

import static net.minecraft.entity.ai.attributes.Attributes.*;

public class RoostStalker extends TameableDragonEntity {

    private DataParameter<ItemStack> itemData;
    private DataParameter<Boolean> scavengingData;

    public RoostStalker(EntityType<? extends RoostStalker> stalker, World level) {
        super(stalker, level);
        maxUpStep = 0;
    }

    @Override
    public EntitySerializer<RoostStalker> getSerializer() {
        return EntitySerializerBuilder.getEntitySerializer(this.getClass(), EntitySerializerType.SLEEPING, EntitySerializerType.VARIANT);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(getSleepingData(), false);
        entityData.define(getVariantData(), 0);
        entityData.define(getItemData(), ItemStack.EMPTY);
        entityData.define(getScavengingData(), false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(5, new MoveToHomeGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(9, new RoostStalkerScavengeGoal(this, 1.1d));
        goalSelector.addGoal(10, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(11, new LookAtGoal(this, LivingEntity.class, 5f));
        goalSelector.addGoal(12, new LookRandomlyGoal(this));
        goalSelector.addGoal(8, new AvoidEntityGoal<PlayerEntity>(this, PlayerEntity.class, 7f, 1.15f, 1f) {
            @Override
            public boolean canUse() {
                return !isTame() && !getItem().isEmpty() && super.canUse();
            }
        });

        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(5, new NonTamedTargetGoal<>(this, LivingEntity.class, true, target -> target instanceof ChickenEntity || target instanceof RabbitEntity || target instanceof TurtleEntity));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        sleepTimer.add(isSleeping() ? 0.08f : -0.15f);

        if (!level.isClientSide) {
            ItemStack item = getStackInSlot(defaultItemSlot);
            if (isFood(item) && getHealth() < getMaxHealth() && getRandom().nextDouble() <= 0.0075)
                eat(item);
        }
    }

    @Override
    public ActionResultType playerInteraction(PlayerEntity player, Hand hand, ItemStack stack) {
        final ActionResultType success = ActionResultType.sidedSuccess(level.isClientSide);

        ItemStack heldItem = getItem();
        Item item = stack.getItem();

        if (!isTame() && Tags.Items.EGGS.contains(item)) {
            eat(stack);
            if (tame(getRandom().nextDouble() < 0.25, player)) getAttribute(MAX_HEALTH).setBaseValue(20d);

            return success;
        }

        if (isTame() && isBreedingItem(stack)) {
            if (!level.isClientSide && canFallInLove() && getAge() == 0) {
                setInLove(player);
                stack.shrink(1);
                return ActionResultType.SUCCESS;
            }

            return ActionResultType.CONSUME;
        }

        if (isOwnedBy(player)) {
            if (player.isShiftKeyDown()) {
                setOrderedToSit(!isInSittingPose());
                return success;
            }

            if (stack.isEmpty() && heldItem.isEmpty() && !isLeashed() && player.getPassengers().size() < 3) {
                if (!level.isClientSide && startRiding(player, true)) {
                    setOrderedToSit(false);
                    AddPassengerPacket.send(this, player);
                }

                return success;
            }

            if ((!stack.isEmpty() && !isFood(stack)) || !heldItem.isEmpty()) {
                setStackInSlot(defaultItemSlot, stack);
                player.setItemInHand(hand, heldItem);

                return success;
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public void doSpecialEffects() {
        if (getVariant() == -1 && tickCount % 25 == 0) {
            double x = getX() + (Mafs.nextDouble(getRandom()) * 0.7d);
            double y = getY() + (getRandom().nextDouble() * 0.5d);
            double z = getZ() + (Mafs.nextDouble(getRandom()) * 0.7d);
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05f, 0);
        }
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad) {
        if (slot == defaultItemSlot) setItem(stack);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slot) {
        return slot == EquipmentSlotType.MAINHAND ? getItem() : super.getItemBySlot(slot);
    }

    @Override
    public void applyStaffInfo(BookContainer container) {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), defaultItemSlot, 0, 0, -15, DragonControlScreen.SADDLE_UV))
                .addAction(BookActions.TARGET);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.DROWN || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.GOLD_NUGGET;
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        return getType().getDimensions().scale(getScale());
    }

    @Override
    public int determineVariant() {
        return getRandom().nextDouble() < 0.005 ? -1 : 0;
    }

    @Override
    // Override normal dragon body controller to allow rotations while sitting: its small enough for it, why not. :P
    protected BodyController createBodyControl() {
        return new BodyController(this);
    }

    public ItemStack getItem() {
        return entityData.get(getItemData());
    }

    public boolean hasItem() {
        return getItem() != ItemStack.EMPTY;
    }

    public void setItem(ItemStack item) {
        entityData.set(getItemData(), item);
        if (!item.isEmpty()) playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 0.5f, 1);
    }

    public boolean isScavenging() {
        return entityData.get(getScavengingData());
    }

    public void setScavenging(boolean b) {
        entityData.set(getScavengingData(), b);
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public boolean defendsHome() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return WRSounds.ENTITY_STALKER_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return WRSounds.ENTITY_STALKER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return WRSounds.ENTITY_STALKER_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 0.8f;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFood(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat();
    }

    @Override
    public DragonInventory createInv() {
        return new DragonInventory(this, 1);
    }

    public DataParameter<ItemStack> getItemData() {
        if (itemData == null) {
            itemData = DataParemeterType.get(DataParemeterType.ROOST_STALKER_ITEM);
        }
        return itemData;
    }

    public DataParameter<Boolean> getScavengingData() {
        if (scavengingData == null) {
            scavengingData = DataParemeterType.get(DataParemeterType.ROOST_STALKER_SCAVENGING);
        }
        return scavengingData;
    }

    public static void setSpawnBiomes(BiomeLoadingEvent event) {
        Biome.Category category = event.getCategory();
        if (category == Biome.Category.PLAINS || category == Biome.Category.FOREST || category == Biome.Category.EXTREME_HILLS)
            event.getSpawns().addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(WREntities.ROOSTSTALKER.get(), 7, 2, 9));
    }

    public static AttributeModifierMap.MutableAttribute getAttributeMap() {
        return MobEntity.createMobAttributes()
                .add(MAX_HEALTH, 8)
                .add(MOVEMENT_SPEED, 0.285)
                .add(ATTACK_DAMAGE, 2);
    }

}