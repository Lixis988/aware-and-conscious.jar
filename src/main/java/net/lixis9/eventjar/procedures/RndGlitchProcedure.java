package net.lixis9.eventjar.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.api.distmarker.Dist; // Добавлен импорт

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = "eventjar", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RndGlitchProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event);
		}
	}

	public static void execute() {
		execute(null);
	}

	private static void execute(@Nullable Object event) {
		if (Math.random() < (1f / 200000f)) {
			GlitchEffectProcedure.execute();
		}
	}
}
