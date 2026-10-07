package net.lixis9.eventjar.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import javax.annotation.Nonnull;

public class OverdoseMobEffect extends MobEffect {
    public OverdoseMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {

        if (!entity.level().isClientSide()) {
            entity.hurt(entity.damageSources().magic(), 1.0F);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {

        return duration % 40 == 0;
    }
}
