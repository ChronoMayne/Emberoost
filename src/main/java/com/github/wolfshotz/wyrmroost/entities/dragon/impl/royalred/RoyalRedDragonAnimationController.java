package com.github.wolfshotz.wyrmroost.entities.dragon.impl.royalred;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import java.util.ArrayList;
import java.util.List;

public class RoyalRedDragonAnimationController {

    private RoyalRedDragon dragon;

    public RoyalRedDragonAnimationController(RoyalRedDragon dragon) {
        this.dragon = dragon;
    }

    public List<AnimationController> get() {
        List<AnimationController> controllers = new ArrayList<>();
        controllers.add(new AnimationController(dragon, "controller", 0, this::getMainPredicate));
        controllers.add(new AnimationController(dragon, "controller2", 0, this::getSecondaryPredicate));
        return controllers;
    }

    private  <E extends IAnimatable> PlayState getMainPredicate(AnimationEvent<E> event) {
        if (dragon.isSleeping()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.sleep", true));
            return PlayState.CONTINUE;

        } else if (dragon.isInSittingPose()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.sit", true));
            return PlayState.CONTINUE;

        } else if (dragon.isFlying()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.flying", true));
            return PlayState.CONTINUE;

        } else if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.walk", true));
            return PlayState.CONTINUE;

        } else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.idle", true));
            return PlayState.CONTINUE;
        }
    }

    private  <E extends IAnimatable> PlayState getSecondaryPredicate(AnimationEvent<E> event) {

        AnimationController controller = dragon.getFactory().getOrCreateAnimationData(dragon.getId()).getAnimationControllers().get("controller");

        if (dragon.isFlying() && dragon.isBreathingFire()) {
            if (controller.getCurrentAnimation() == null || controller.getCurrentAnimation().animationName.equals("animation.royalred.flying")) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.roar", true));
                return PlayState.CONTINUE;
            }
        } else if (event.isMoving() && !dragon.isFlying() && dragon.isBreathingFire()) {
            if (controller.getCurrentAnimation() == null || controller.getCurrentAnimation().animationName.equals("animation.royalred.walk")) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.roar", true));
                return PlayState.CONTINUE;
            }
        } else if (dragon.isOnGround() && !dragon.isInSittingPose() && dragon.isBreathingFire()) {
            if (controller.getCurrentAnimation() == null || controller.getCurrentAnimation().animationName.equals("animation.royalred.idle")) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.roar", true));
                return PlayState.CONTINUE;
            }
        } else if (dragon.getEntityData().get(dragon.getSlapData())) {
            if (controller.getCurrentAnimation() == null || controller.getCurrentAnimation().animationName.equals("animation.royalred.walk")) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.slap", true));
                return PlayState.CONTINUE;
            }
        } else if (dragon.getEntityData().get(dragon.getSlapData())) {
            if (controller.getCurrentAnimation() == null || controller.getCurrentAnimation().animationName.equals("animation.royalred.idle")) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.slap", true));
                return PlayState.CONTINUE;
            }
        } else if (dragon.getEntityData().get(dragon.getSlapData())) {
            if (controller.getCurrentAnimation() == null || controller.getCurrentAnimation().animationName.equals("animation.royalred.flying")) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.royalred.slap", true));
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }


}
