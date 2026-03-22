package net.lixis9.eventjar.potion;

import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.lixis9.eventjar.procedures.MobFollowTaskProcedure;

public class ConscienceMobEffect extends MobEffect {
    public ConscienceMobEffect() {
        super(MobEffectCategory.HARMFUL, -16777216);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        if (entity instanceof Player && !entity.level().isClientSide()) {
            // Запускаем задачу следования мобов за игроком
            MobFollowTaskProcedure.startFollowTask((Player) entity);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        // Останавливаем задачу следования мобов только на серверной стороне
        if (!entity.level().isClientSide()) {
            MobFollowTaskProcedure.stopFollowTask();
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
