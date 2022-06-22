package com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.screen.DragonControlScreen;
import com.github.wolfshotz.wyrmroost.client.sound.BreathSound;
import com.github.wolfshotz.wyrmroost.containers.BookContainer;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.DragonInventory;
import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals.*;
import com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred.goals.RedRoyalDragonAttackGoal;
import com.github.wolfshotz.wyrmroost.entities.projectile.breath.FireBreathEntity;
import com.github.wolfshotz.wyrmroost.entities.util.EntityConstants;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializer;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializerBuilder;
import com.github.wolfshotz.wyrmroost.entities.util.EntitySerializerType;
import com.github.wolfshotz.wyrmroost.items.DragonArmorItem;
import com.github.wolfshotz.wyrmroost.items.book.action.BookActions;
import com.github.wolfshotz.wyrmroost.network.packets.KeybindHandler;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRSounds;
import com.github.wolfshotz.wyrmroost.util.LerpedFloat;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.apache.commons.lang3.ArrayUtils;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

import static com.github.wolfshotz.wyrmroost.entities.util.EntityConstants.ARMOR;
import static com.github.wolfshotz.wyrmroost.entities.util.EntityConstants.*;
import static net.minecraft.entity.ai.attributes.Attributes.*;


public class RoyalRedDragon extends TameableDragonEntity implements IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this);

    public static final int ARMOR_SLOT = 0;
    private static final int MAX_KNOCKOUT_TIME = 3600; // 3 minutes


    public static final DataParameter<Boolean> BREATHING_FIRE = EntityDataManager.defineId(RoyalRedDragon.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> KNOCKED_OUT = EntityDataManager.defineId(RoyalRedDragon.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> SLAP = EntityDataManager.defineId(RoyalRedDragon.class, DataSerializers.BOOLEAN);

    public final LerpedFloat flightTimer = LerpedFloat.unit();
    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public final LerpedFloat breathTimer = LerpedFloat.unit();
    public final LerpedFloat knockOutTimer = LerpedFloat.unit();
    private int knockOutTime = 0;
    private final RoyalRedDragonAttacks attacks;
    private final RoyalRedDragonAnimationController animationController;

    public RoyalRedDragon(EntityType<? extends TameableDragonEntity> dragon, World level) {
        super(dragon, level);
        noCulling = WRConfig.NO_CULLING.get();

        setPathfindingMalus(PathNodeType.DANGER_FIRE, 0);
        setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0);

        this.attacks = new RoyalRedDragonAttacks(this);
        this.animationController = new RoyalRedDragonAnimationController(this);
    }

    @Override
    public EntitySerializer<RoyalRedDragon> getSerializer() {
        return EntitySerializerBuilder.getEntitySerializer(this.getClass(),EntitySerializerType.SLEEPING, EntitySerializerType.VARIANT, EntitySerializerType.GENDER, EntitySerializerType.KNOCK_OUT_TIME);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        entityData.define(GENDER, false);
        entityData.define(SLEEPING, false);
        entityData.define(VARIANT, 0);
        entityData.define(BREATHING_FIRE, false);
        entityData.define(KNOCKED_OUT, false);
        entityData.define(FLYING, false);
        entityData.define(SLAP, false);
        entityData.define(ARMOR, ItemStack.EMPTY);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(4, new MoveToHomeGoal(this));
        if (isFlying()) {
            goalSelector.addGoal(5, new RedRoyalDragonAttackGoal(this));
        }
        //Does attack
        goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.1d, true));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new DragonBreedGoal(this));
        goalSelector.addGoal(9, new FlyerWanderGoal(this, 1));
        goalSelector.addGoal(10, new LookAtGoal(this, LivingEntity.class, 10f));
        goalSelector.addGoal(11, new LookRandomlyGoal(this));


        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new DefendHomeGoal(this));
        targetSelector.addGoal(4, new HurtByTargetGoal(this));
        targetSelector.addGoal(5, new NonTamedTargetGoal<>(this, LivingEntity.class, false, e -> e.getType() == EntityType.PLAYER || e instanceof AnimalEntity));
    }

    @Override
    public DragonInventory createInv() {
        return new DragonInventory(this, 1);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        flightTimer.add(isFlying() ? 0.1f : -0.085f);
        sitTimer.add(isInSittingPose() ? 0.075f : -0.1f);
        sleepTimer.add(isSleeping() ? 0.035f : -0.05f);
        breathTimer.add(isBreathingFire() ? 0.15f : -0.2f);
        knockOutTimer.add(isKnockedOut() ? 0.05f : -0.1f);

        if (!level.isClientSide) {
            if (isBreathingFire() && getControllingPlayer() == null && getTarget() == null)
                setBreathingFire(false);

            if (breathTimer.get() == 1) level.addFreshEntity(new FireBreathEntity(this));
            //Roar Animation Trigger
            /*
            if (noAnimations() && !isKnockedOut() && !isSleeping() && !isBreathingFire() && isJuvenile() && getRandom().nextDouble() < 0.0004)
                AnimationPacket.send(this, ROAR_ANIMATION);
*/


            if (isKnockedOut() && --knockOutTime <= 0) setKnockedOut(false);
        }
    }

    @Override
    public ActionResultType playerInteraction(PlayerEntity player, Hand hand, ItemStack stack) {
        if (!isTame() && isFood(stack)) {
            if (isHatchling() || player.isCreative()) {
                eat(stack);
                tame(getRandom().nextDouble() < 0.1, player);
                setKnockedOut(false);
                return ActionResultType.sidedSuccess(level.isClientSide);
            }

            if (isKnockedOut() && knockOutTime <= MAX_KNOCKOUT_TIME / 2) {
                if (!level.isClientSide) {
                    /* Roar Animation Trigger
                    // base taming chances on consciousness; the closer it is to waking up the better the chances
                    if (tame(getRandom().nextInt(knockOutTime) < MAX_KNOCKOUT_TIME * 0.2d, player))
                    {
                        setKnockedOut(false);
                        AnimationPacket.send(this, ROAR_ANIMATION);
                    }
                    else knockOutTime += 600; // add 30 seconds to knockout time
                    eat(stack);
                    player.swing(hand);
                    return ActionResultType.SUCCESS;
                    */

                } else return ActionResultType.CONSUME;
            }
        }

        return super.playerInteraction(player, hand, stack);
    }

    //If entity is attacking, (in this case hand is the default, then running the animation when it swings its "hand" will run the animation normally)
    @Override
    public void swing(Hand hand) {
        entityData.set(SLAP, true);
        playSound(SoundEvents.GENERIC_EAT, 1, 1, true);
        super.swing(hand);
    }

    @Override
    public void die(DamageSource cause) {
        if (isTame() || isKnockedOut() || cause.getEntity() == null)
            super.die(cause);
        else // knockout RR's instead of killing them
        {
            setHealth(getMaxHealth() * 0.25f); // reset to 25% health
            setKnockedOut(true);
        }
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> key) {
        if (level.isClientSide && key.equals(BREATHING_FIRE) && isBreathingFire())
            BreathSound.play(this);
        else super.onSyncedDataUpdated(key);
    }

    @Override
    public void onInvContentsChanged(int slot, ItemStack stack, boolean onLoad) {
        if (slot == ARMOR_SLOT) setArmor(stack);
    }

    @Override
    public void recievePassengerKeybind(int key, int mods, boolean pressed) {
        if (!noAnimations()) return;
/* Roar Animation Trigger
        if (key == KeybindHandler.MOUNT_KEY && pressed && !isBreathingFire())
        {
            if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) setAnimation(ROAR_ANIMATION);
            else meleeAttack();
        }
*/
        if (key == KeybindHandler.ALT_MOUNT_KEY && canBreatheFire()) setBreathingFire(pressed);
    }

    public boolean canBreatheFire() {
        return ageProgress() > 0.75f;
    }

    @Override
    public Vector3d getApproximateMouthPos() {
        Vector3d rotVector = calculateViewVector(xRot * 0.65f, yHeadRot);
        Vector3d position = getEyePosition(1).subtract(0, 0.9, 0);
        position = position.add(rotVector.scale(getBbWidth() + 1.3));
        return position;
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        EntitySize size = getType().getDimensions().scale(getScale());
        float heightFactor = isSleeping() ? 0.5f : isInSittingPose() ? 0.9f : 1;
        return size.scale(1, heightFactor);
    }

    @Override
    public void applyStaffInfo(BookContainer container) {
        super.applyStaffInfo(container);

        container.slot(BookContainer.accessorySlot(getInventory(), ARMOR_SLOT, 0, -15, -15, DragonControlScreen.ARMOR_UV).only(DragonArmorItem.class))
                .addAction(BookActions.TARGET);
    }

    @Override
    public void setMountCameraAngles(boolean backView, EntityViewRenderEvent.CameraSetup event) {
        if (backView)
            event.getInfo().move(ClientEvents.getViewCollision(-8.5, this), 0, 0);
        else
            event.getInfo().move(ClientEvents.getViewCollision(-5, this), -0.75, 0);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || super.isInvulnerableTo(source);
    }

    @Override
    public int determineVariant() {
        return getRandom().nextDouble() < 0.03 ? -1 : 0;
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile() || isKnockedOut();
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return getBbHeight() + 0.5f;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return isTame() && isJuvenile() && !isKnockedOut() && getPassengers().size() < 3;
    }

    @Override
    public Vector3d getPassengerPosOffset(Entity entity, int index) {
        return new Vector3d(0, getBbHeight() * 0.85f, index == 0 ? 0.5f : -1);
    }

    @Override
    public float getScale() {
        float i = getAgeScale(0.3f);
        if (isMale()) i *= 0.8f;
        return i;
    }

    @Override
    public int getYawRotationSpeed() {
        return isFlying() ? 5 : 7;
    }

    public boolean isBreathingFire() {
        return entityData.get(BREATHING_FIRE);
    }

    public void setBreathingFire(boolean b) {
        if (!level.isClientSide) entityData.set(BREATHING_FIRE, b);
    }

    public boolean isKnockedOut() {
        return entityData.get(KNOCKED_OUT);
    }

    public void setKnockedOut(boolean b) {
        entityData.set(KNOCKED_OUT, b);
        if (!level.isClientSide) {
            knockOutTime = b ? MAX_KNOCKOUT_TIME : 0;
            if (b) {
                xRot = 0;
                clearAI();
                setFlying(false);
            }
        }
    }

    //Get Knocked out Time
    public int getKnockOutTime() {
        return knockOutTime;
    }

    //Set Knock out time
    public void setKnockoutTime(int i) {
        knockOutTime = Math.max(0, i);
        if (i > 0 && !isKnockedOut()) entityData.set(KNOCKED_OUT, true);
    }

    //Has fall damage unless dragon is knocked out
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        if (isKnockedOut()) return false;
        return super.causeFallDamage(distance, damageMultiplier);
    }

    //Makes sure Dragon is not knocked out and is able to fly
    @Override
    public boolean canFly() {
        return super.canFly() && !isKnockedOut();
    }

    @Override
    public boolean isImmuneToArrows() {
        return true;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isFood(ItemStack stack) {
        return stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat();
    }

    @Override
    public boolean shouldSleep() {
        return !isKnockedOut() && super.shouldSleep();
    }

    @Override
    public boolean defendsHome() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return WRSounds.ENTITY_ROYALRED_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return WRSounds.ENTITY_ROYALRED_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return WRSounds.ENTITY_ROYALRED_DEATH.get();
    }

    @Override
    public float getSoundVolume() {
        return 1.5f * getScale();
    }

    @Override
    public Attribute[] getScaledAttributes() {
        return ArrayUtils.addAll(super.getScaledAttributes(), ATTACK_KNOCKBACK);
    }

    public static AttributeModifierMap.MutableAttribute getAttributeMap() {
        // base male attributes
        return MobEntity.createMobAttributes()
                .add(MAX_HEALTH, 130)
                .add(MOVEMENT_SPEED, 0.22)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(FOLLOW_RANGE, 60)
                .add(ATTACK_KNOCKBACK, 4)
                .add(ATTACK_DAMAGE, 12)
                .add(FLYING_SPEED, 0.121)
                .add(WREntities.Attributes.PROJECTILE_DAMAGE.get(), 4);
    }

    //Gecko Lib Information
    @Override
    public void registerControllers(AnimationData data) {
        animationController.get().forEach(data::addAnimationController);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public RoyalRedDragonAttacks getAttacks() {
        return attacks;
    }
}
