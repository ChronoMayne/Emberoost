package com.github.wolfshotz.wyrmroost.entities.dragon.impl.leviathan.butterfly;

import com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.LessShitLookController;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

public class ButterflyLeviathanMovementController extends MovementController {

    private ButterflyLeviathan leviathan;

    public ButterflyLeviathanMovementController(ButterflyLeviathan leviathan) {
        super(leviathan);
        this.leviathan = leviathan;
    }

    public void tick() {
        if (operation == Action.MOVE_TO && !leviathan.canBeControlledByRider()) {
            operation = Action.WAIT;
            double x = wantedX - leviathan.getX();
            double y = wantedY - leviathan.getY();
            double z = wantedZ - leviathan.getZ();
            double distSq = x * x + y * y + z * z;
            if (distSq < 2.5000003E-7) leviathan.setSpeed(0f); // why move...
            else {
                float newYaw = (float) Math.toDegrees(MathHelper.atan2(z, x)) - 90f;
                float pitch = -((float) (MathHelper.atan2(y, MathHelper.sqrt(x * x + z * z)) * 180 / Math.PI));
                pitch = MathHelper.clamp(MathHelper.wrapDegrees(pitch), -85f, 85f);

                leviathan.yHeadRot = newYaw;
                leviathan.yBodyRot = leviathan.yRot = rotlerp(leviathan.yRot, leviathan.yHeadRot, leviathan.getYawRotationSpeed());
                pitch = rotlerp(pitch, pitch, 75);
                ((LessShitLookController) leviathan.getLookControl()).stopLooking();
                float speed = leviathan.isInWater() ? (float) leviathan.getAttributeValue(ForgeMod.SWIM_SPEED.get()) : (float) leviathan.getAttributeValue(MOVEMENT_SPEED);
                leviathan.setSpeed(speed);
                if (leviathan.isInWater()) {
                    leviathan.zza = MathHelper.cos(pitch * (Mafs.PI / 180f)) * speed;
                    leviathan.yya = -MathHelper.sin(pitch * (Mafs.PI / 180f)) * speed;
                }
            }
        } else {
            leviathan.setSpeed(0);
            leviathan.setZza(0);
            leviathan.setYya(0);
        }
    }
}