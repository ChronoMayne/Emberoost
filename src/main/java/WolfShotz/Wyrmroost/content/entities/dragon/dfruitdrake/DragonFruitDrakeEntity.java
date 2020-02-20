package WolfShotz.Wyrmroost.content.entities.dragon.dfruitdrake;

import WolfShotz.Wyrmroost.content.entities.dragon.AbstractDragonEntity;
import WolfShotz.Wyrmroost.content.entities.dragonegg.DragonEggProperties;
import WolfShotz.Wyrmroost.content.world.WorldCapability;
import WolfShotz.Wyrmroost.registry.WRItems;
import WolfShotz.Wyrmroost.util.entityutils.ai.goals.CommonEntityGoals;
import WolfShotz.Wyrmroost.util.entityutils.ai.goals.DragonBreedGoal;
import WolfShotz.Wyrmroost.util.entityutils.ai.goals.DragonFollowOwnerGoal;
import WolfShotz.Wyrmroost.util.entityutils.client.animation.Animation;
import com.google.common.collect.Lists;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED;

@SuppressWarnings("deprecation")
public class DragonFruitDrakeEntity extends AbstractDragonEntity implements IShearable
{
    private int shearCooldownTime;
    
    public DragonFruitDrakeEntity(EntityType<? extends DragonFruitDrakeEntity> dragon, World world)
    {
        super(dragon, world);
        
        SLEEP_ANIMATION = new Animation(15);
        WAKE_ANIMATION = new Animation(15);
    }
    
    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(1, new SwimGoal(this));
        goalSelector.addGoal(3, sitGoal = new SitGoal(this));
        goalSelector.addGoal(4, new MeleeAttackGoal(this, 1d, false));
        goalSelector.addGoal(5, new DragonBreedGoal(this, false, true));
        goalSelector.addGoal(6, new DragonFollowOwnerGoal(this, 1.2d, 12d, 3d));
        goalSelector.addGoal(7, CommonEntityGoals.wanderAvoidWater(this, 1));
        goalSelector.addGoal(8, CommonEntityGoals.lookAt(this, 7f));
        goalSelector.addGoal(8, CommonEntityGoals.lookRandomly(this));
        
        targetSelector.addGoal(1, new HurtByTargetGoal(this).setCallsForHelp(DragonFruitDrakeEntity.class));
        targetSelector.addGoal(2, CommonEntityGoals.nonTamedTargetGoal(this, PlayerEntity.class, 2, true, true, EntityPredicates.CAN_AI_TARGET));
    }
    
    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.232524f);
        getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20d);
        getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4d);
    }
    
    // ================================
    //           Entity NBT
    // ================================
    @Override
    public void writeAdditional(CompoundNBT nbt)
    {
        super.writeAdditional(nbt);
        
        nbt.putInt("shearcooldown", shearCooldownTime);
    }
    
    @Override
    public void readAdditional(CompoundNBT nbt)
    {
        super.readAdditional(nbt);
        
        this.shearCooldownTime = nbt.getInt("shearcooldown");
    }
    // ================================

    @Override
    public void tick()
    {
        super.tick();

        if (shearCooldownTime > 0) --shearCooldownTime;
    }

    public static boolean canSpawnHere(EntityType type, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random)
    {
        World world = worldIn.getWorld();

        return world.getDimension() instanceof OverworldDimension && WorldCapability.isPortalTriggered(world);
    }

    @Override
    public void travel(Vec3d vec3d)
    {
        if (!isBeingRidden())
        {
            super.travel(vec3d);
            return;
        }
        
        // We're being ridden, follow rider controls
        LivingEntity rider = (LivingEntity) getControllingPassenger();
        if (canPassengerSteer())
        {
            float f = rider.moveForward, s = rider.moveStrafing;
            float speed = (float) (getAttribute(MOVEMENT_SPEED).getValue());
            boolean moving = (f != 0 || s != 0);
            Vec3d target = new Vec3d(s, vec3d.y, f);
            
            setAIMoveSpeed(speed / 2);
            super.travel(target);
            if (moving)
            {
                prevRotationYaw = rotationYaw = rider.rotationYaw;
                rotationPitch = rider.rotationPitch * 0.5f;
                setRotation(rotationYaw, rotationPitch);
                renderYawOffset = rotationYaw;
                rotationYawHead = renderYawOffset;
            }
        }
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand, ItemStack stack)
    {
        if (super.processInteract(player, hand, stack)) return true;

        if (!isChild() && !player.isSneaking())
        {
            setSit(false);
            player.startRiding(this);

            return true;
        }

        if (isOwner(player) && player.isSneaking())
        {
            setSit(!isSitting());

            return true;
        }

        return false;
    }

    @Override
    public boolean canBeSteered() { return true;}

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) { return sizeIn.height; }

    @Override
    public double getMountedYOffset() { return super.getMountedYOffset() + 0.1d; }
    
    @Override
    public boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos)
    {
        return shearCooldownTime <= 0;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IWorld world, BlockPos pos, int fortune)
    {
        playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1f, 1f);
        shearCooldownTime = 12000;
        return Lists.newArrayList(new ItemStack(WRItems.FOOD_DRAGON_FRUIT.get(), 1));
    }

    @Override // These bois are lazy, can sleep during the day
    public void handleSleep()
    {
        int sleepChance = world.isDaytime() ? 1000 : 300;
        if (!isSleeping()
                && --sleepCooldown <= 0
                && (!isTamed() || isSitting() || (getHomePos().isPresent() && isWithinHomeDistanceFromPosition()))
                && !isBeingRidden()
                && getAttackTarget() == null
                && getNavigator().noPath()
                && !isAngry()
                && !isInWaterOrBubbleColumn()
                && !isFlying()
                && getRNG().nextInt(sleepChance) == 0) setSleeping(true);
        else if (isSleeping() && world.isDaytime() && getRNG().nextInt(375) == 0) setSleeping(false);
    }
    
    @Override
    public boolean canFly()
    {
        return false;
    }
    
    @Override
    public List<Item> getFoodItems()
    {
        List<Item> foods = Tags.Items.CROPS.getAllElements().stream().filter(i -> i.getItem() != Items.NETHER_WART).collect(Collectors.toList());
        Collections.addAll(foods, WRItems.FOOD_DRAGON_FRUIT.get(), Items.APPLE, Items.SWEET_BERRIES);
        return foods;
    }
    
    @Override
    public DragonEggProperties createEggProperties()
    {
        return new DragonEggProperties(0.45f, 0.75f, 9600);
    }
    
    @Override
    public Animation[] getAnimations()
    {
        return new Animation[]{NO_ANIMATION, SLEEP_ANIMATION, WAKE_ANIMATION};
    }
}