package net.lixis9.eventjar.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import javax.annotation.Nonnull;

public class OverdoseMobEffect extends MobEffect {
    public OverdoseMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B0000); // Темно-красный цвет
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
        // Наносим урон при каждой "тактовой" обработке
        if (!entity.level().isClientSide()) {
            entity.hurt(entity.damageSources().magic(), 1.0F);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Каждые 2 секунды (40 тиков)
        return duration % 40 == 0;
    }
}